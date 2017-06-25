package api.daoFiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import api.helper.ContainerHelper;
import api.models.User;
import api.mappers.UserMapper;
import api.queries.UserQueryCreator;

import java.util.ArrayList;
import java.util.List;


@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final UserMapper USER_MAPPER = new UserMapper();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int createUser(String nickname, String fullname, String email, String about) {
        jdbcTemplate.update(
                UserQueryCreator.getUserCreationQuery(),
                nickname,
                fullname,
                about,
                email
        );

        return jdbcTemplate.queryForObject(
                UserQueryCreator.getIdByNicknameQuery(),
                new Object[]{nickname},
                Integer.class
        );
    }

    public void updateUser(User user) {
        final Boolean hasEmail = ContainerHelper.isPresent(user.getEmail());
        final Boolean hasAbout = ContainerHelper.isPresent(user.getAbout());
        final Boolean hasFullname = ContainerHelper.isPresent(user.getFullname());
        final Boolean condition = hasEmail || hasAbout || hasFullname;

        if (condition) {
            final List<Object> sqlParameters = new ArrayList<>();

            if (hasEmail) {
                sqlParameters.add(user.getEmail());
            }

            if (hasAbout) {
                sqlParameters.add(user.getAbout());
            }

            if (hasFullname) {
                sqlParameters.add(user.getFullname());
            }

            sqlParameters.add(user.getNickname());

            jdbcTemplate.update(
                    UserQueryCreator.getUserUpdateQuery(hasEmail, hasAbout, hasFullname),
                    sqlParameters.toArray()
            );
        }
    }

    public User getUser(String userName) {
        return jdbcTemplate.queryForObject(
                UserQueryCreator.getUserByNicknameQuery(),
                new Object[]{userName},
                USER_MAPPER
        );
    }

    public List<User> getUserList(String userName, String email) {
        return jdbcTemplate.query(
                UserQueryCreator.getUserByNicknameOrEmailQuery(),
                new Object[]{userName, email},
                USER_MAPPER
        );
    }

    public Integer getUserId(String userName) {
        return jdbcTemplate.queryForObject(
                UserQueryCreator.getIdByNicknameQuery(),
                new Object[]{userName},
                Integer.class
        );
    }

    public String getDBUserName(String userName) {
        return jdbcTemplate.queryForObject(
                UserQueryCreator.getDBNameQuery(),
                new Object[]{userName},
                String.class
        );
    }

    public boolean checkUsersPresence(List<String> userNameList) {
        final Object[] parameters = userNameList.stream().distinct().toArray();

        final int userCount = jdbcTemplate.queryForObject(
                UserQueryCreator.getCountUsersQuery(parameters.length),
                parameters,
                Integer.class
        );
        return userCount == parameters.length;
    }
}
