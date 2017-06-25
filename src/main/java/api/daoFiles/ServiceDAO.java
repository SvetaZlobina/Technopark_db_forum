package api.daoFiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import api.models.Status;
import api.queries.ServiceQueryCreator;

@Repository
public class ServiceDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void clear() {
        jdbcTemplate.update(ServiceQueryCreator.getClearQuery());
    }

    public Status getStatus() {
        final Status status = new Status();
        status.setUser(jdbcTemplate.queryForObject(ServiceQueryCreator.getUserCountQuery(), Integer.class));
        status.setForum(jdbcTemplate.queryForObject(ServiceQueryCreator.getForumCountQuery(), Integer.class));
        status.setThread(jdbcTemplate.queryForObject(ServiceQueryCreator.getThreadCountQuery(), Integer.class));
        status.setPost(jdbcTemplate.queryForObject(ServiceQueryCreator.getPostCountQuery(), Integer.class));

        return status;
    }
}
