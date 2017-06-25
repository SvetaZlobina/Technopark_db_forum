package api.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.ForumDAO;
import api.daoFiles.PostDAO;
import api.models.*;
import api.daoFiles.UserDAO;
import api.models.Thread;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PostController extends ThreadRelatedHelper {
    @Autowired
    PostDAO postDAO;
    @Autowired
    ForumDAO forumDAO;
    @Autowired
    UserDAO userDAO;

    @GetMapping(value = "api/thread/{slug_or_id}/posts")
    public ObjectNode getPostList(@PathVariable("slug_or_id") String slugOrId,
                                  @RequestParam(value = "limit", defaultValue = "0") Integer limit,
                                  @RequestParam(value = "marker", defaultValue = "0") Integer marker,
                                  @RequestParam(value = "sort", defaultValue = "flat") String sort,
                                  @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                  HttpServletResponse response) {
        final Integer dbThreadId = getIdBySlugOrId(slugOrId);
        if (dbThreadId == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final List<Post> postList = postDAO.getPostList(dbThreadId, limit, desc, sort, marker);
        final Integer newMarker = postDAO.getNewPostOffset(marker, sort, postList);

        return new PostPage(postList, Integer.toString(newMarker)).toJsonNode();
    }

    @GetMapping(value = "api/post/{id}/details")
    public ObjectNode getPostDetailData(@PathVariable("id") Integer id,
                                        @RequestParam(value = "related", defaultValue = "") String related,
                                        HttpServletResponse response) {
        final Post dbPost;
        try {
            dbPost = postDAO.extractPostById(id);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final PostDetail postDetailData = new PostDetail();
        postDetailData.setPost(dbPost);

        final String[] relatedData = related.split(",");

        for (String option : relatedData) {
            if (option.equals("user")) {
                postDetailData.setAuthor(userDAO.getUser(dbPost.getAuthor()));

            } else if (option.equals("forum")) {
                postDetailData.setForum(forumDAO.readForum(dbPost.getForum()));

            } else if (option.equals("thread")) {
                postDetailData.setThread(threadDAO.getThreadById(dbPost.getThread()));
            }
        }

        return postDetailData.toJsonNode();
    }

    @PostMapping(value = "api/post/{id}/details")
    public Post updatePost(
            @PathVariable("id") Integer postId,
            @RequestBody PostUpdate postUpdate,
            HttpServletResponse response
    ) {
        final Post dbPost;

        try {
            dbPost = postDAO.extractPostById(postId);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final String dbMessage = dbPost.getMessage();
        final String updateMessage = postUpdate.getMessage();
        if (dbMessage.equals(updateMessage)) {
            return dbPost;
        }

        postDAO.updatePost(postId, postUpdate);
        return postDAO.extractPostById(postId);
    }

    @PostMapping(value = "api/thread/{slug_or_id}/create")
    public List<Post> createPostBatch(
            @PathVariable("slug_or_id") String slugOrId,
            @RequestBody List<Post> postList,
            HttpServletResponse response
    ) {
        final Thread dbThread = getThreadBySlugOrId(slugOrId);
        if (dbThread == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (!validUsers(postList)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (!validParents(postList, dbThread.getId())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }

        return innerCreatePosts(dbThread, postList, response);
    }

    @Transactional
    private List<Post> innerCreatePosts(
            Thread thread,
            List<Post> postList,
            HttpServletResponse response
    ) {
        addPostAuthors(postList, thread);
        updatePostList(postList, thread);

        final List<Integer> postIdList;
        try {
            postIdList = postDAO.createPosts(postList);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        for (int i = 0; i != postList.size(); ++i) {
            postList.get(i).setId(postIdList.get(i));
        }

        forumDAO.increasePostCount(thread.getForum(), postList.size());
        response.setStatus(HttpServletResponse.SC_CREATED);

        return postList;
    }

    private void addPostAuthors(List<Post> postList, Thread thread) {
        final List<String> usersToAdd = new ArrayList<>();

        for (int i = 0; i != postList.size(); ++i) {
            final String author = postList.get(i).getAuthor();
            if (!usersToAdd.contains(author)) {
                usersToAdd.add(author);
            }
        }

        usersToAdd.sort(String::compareTo);
        forumDAO.addUserBatch(thread.getForum(), usersToAdd);
    }

    private void updatePostList(List<Post> postList, Thread thread) {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final String forumSlug = thread.getForum();
        final Integer threadId = thread.getId();

        for (int i = 0; i != postList.size(); ++i) {
            postList.get(i).setForum(forumSlug);
            postList.get(i).setThread(threadId);
            postList.get(i).setCreated(timestamp);
        }
    }

    private boolean validParents(List<Post> postList, Integer threadId) {
        final List<Integer> parentIdList = new ArrayList<>();
        for (int i = 0; i != postList.size(); ++i) {
            final Integer parentId = postList.get(i).getParent();

            if (parentId != null && !parentId.equals(0)) {
                if (!parentIdList.contains(parentId)) {
                    parentIdList.add(parentId);
                }
            }
        }
        return parentIdList.isEmpty() || postDAO.checkPostsPresence(parentIdList, threadId);
    }

    private boolean validUsers(List<Post> postList) {
        final List<String> userNameList = new ArrayList<>();
        for (int i = 0; i != postList.size(); ++i) {
            userNameList.add(postList.get(i).getAuthor());
        }

        return userDAO.checkUsersPresence(userNameList);
    }
}
