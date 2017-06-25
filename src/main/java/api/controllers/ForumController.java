package api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.ForumDAO;
import api.models.Forum;
import api.models.Thread;
import api.daoFiles.ThreadDAO;
import api.models.User;
import api.daoFiles.UserDAO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "api/forum")
public class ForumController {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ForumDAO forumDAO;

    @Autowired
    private ThreadDAO threadDAO;

    private static final Logger log = Logger.getLogger(ForumController.class.getName());

    @GetMapping(path = "/{slug}/users")
    public List<User> getUserList(@PathVariable final String slug,
                                  @RequestParam(value = "limit", defaultValue = "0") final Integer limit,
                                  @RequestParam(value = "desc", defaultValue = "false") final Boolean desc,
                                  @RequestParam(value = "since", defaultValue = "") final String since,
                                  final HttpServletResponse response) {
        String dbSlug;
        try {
            dbSlug = forumDAO.getDBForumSlug(slug);
        } catch (DataAccessException err) {
            //log.info("DataAccessException: " + err);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return forumDAO.getForumUsers(dbSlug, limit, desc, since);
    }

    @PostMapping(path = "/{slug}/create")
    public Thread createThread(@PathVariable("slug") final String forumSlug,
                               @RequestBody final Thread thread,
                                final HttpServletResponse response)
            throws IllegalAccessException {
        User oldUser;
        Forum oldForum;
        try {
            oldUser = userDAO.getUser(thread.getAuthor());
            oldForum = forumDAO.readForum(forumSlug);
        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        thread.setForum(oldForum.getSlug());
        thread.setAuthor(oldUser.getNickname());

        if (thread.getSlug() != null) {
            try {
                final Thread dbThread = threadDAO.getThreadBySlug(thread.getSlug());
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return dbThread;

            } catch (DataAccessException e) {
                //TODO: show Exception somehow
            }
        }

        response.setStatus(HttpServletResponse.SC_CREATED);

        Thread resultSet = threadDAO.create(thread);
        forumDAO.incrementThreadCount(oldForum.getSlug());
        forumDAO.addSingleUser(oldForum.getSlug(), oldUser.getNickname());

        return resultSet;
    }


    @GetMapping(path = "/{slug}/threads")
    public List<Thread> getThreadList(@PathVariable("slug") final String forumSlug,
                                      @RequestParam(value = "limit", defaultValue = "0") final Integer limit,
                                      @RequestParam(value = "desc", defaultValue = "false") final Boolean desc,
                                      @RequestParam(value = "since", defaultValue = "") final String since,
                                      final HttpServletResponse response) {
        String dbSlug;
        try {
            dbSlug = forumDAO.getDBForumSlug(forumSlug);
        } catch (DataAccessException err) {
            //log.info("DataAccessException: " + err);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return threadDAO.getThreadList(dbSlug, limit, desc, since);
    }

    @GetMapping(path = "/{slug}/details")
    public Forum getDetails(@PathVariable("slug") final String forumSlug,
                            final HttpServletResponse response) {

        try {
            response.setStatus(HttpServletResponse.SC_OK);
            return forumDAO.readForum(forumSlug);
        } catch (DataAccessException err) {
            //log.info("DataAccessException: " + err);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping(path = "/create")
    public Forum create(@RequestBody final Forum forum,
                        final HttpServletResponse response) {

        try {
            final Forum dbForum = forumDAO.readForum(forum.getSlug());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return dbForum;

        } catch (DataAccessException e) {

            try {
                final String userName = userDAO.getDBUserName(forum.getUser());
                forumDAO.createForum(forum.getTitle(), userName, forum.getSlug());
                response.setStatus(HttpServletResponse.SC_CREATED);
                return forumDAO.readForum(forum.getSlug());

            } catch (DataAccessException err) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }
    }
}
