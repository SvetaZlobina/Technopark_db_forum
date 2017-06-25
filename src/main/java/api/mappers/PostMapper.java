package api.mappers;

import org.springframework.jdbc.core.RowMapper;
import api.models.Post;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet resultSet, int i) throws SQLException {
        final Post post = new Post();
        post.setThread(resultSet.getInt("thread"));
        post.setForum(resultSet.getString("forum"));
        post.setAuthor(resultSet.getString("author"));
        post.setCreated(resultSet.getTimestamp("created"));
        post.setIsEdited(resultSet.getBoolean("isEdited"));
        post.setId(resultSet.getInt("id"));
        post.setMessage(resultSet.getString("message"));
        post.setParent(resultSet.getInt("parent"));

        return post;
    }
}
