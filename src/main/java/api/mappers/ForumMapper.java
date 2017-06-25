package api.mappers;


import api.models.ForumModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumMapper implements RowMapper<ForumModel> {

    @Override
    public ForumModel mapRow(ResultSet resultSet, int i) throws SQLException {
        final ForumModel forumModel = new ForumModel();
        forumModel.setSlug(resultSet.getString("slug"));
        forumModel.setTitle(resultSet.getString("title"));
        forumModel.setUser(resultSet.getString("user"));
        forumModel.setPosts(resultSet.getInt("posts"));
        forumModel.setThreads(resultSet.getInt("threads"));
        forumModel.setId(resultSet.getInt("id"));

        return forumModel;
    }
}