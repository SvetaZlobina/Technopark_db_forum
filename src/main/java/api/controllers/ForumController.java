package api.controllers;


import api.models.ThreadModel;
import api.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.DaoForum;
import api.models.ForumModel;
import api.daoFiles.DaoThread;
import api.daoFiles.DaoUser;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "api/forum")
public class ForumController {
    @Autowired
    private DaoUser daoUser;

    @Autowired
    private DaoForum daoForum;

    @Autowired
    private DaoThread daoThread;

    private static final Logger log = Logger.getLogger(ForumController.class.getName());

    @GetMapping(path = "/{slug}/users")
    public List<UserModel> getUserList(@PathVariable final String slug,
                                       @RequestParam(value = "limit", defaultValue = "0") final Integer limit,
                                       @RequestParam(value = "desc", defaultValue = "false") final Boolean desc,
                                       @RequestParam(value = "since", defaultValue = "") final String since,
                                       final HttpServletResponse response) {
        String dbSlug;
        try {
            dbSlug = daoForum.getDBForumSlug(slug);
        } catch (DataAccessException err) {
            //log.info("DataAccessException: " + err);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return daoForum.getForumUsers(dbSlug, limit, desc, since);
    }

    @PostMapping(path = "/{slug}/create")
    public ThreadModel createThread(@PathVariable("slug") final String forumSlug,
                                    @RequestBody final ThreadModel threadModel,
                                    final HttpServletResponse response)
            throws IllegalAccessException {
        UserModel oldUserModel;
        ForumModel oldForumModel;
        try {
            oldUserModel = daoUser.getUser(threadModel.getAuthor());
            oldForumModel = daoForum.readForum(forumSlug);
        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        threadModel.setForum(oldForumModel.getSlug());
        threadModel.setAuthor(oldUserModel.getNickname());

        if (threadModel.getSlug() != null) {
            try {
                final ThreadModel dbThreadModel = daoThread.getThreadBySlug(threadModel.getSlug());
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return dbThreadModel;

            } catch (DataAccessException e) {
                //TODO: show Exception somehow
            }
        }

        response.setStatus(HttpServletResponse.SC_CREATED);

        ThreadModel resultSet = daoThread.create(threadModel);
        daoForum.incrementThreadCount(oldForumModel.getSlug());
        daoForum.addSingleUser(oldForumModel.getSlug(), oldUserModel.getNickname());

        return resultSet;
    }


    @GetMapping(path = "/{slug}/threads")
    public List<ThreadModel> getThreadList(@PathVariable("slug") final String forumSlug,
                                           @RequestParam(value = "limit", defaultValue = "0") final Integer limit,
                                           @RequestParam(value = "desc", defaultValue = "false") final Boolean desc,
                                           @RequestParam(value = "since", defaultValue = "") final String since,
                                           final HttpServletResponse response) {
        String dbSlug;
        try {
            dbSlug = daoForum.getDBForumSlug(forumSlug);
        } catch (DataAccessException err) {
            //log.info("DataAccessException: " + err);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return daoThread.getThreadList(dbSlug, limit, desc, since);
    }

    @GetMapping(path = "/{slug}/details")
    public ForumModel getDetails(@PathVariable("slug") final String forumSlug,
                                 final HttpServletResponse response) {

        try {
            response.setStatus(HttpServletResponse.SC_OK);
            return daoForum.readForum(forumSlug);
        } catch (DataAccessException err) {
            //log.info("DataAccessException: " + err);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping(path = "/create")
    public ForumModel create(@RequestBody final ForumModel forumModel,
                             final HttpServletResponse response) {

        try {
            final ForumModel dbForumModel = daoForum.readForum(forumModel.getSlug());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return dbForumModel;

        } catch (DataAccessException e) {

            try {
                final String userName = daoUser.getDBUserName(forumModel.getUser());
                daoForum.createForum(forumModel.getTitle(), userName, forumModel.getSlug());
                response.setStatus(HttpServletResponse.SC_CREATED);
                return daoForum.readForum(forumModel.getSlug());

            } catch (DataAccessException err) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }
    }
}
