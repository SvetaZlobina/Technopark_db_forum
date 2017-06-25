package api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.ForumDAO;
import api.models.*;
import api.daoFiles.UserDAO;
import api.models.Thread;

import javax.servlet.http.HttpServletResponse;


@RestController
public class ThreadController extends ThreadRelatedHelper {

    @Autowired
    UserDAO userDAO;
    @Autowired
    ForumDAO forumDAO;

    @GetMapping(value = "api/thread/{slug_or_id}/details")
    public Thread getThreadDetails(
            @PathVariable("slug_or_id") String slugOrId,
            HttpServletResponse response
    ) {
        final Thread dbThread = getThreadBySlugOrId(slugOrId);
        if (dbThread == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return dbThread;
    }

    @PostMapping(value = "api/forum/{slug}/create")
    public Thread createThread(
            @PathVariable("slug") String forumSlug,
            @RequestBody Thread thread,
            HttpServletResponse response
    ) throws IllegalAccessException {
        final User oldUser;
        final Forum oldForum;
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
                // Left intentionally
            }
        }

        response.setStatus(HttpServletResponse.SC_CREATED);

        final Thread result = threadDAO.create(thread);
        forumDAO.incrementThreadCount(oldForum.getSlug());
        forumDAO.addSingleUser(oldForum.getSlug(), oldUser.getNickname());

        return result;
    }

    @PostMapping(value = "api/thread/{slug_or_id}/details")
    public Thread updateThread(
            @PathVariable("slug_or_id") String slugOrId,
            @RequestBody ThreadUpdate threadUpdate,
            HttpServletResponse response
    ) {
        final Thread dbThread = getThreadBySlugOrId(slugOrId);
        if (dbThread == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        threadDAO.updateThread(dbThread.getId(), threadUpdate);
        return threadDAO.getThreadById(dbThread.getId());
    }

    @PostMapping(value = "api/thread/{slug_or_id}/vote")
    public Thread vote(
            @PathVariable("slug_or_id") String slugOrId,
            @RequestBody Vote vote,
            HttpServletResponse response
    ) {
        final int dbUserId;
        try {
            dbUserId = userDAO.getUserId(vote.getNickname());
        } catch (DataAccessException err) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final Integer dbThreadId = getIdBySlugOrId(slugOrId);
        if (dbThreadId == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        threadDAO.voteThread(dbThreadId, dbUserId, vote.getStatus());

        response.setStatus(HttpServletResponse.SC_OK);
        return threadDAO.getThreadById(dbThreadId);
    }
}
