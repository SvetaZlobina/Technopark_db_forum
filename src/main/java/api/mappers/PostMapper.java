package api.mappers;

import api.models.PostModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PostMapper implements RowMapper<PostModel> {
    @Override
    public PostModel mapRow(ResultSet resultSet, int i) throws SQLException {
        final PostModel postModel = new PostModel();
        postModel.setThread(resultSet.getInt("thread"));
        postModel.setForum(resultSet.getString("forum"));
        postModel.setAuthor(resultSet.getString("author"));
        postModel.setCreated(resultSet.getTimestamp("created"));
        postModel.setIsEdited(resultSet.getBoolean("isEdited"));
        postModel.setId(resultSet.getInt("id"));
        postModel.setMessage(resultSet.getString("message"));
        postModel.setParent(resultSet.getInt("parent"));

        return postModel;
    }
}
