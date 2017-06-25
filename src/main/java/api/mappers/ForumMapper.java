package api.mappers;


import org.springframework.jdbc.core.RowMapper;
import api.models.Forum;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumMapper implements RowMapper<Forum> {

    @Override
    public Forum mapRow(ResultSet resultSet, int i) throws SQLException {
        final Forum forum = new Forum();
        forum.setSlug(resultSet.getString("slug"));
        forum.setTitle(resultSet.getString("title"));
        forum.setUser(resultSet.getString("user"));
        forum.setPosts(resultSet.getInt("posts"));
        forum.setThreads(resultSet.getInt("threads"));
        forum.setId(resultSet.getInt("id"));

        return forum;
    }
}