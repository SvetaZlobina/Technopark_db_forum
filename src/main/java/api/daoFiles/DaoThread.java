package api.daoFiles;


import api.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import api.helper.ContainerHelper;
import api.mappers.ThreadMapper;
import api.models.ThreadUpdateModel;
import api.models.VoteModel;
import api.queries.ThreadQueryCreator;

import java.util.ArrayList;
import java.util.List;


@Repository
public class DaoThread {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ThreadMapper THREAD_MAPPER = new ThreadMapper();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ThreadModel create(final ThreadModel threadModel)
            throws DataAccessException, IllegalAccessException {

        Integer lastId = jdbcTemplate.queryForObject(ThreadQueryCreator.getLastIdQuery(), Integer.class);
        lastId = lastId == null ? 0 : lastId;

        final Object[] sqlParameters = new Object[]{
                threadModel.getTitle(),
                threadModel.getAuthor(),
                threadModel.getForum(),
                threadModel.getMessage(),
                threadModel.getVotes(),
                threadModel.getSlug(),
                threadModel.getCreated()
        };
        jdbcTemplate.update(ThreadQueryCreator.getThreadCreationQuery(), sqlParameters);

        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getLastThreadExtractionQuery(),
                new Object[]{lastId},
                THREAD_MAPPER
        );
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateThread(final Integer threadId,
                             final ThreadUpdateModel threadUpdateModel) throws DataAccessException {

        final ArrayList<Object> queryParameters = new ArrayList<>();

        final String threadTitle = threadUpdateModel.getTitle();
        if (ContainerHelper.isPresent(threadTitle)) {
            queryParameters.add(threadTitle);
        }

        final String threadMessage = threadUpdateModel.getMessage();
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
    public Integer getIdById(final Integer id) {

        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getIdByIdQuery(),
                new Object[]{id},
                Integer.class
        );
    }

    // Do not delete. This method is called by name
    public Integer getIdBySlug(final String slug) {

        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getIdBySlugQuery(),
                new Object[]{slug},
                Integer.class
        );
    }

    public List<ThreadModel> getThreadList(final String forumSlug,
                                           final Integer limit,
                                           final Boolean desc,
                                           final String since) {

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

    public ThreadModel getThreadBySlug(final String threadSlug) throws DataAccessException {

        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getThreadBySlugQuery(),
                new Object[]{threadSlug},
                THREAD_MAPPER
        );
    }

    public ThreadModel getThreadById(final Integer id) throws DataAccessException {

        return jdbcTemplate.queryForObject(
                ThreadQueryCreator.getThreadByIdQuery(),
                new Object[]{id},
                THREAD_MAPPER
        );
    }

    public void voteThread(final Integer threadId,
                           final Integer userId,
                           final VoteModel.VoteState voteState) {

        Integer threadVoteState = getThreadVoteState(threadId, userId);

        final String query;
        if (voteState == VoteModel.VoteState.VOTE && !threadVoteState.equals(1)) {
            query = ThreadQueryCreator.getVoteQuery(threadVoteState);
        } else if (voteState == VoteModel.VoteState.UNVOTE && !threadVoteState.equals(-1)) {
            query = ThreadQueryCreator.getUnvoteQuery(threadVoteState);
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

    public Integer getThreadVoteState(final Integer threadId,
                                       final Integer userId) {

        try {
            return jdbcTemplate.queryForObject(
                    ThreadQueryCreator.getVoteStateQuery(),
                    new Object[]{threadId, userId},
                    Integer.class
            );
        } catch (DataAccessException e) {

            return 0;
        }
    }

}
