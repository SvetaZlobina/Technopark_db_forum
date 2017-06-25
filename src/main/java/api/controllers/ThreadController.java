package api.controllers;


import api.daoFiles.DaoForum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.DaoPost;
import api.models.*;
import api.daoFiles.DaoUser;
import api.models.ThreadModel;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping(path = "api/thread/{slug_or_id}")
public class ThreadController extends ServiceForController {

    @Autowired
    private DaoUser daoUser;

    @Autowired
    private DaoPost daoPost;

    @Autowired
    private DaoForum daoForum;

    private static final Logger log = Logger.getLogger(ThreadController.class.getName());


    @GetMapping(path = "/details")
    public ThreadModel getThreadDetails(@PathVariable("slug_or_id") final String slugOrId,
                                        final HttpServletResponse response
    ) {

        ThreadModel dbThreadModel = getThreadBySlug(slugOrId);
        if (dbThreadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return dbThreadModel;
    }


    @PostMapping(path = "/details")
    public ThreadModel updateThread(@PathVariable("slug_or_id") final String slugOrId,
                                    @RequestBody final ThreadUpdateModel threadUpdateModel,
                                    final HttpServletResponse response
    ) {

        ThreadModel dbThreadModel = getThreadBySlug(slugOrId);
        if (dbThreadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        daoThread.updateThread(dbThreadModel.getId(), threadUpdateModel);
        return daoThread.getThreadById(dbThreadModel.getId());
    }

    @PostMapping(path = "/vote")
    public ThreadModel createVote(@PathVariable("slug_or_id") final String slugOrId,
                                  @RequestBody final VoteModel voteModel,
                                  final HttpServletResponse response
    ) {

        int dbUserId;
        try {
            dbUserId = daoUser.getUserId(voteModel.getNickname());

        } catch (DataAccessException err) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        Integer dbThreadId = getIdBySlug(slugOrId);
        if (dbThreadId == null) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        daoThread.voteThread(dbThreadId, dbUserId, voteModel.getStatus());

        response.setStatus(HttpServletResponse.SC_OK);
        return daoThread.getThreadById(dbThreadId);
    }

    @GetMapping(path = "/posts")
    public ObjectNode getPostList(@PathVariable("slug_or_id") final String slugOrId,
                                  @RequestParam(value = "limit", defaultValue = "0") final Integer limit,
                                  @RequestParam(value = "marker", defaultValue = "0") final Integer marker,
                                  @RequestParam(value = "sort", defaultValue = "flat") final String sort,
                                  @RequestParam(value = "desc", defaultValue = "false") final Boolean desc,
                                  final HttpServletResponse response) {

        Integer dbThreadId = getIdBySlug(slugOrId);
        if (dbThreadId == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        List<PostModel> postModelList = daoPost.getPostList(dbThreadId, limit, desc, sort, marker);
        Integer newMarker = daoPost.getNewPostOffset(marker, sort, postModelList);

        return new PostPageModel(postModelList, Integer.toString(newMarker)).toJsonNode();
    }

    @PostMapping(path = "/create")
    public List<PostModel> createPostBatch(@PathVariable("slug_or_id") final String slugOrId,
                                           @RequestBody final List<PostModel> postModelList,
                                           final HttpServletResponse response
    ) {

        ThreadModel dbThreadModel = getThreadBySlug(slugOrId);
        if (dbThreadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (!validUsers(postModelList)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (!validParents(postModelList, dbThreadModel.getId())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }

        return createPostsInside(dbThreadModel, postModelList, response);
    }


    //below are private methods for Mappings above

    @Transactional
    private List<PostModel> createPostsInside(final ThreadModel threadModel,
                                              final List<PostModel> postModelList,
                                              final HttpServletResponse response
    ) {
        addPostAuthors(postModelList, threadModel);
        updatePostList(postModelList, threadModel);

        final List<Integer> postIdList;
        try {
            postIdList = daoPost.createPosts(postModelList);
        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        for (int i = 0; i != postModelList.size(); ++i) {
            postModelList.get(i).setId(postIdList.get(i));
        }

        daoForum.increasePostCount(threadModel.getForum(), postModelList.size());
        response.setStatus(HttpServletResponse.SC_CREATED);

        return postModelList;
    }

    private void addPostAuthors(List<PostModel> postModelList, ThreadModel threadModel) {
        final List<String> usersToAdd = new ArrayList<>();

        for (int i = 0; i != postModelList.size(); ++i) {
            final String author = postModelList.get(i).getAuthor();
            if (!usersToAdd.contains(author)) {
                usersToAdd.add(author);
            }
        }

        usersToAdd.sort(String::compareTo);
        daoForum.addUserBatch(threadModel.getForum(), usersToAdd);
    }

    private void updatePostList(List<PostModel> postModelList, ThreadModel threadModel) {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final String forumSlug = threadModel.getForum();
        final Integer threadId = threadModel.getId();

        for (int i = 0; i != postModelList.size(); ++i) {
            postModelList.get(i).setForum(forumSlug);
            postModelList.get(i).setThread(threadId);
            postModelList.get(i).setCreated(timestamp);
        }
    }

    private boolean validParents(List<PostModel> postModelList, Integer threadId) {
        final List<Integer> parentIdList = new ArrayList<>();
        for (int i = 0; i != postModelList.size(); ++i) {
            final Integer parentId = postModelList.get(i).getParent();

            if (parentId != null && !parentId.equals(0)) {
                if (!parentIdList.contains(parentId)) {
                    parentIdList.add(parentId);
                }
            }
        }
        return parentIdList.isEmpty() || daoPost.checkPostsPresence(parentIdList, threadId);
    }

    private boolean validUsers(List<PostModel> postModelList) {
        final List<String> userNameList = new ArrayList<>();
        for (int i = 0; i != postModelList.size(); ++i) {
            userNameList.add(postModelList.get(i).getAuthor());
        }

        return daoUser.checkUsersPresence(userNameList);
    }
}
