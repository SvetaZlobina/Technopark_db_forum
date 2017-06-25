package api.mappers;


import org.springframework.jdbc.core.RowMapper;
import api.models.Thread;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadMapper implements RowMapper<Thread> {
    @Override
    public Thread mapRow(ResultSet resultSet, int i) throws SQLException {
        final Thread thread = new Thread();
        thread.setTitle(resultSet.getString("title"));
        thread.setSlug(resultSet.getString("slug"));
        thread.setAuthor(resultSet.getString("author"));
        thread.setForum(resultSet.getString("forum"));
        thread.setMessage(resultSet.getString("message"));
        thread.setCreated(resultSet.getTimestamp("created"));
        thread.setId(resultSet.getInt("id"));
        thread.setVotes(resultSet.getInt("votes"));

        return thread;
    }
}
