package api.daoFiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import api.helper.ContainerHelper;
import api.models.Post;
import api.mappers.PostMapper;
import api.models.PostUpdate;
import api.queries.PostQueryCreator;

import java.util.*;

@Repository
public class PostDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final PostMapper POST_MAPPER = new PostMapper();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Integer> createPosts(List<Post> posts) {
        final int maxId = getMaxPostId();

        final List<Object[]> sqlData = new ArrayList<>();
        for (int i = 0; i != posts.size(); ++i) {
            sqlData.add(new Object[]{
                    posts.get(i).getThread(),
                    posts.get(i).getParent(),
                    posts.get(i).getAuthor(),
                    posts.get(i).getMessage(),
                    posts.get(i).getForum(),
                    posts.get(i).getThread(),
                    posts.get(i).getCreated(),
                    posts.get(i).getParent(),
                    posts.get(i).getParent()
            });
        }

        jdbcTemplate.batchUpdate(PostQueryCreator.getPostCreationQuery(), sqlData);

        return getPostIdListAfterId(maxId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updatePost(Integer postId, PostUpdate update) {
        final String newMessage = update.getMessage();
        if (!ContainerHelper.isPresent(newMessage)) {
            return;
        }

        jdbcTemplate.update(
                PostQueryCreator.getPostUpdateQuery(),
                newMessage,
                postId
        );
    }

    public List<Integer> getPostIdListAfterId(Integer lastId) {
        return jdbcTemplate.queryForList(
                PostQueryCreator.getPostIdsAfterIdQuery(),
                new Object[]{lastId},
                Integer.class
        );
    }

    public Post extractPostById(Integer postId) {
        return jdbcTemplate.queryForObject(
                PostQueryCreator.getPostByIdQuery(),
                new Object[]{postId},
                POST_MAPPER
        );
    }

    public Integer getMaxPostId() {
        final Integer id = jdbcTemplate.queryForObject(
                PostQueryCreator.getMaxIdQuery(),
                Integer.class
        );

        if (id == null) {
            return 0;
        } else {
            return id;
        }
    }

    public List<Post> getPostList(Integer threadId, Integer limit, Boolean desc, String sort, Integer offset) {
        if (sort.equals("flat")) {
            return getPostsSortFlat(threadId, limit, desc, offset);
        } else if (sort.equals("tree")) {
            return getPostsSortTree(threadId, limit, desc, offset);
        } else {
            return getPostsSortParentTree(threadId, limit, desc, offset);
        }
    }

    public Integer getNewPostOffset(Integer currOffset, String sortName, List<Post> postList) {
        if (sortName.equals("flat") || sortName.equals("tree")) {
            return currOffset + postList.size();
        } else {
            Integer newOffset = currOffset;
            for (int i = 0; i != postList.size(); ++ i) {
                final Integer parentId = postList.get(i).getParent();
                if (parentId == null || parentId.equals(0)) {
                    ++newOffset;
                }
            }
            return newOffset;
        }
    }

    public boolean checkPostsPresence(List<Integer> postIdList, Integer threadId) {
        final List<Object> queryParameters = new ArrayList<>();
        queryParameters.add(threadId);
        queryParameters.addAll(postIdList);

        final Integer postNum = jdbcTemplate.queryForObject(
                PostQueryCreator.getCountPostsQuery(postIdList.size()),
                queryParameters.toArray(),
                Integer.class
        );
        return postNum.equals(postIdList.size());
    }

    private List<Post> getPostsSortFlat(Integer threadId, Integer limit, Boolean desc, Integer offset) {
        final ArrayList<Object> sqlData = new ArrayList<>();
        sqlData.add(threadId);

        if (limit > 0) {
            sqlData.add(limit);
        }

        if (offset > 0) {
            sqlData.add(offset);
        }

        return jdbcTemplate.query(
                PostQueryCreator.getPostsSortFlat(limit, desc, offset),
                sqlData.toArray(),
                POST_MAPPER
        );
    }

    private List<Post> getPostsSortTree(Integer threadId, Integer limit, Boolean desc, Integer offset) {
        final ArrayList<Object> sqlData = new ArrayList<>();
        sqlData.add(threadId);

        if (limit > 0) {
            sqlData.add(limit);
        }

        if (offset > 0) {
            sqlData.add(offset);
        }

        return jdbcTemplate.query(
                PostQueryCreator.getPostsSortTreeQuery(limit, desc, offset),
                sqlData.toArray(),
                POST_MAPPER
        );
    }

    private List<Post> getPostsSortParentTree(Integer threadId, Integer limit, Boolean desc, Integer offset) {
        final ArrayList<Object> sqlParameters = new ArrayList<>();
        sqlParameters.add(threadId);

        if (limit > 0) {
            sqlParameters.add(limit);
        }

        if (offset > 0) {
            sqlParameters.add(offset);
        }

        return jdbcTemplate.query(
                PostQueryCreator.getPostsSortParentTreeQuery(limit, desc, offset),
                sqlParameters.toArray(),
                POST_MAPPER
        );
    }

}
