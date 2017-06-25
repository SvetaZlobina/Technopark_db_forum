package api.daoFiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import api.mappers.UserMapper;
import api.models.Forum;
import api.mappers.ForumMapper;
import api.models.User;
import api.queries.ForumQueryCreator;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ForumDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final UserMapper USER_MAPPER = new UserMapper();
    private static final ForumMapper FORUM_MAPPER = new ForumMapper();

    public void addSingleUser(final String forumSlug, final String userName) {
        jdbcTemplate.update(
                ForumQueryCreator.getUserAdditionQuery(),
                forumSlug,
                userName
        );
    }

    public void addUserBatch(final String forumSlug, final List<String> userNameList) {
        final List<Object[]> sqlArguments = new ArrayList<>();
        for (int i = 0; i != userNameList.size(); ++i) {
            sqlArguments.add(new Object[]{forumSlug, userNameList.get(i)});
        }

        jdbcTemplate.batchUpdate(
                ForumQueryCreator.getUserAdditionQuery(),
                sqlArguments
        );
    }

    public List<User> getForumUsers(final String forumSlug, final Integer limit, final Boolean desc, final String since) {
        final ArrayList<Object> sqlArguments = new ArrayList<>();
        sqlArguments.add(forumSlug);

        if (!since.isEmpty()) {
            sqlArguments.add(since);
        }

        if (limit > 0) {
            sqlArguments.add(limit);
        }

        return jdbcTemplate.query(
                ForumQueryCreator.getForumUsersExtractionQuery(limit, desc, since),
                sqlArguments.toArray(),
                USER_MAPPER
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void incrementThreadCount(final String forumSlug) {
        jdbcTemplate.update(
                ForumQueryCreator.getThreadIncrementQuery(),
                forumSlug
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void increasePostCount(final String forumSlug, final Integer postsToIncrease) {
        jdbcTemplate.update(
                ForumQueryCreator.getPostIncreaseQuery(),
                postsToIncrease,
                forumSlug
        );
    }

    public Forum readForum(final String forumSlug) {
        return jdbcTemplate.queryForObject(
                ForumQueryCreator.getForumExtractionQuery(),
                new Object[]{forumSlug},
                FORUM_MAPPER
        );
    }

    public String getDBForumSlug(final String forumSlug) {
        return jdbcTemplate.queryForObject(
                ForumQueryCreator.getDBForumSlugQuery(),
                new Object[]{forumSlug},
                String.class
        );
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void createForum(final String forumTitle, final String userName, final String forumSlug) {
        jdbcTemplate.update(
                ForumQueryCreator.getForumCreationQuery(),
                forumTitle,
                userName,
                forumSlug
        );
    }
}
