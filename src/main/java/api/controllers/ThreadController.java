package api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import api.daoFiles.ForumDAO;
import api.models.*;
import api.daoFiles.UserDAO;
import api.models.Thread;

import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;


@RestController
@RequestMapping(path = "api/thread/{slug_or_id}")
public class ThreadController extends ThreadHelper {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ForumDAO forumDAO;

    private static final Logger log = Logger.getLogger(ThreadController.class.getName());


    @GetMapping(path = "/details")
    public Thread getThreadDetails(@PathVariable("slug_or_id") final String slugOrId,
                                   final HttpServletResponse response
    ) {

        Thread dbThread = getThreadBySlugOrId(slugOrId);
        if (dbThread == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return dbThread;
    }


    @PostMapping(path = "/details")
    public Thread updateThread(@PathVariable("slug_or_id") final String slugOrId,
                               @RequestBody final ThreadUpdate threadUpdate,
                               final HttpServletResponse response
    ) {

        Thread dbThread = getThreadBySlugOrId(slugOrId);
        if (dbThread == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        threadDAO.updateThread(dbThread.getId(), threadUpdate);
        return threadDAO.getThreadById(dbThread.getId());
    }

    @PostMapping(path = "/vote")
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
