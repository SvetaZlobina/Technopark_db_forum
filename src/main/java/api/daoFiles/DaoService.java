package api.daoFiles;

import api.models.StatusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import api.queries.ServiceQueryCreator;

@Repository
public class DaoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void clearDB() {
        jdbcTemplate.update(ServiceQueryCreator.getClearQuery());
    }

    public StatusModel getStatus() {
        final StatusModel statusModel = new StatusModel();

        statusModel.setUser(jdbcTemplate.queryForObject(ServiceQueryCreator.getUserCountQuery(), Integer.class));
        statusModel.setForum(jdbcTemplate.queryForObject(ServiceQueryCreator.getForumCountQuery(), Integer.class));
        statusModel.setThread(jdbcTemplate.queryForObject(ServiceQueryCreator.getThreadCountQuery(), Integer.class));
        statusModel.setPost(jdbcTemplate.queryForObject(ServiceQueryCreator.getPostCountQuery(), Integer.class));

        return statusModel;
    }
}
