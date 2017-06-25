package api.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.DaoForum;
import api.daoFiles.DaoPost;
import api.models.*;
import api.daoFiles.DaoUser;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(path = "api/post/{id}")
public class PostController extends ServiceForController {

    @Autowired
    private DaoPost daoPost;

    @Autowired
    private DaoForum daoForum;

    @Autowired
    private DaoUser daoUser;


    @GetMapping(path = "/details")
    public ObjectNode getPostData(@PathVariable("id") final Integer id,
                                  @RequestParam(value = "related", defaultValue = "") final String related,
                                  final HttpServletResponse response) {
        PostModel dbPostModel;
        try {
            dbPostModel = daoPost.extractPostById(id);

        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final PostDetailModel postDetailModelData = new PostDetailModel();
        postDetailModelData.setPost(dbPostModel);

        final String[] relatedData = related.split(",");

        for (String option : relatedData) {
            if (option.equals("user")) {
                postDetailModelData.setAuthor(daoUser.getUser(dbPostModel.getAuthor()));

            } else if (option.equals("forum")) {
                postDetailModelData.setForum(daoForum.readForum(dbPostModel.getForum()));

            } else if (option.equals("thread")) {
                postDetailModelData.setThread(daoThread.getThreadById(dbPostModel.getThread()));
            }
        }

        return postDetailModelData.toJsonNode();
    }

    @PostMapping(value = "/details")
    public PostModel updatePostsData(@PathVariable("id") final Integer postId,
                                     @RequestBody final PostUpdateModel postUpdateModel,
                                     final HttpServletResponse response
    ) {
        PostModel dbPostModel;

        try {
            dbPostModel = daoPost.extractPostById(postId);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final String dbMessage = dbPostModel.getMessage();
        final String updateMessage = postUpdateModel.getMessage();
        if (dbMessage.equals(updateMessage)) {
            return dbPostModel;
        }

        daoPost.updatePost(postId, postUpdateModel);
        return daoPost.extractPostById(postId);
    }

}
