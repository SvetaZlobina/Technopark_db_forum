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

@RestController
public class ForumController {
    @Autowired
    UserDAO userDAO;
    @Autowired
    ForumDAO forumDAO;
    @Autowired
    ThreadDAO threadDAO;

    @GetMapping(value = "api/forum/{slug}/users")
    public List<User> getUserList(@PathVariable String slug,
                                  @RequestParam(value = "limit", defaultValue = "0") Integer limit,
                                  @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                  @RequestParam(value = "since", defaultValue = "") String since,
                                  HttpServletResponse response) {
        final String dbSlug;
        try {
            dbSlug = forumDAO.getDBForumSlug(slug);
        } catch (DataAccessException err) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return forumDAO.getForumUsers(dbSlug, limit, desc, since);
    }

    @GetMapping(value = "api/forum/{slug}/threads")
    public List<Thread> getThreadList(@PathVariable("slug") String forumSlug,
                                      @RequestParam(value = "limit", defaultValue = "0") Integer limit,
                                      @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                      @RequestParam(value = "since", defaultValue = "") String since,
                                      HttpServletResponse response) {
        final String dbSlug;
        try {
            dbSlug = forumDAO.getDBForumSlug(forumSlug);
        } catch (DataAccessException err) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return threadDAO.getThreadList(dbSlug, limit, desc, since);
    }

    @GetMapping(value = "api/forum/{slug}/details")
    public Forum getDetails(
            @PathVariable("slug") String forumSlug,
            HttpServletResponse response
    ) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            return forumDAO.readForum(forumSlug);
        } catch (DataAccessException err) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping(value = "/api/forum/create")
    public Forum create(
            @RequestBody Forum forum,
            HttpServletResponse response
    ) {
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
