package api.mappers;

import org.springframework.jdbc.core.RowMapper;
import api.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        final User user = new User();
        user.setNickname(resultSet.getString("nickname"));
        user.setEmail(resultSet.getString("email"));
        user.setAbout(resultSet.getString("about"));
        user.setFullname(resultSet.getString("fullname"));
        user.setId(resultSet.getInt("id"));

        return user;
    }
}
