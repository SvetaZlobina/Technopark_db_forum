package api.daoFiles;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import api.helper.ContainerHelper;
import api.models.Thread;
import api.mappers.ThreadMapper;
import api.models.ThreadUpdate;
import api.models.Vote;
import api.queries.ThreadQueryCreator;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ThreadDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final ThreadMapper THREAD_MAPPER = new ThreadMapper();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Thread create(Thread thread) throws DataAccessException, IllegalAccessException {
        Integer lastId = jdbcTemplate.queryForObject(ThreadQueryCreator.getLastIdQuery(), Integer.class);
        lastId = lastId == null ? 0 : lastId;

        final Object[] sqlParameters = new Object[] {
                thread.getTitle(),
                thread.getAuthor(),
                thread.getForum(),
                thread.getMessage(),
                thread.getVotes(),
                thread.getSlug(),
                thread.getCreated()
        };
        jdbcTemplate.update(ThreadQueryCreator.getThreadCreationQuery(), sqlParameters);

        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getLastThreadExtractionQuery(),
                new Object[]{lastId},
                THREAD_MAPPER
        );
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateThread(Integer threadId, ThreadUpdate threadUpdate) throws DataAccessException {
        final ArrayList<Object> queryParameters = new ArrayList<>();

        final String threadTitle = threadUpdate.getTitle();
        if (ContainerHelper.isPresent(threadTitle)) {
            queryParameters.add(threadTitle);
        }

        final String threadMessage = threadUpdate.getMessage();
        if (ContainerHelper.isPresent(threadMessage)) {
            queryParameters.add(threadMessage);
        }

        if (queryParameters.isEmpty()) {
            return;
        }

        queryParameters.add(threadId);
        jdbcTemplate.update(
                ThreadQueryCreator.getThreadUpdateQuery(threadTitle, threadMessage),
                queryParameters.toArray()
        );
    }

    // Do not delete. This method is called by name
    public Integer getIdById(Integer id) {
        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getIdByIdQuery(),
                new Object[]{id},
                Integer.class
        );
    }

    // Do not delete. This method is called by name
    public Integer getIdBySlug(String slug) {
        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getIdBySlugQuery(),
                new Object[]{slug},
                Integer.class
        );
    }

    public List<Thread> getThreadList(String forumSlug, Integer limit, Boolean desc, String since) {
        final ArrayList<Object> sqlData = new ArrayList<>();
        sqlData.add(forumSlug);

        if (limit > 0) {
            sqlData.add(limit);
        }

        return jdbcTemplate.query(
                ThreadQueryCreator.getThreadListQuery(limit, desc, since),
                sqlData.toArray(),
                THREAD_MAPPER
        );
    }

    public Thread getThreadBySlug(String threadSlug) throws DataAccessException {
        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getThreadBySlugQuery(),
                new Object[]{threadSlug},
                THREAD_MAPPER
        );
    }

    public Thread getThreadById(Integer id) throws DataAccessException {
        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getThreadByIdQuery(),
                new Object[]{id},
                THREAD_MAPPER
        );
    }

    public void voteThread(Integer threadId, Integer userId, Vote.VoteStatus voteStatus) {
        final Integer threadVoteStatus = getThreadVoteStatus(threadId, userId);

        final String query;
        if (voteStatus == Vote.VoteStatus.VOTE && !threadVoteStatus.equals(1)) {
            query = ThreadQueryCreator.getVoteQuery(threadVoteStatus);
        } else if (voteStatus == Vote.VoteStatus.UNVOTE && !threadVoteStatus.equals(-1)) {
            query = ThreadQueryCreator.getUnvoteQuery(threadVoteStatus);
        } else {
            return;
        }

        jdbcTemplate.update(
                query,
                threadId,
                threadId,
                userId
        );
    }

    public Integer getThreadVoteStatus(Integer threadId, Integer userId) {
        try {
            return jdbcTemplate.queryForObject(
                    ThreadQueryCreator.getVoteStatusQuery(),
                    new Object[]{threadId, userId},
                    Integer.class
            );
        } catch (DataAccessException e) {
            return 0;
        }
    }

}
