package api.mappers;

import api.models.UserModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<UserModel> {

    @Override
    public UserModel mapRow(ResultSet resultSet, int i) throws SQLException {
        final UserModel userModel = new UserModel();
        userModel.setNickname(resultSet.getString("nickname"));
        userModel.setEmail(resultSet.getString("email"));
        userModel.setAbout(resultSet.getString("about"));
        userModel.setFullname(resultSet.getString("fullname"));
        userModel.setId(resultSet.getInt("id"));

        return userModel;
    }
}
