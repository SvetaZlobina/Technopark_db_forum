package api.daoFiles;

import api.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import api.helper.ContainerHelper;
import api.mappers.UserMapper;
import api.queries.UserQueryCreator;

import java.util.ArrayList;
import java.util.List;


@Repository
public class DaoUser {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final UserMapper USER_MAPPER = new UserMapper();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int createUser(final String nickname,
                          final String fullname,
                          final String email,
                          final String about) {

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

    public void updateUser(final UserModel userModel) {

        final Boolean hasEmail = ContainerHelper.isPresent(userModel.getEmail());
        final Boolean hasAbout = ContainerHelper.isPresent(userModel.getAbout());
        final Boolean hasFullname = ContainerHelper.isPresent(userModel.getFullname());
        final Boolean condition = hasEmail || hasAbout || hasFullname;

        if (condition) {
            final List<Object> sqlParameters = new ArrayList<>();

            if (hasEmail) {
                sqlParameters.add(userModel.getEmail());
            }

            if (hasAbout) {
                sqlParameters.add(userModel.getAbout());
            }

            if (hasFullname) {
                sqlParameters.add(userModel.getFullname());
            }

            sqlParameters.add(userModel.getNickname());

            jdbcTemplate.update(
                    UserQueryCreator.getUserUpdateQuery(hasEmail, hasAbout, hasFullname),
                    sqlParameters.toArray()
            );
        }
    }

    public UserModel getUser(final String userName) {

        return jdbcTemplate.queryForObject(
                UserQueryCreator.getUserByNicknameQuery(),
                new Object[]{userName},
                USER_MAPPER
        );
    }

    public List<UserModel> getUserList(final String userName,
                                       final String email) {

        return jdbcTemplate.query(
                UserQueryCreator.getUserByNicknameOrEmailQuery(),
                new Object[]{userName, email},
                USER_MAPPER
        );
    }

    public Integer getUserId(final String userName) {

        return jdbcTemplate.queryForObject(
                UserQueryCreator.getIdByNicknameQuery(),
                new Object[]{userName},
                Integer.class
        );
    }

    public String getDBUserName(final String userName) {

        return jdbcTemplate.queryForObject(
                UserQueryCreator.getDBNameQuery(),
                new Object[]{userName},
                String.class
        );
    }

    public boolean checkUsersPresence(final List<String> userNameList) {

        final Object[] parameters = userNameList.stream().distinct().toArray();

        final int userCount = jdbcTemplate.queryForObject(
                UserQueryCreator.getCountUsersQuery(parameters.length),
                parameters,
                Integer.class
        );
        return userCount == parameters.length;
    }
}
