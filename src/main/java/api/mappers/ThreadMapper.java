package api.mappers;


import api.models.ThreadModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadMapper implements RowMapper<ThreadModel> {
    @Override
    public ThreadModel mapRow(ResultSet resultSet, int i) throws SQLException {
        final ThreadModel threadModel = new ThreadModel();
        threadModel.setTitle(resultSet.getString("title"));
        threadModel.setSlug(resultSet.getString("slug"));
        threadModel.setAuthor(resultSet.getString("author"));
        threadModel.setForum(resultSet.getString("forum"));
        threadModel.setMessage(resultSet.getString("message"));
        threadModel.setCreated(resultSet.getTimestamp("created"));
        threadModel.setId(resultSet.getInt("id"));
        threadModel.setVotes(resultSet.getInt("votes"));

        return threadModel;
    }
}
