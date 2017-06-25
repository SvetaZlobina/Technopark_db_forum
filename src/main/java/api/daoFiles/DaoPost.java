package api.daoFiles;

import api.models.PostModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import api.helper.ContainerHelper;
import api.mappers.PostMapper;
import api.models.PostUpdateModel;
import api.queries.PostQueryCreator;

import java.util.*;

@Repository
public class DaoPost {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final PostMapper POST_MAPPER = new PostMapper();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Integer> createPosts(final List<PostModel> postModels) {
        final int maxId = getMaxPostId();

        final List<Object[]> sqlData = new ArrayList<>();
        for (int i = 0; i != postModels.size(); ++i) {
            sqlData.add(new Object[]{
                    postModels.get(i).getThread(),
                    postModels.get(i).getParent(),
                    postModels.get(i).getAuthor(),
                    postModels.get(i).getMessage(),
                    postModels.get(i).getForum(),
                    postModels.get(i).getThread(),
                    postModels.get(i).getCreated(),
                    postModels.get(i).getParent(),
                    postModels.get(i).getParent()
            });
        }

        jdbcTemplate.batchUpdate(PostQueryCreator.getPostCreationQuery(), sqlData);

        return getPostIdListAfterId(maxId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updatePost(final Integer postId,
                           final PostUpdateModel update) {

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

    public List<Integer> getPostIdListAfterId(final Integer lastId) {

        return jdbcTemplate.queryForList(
                PostQueryCreator.getPostIdsAfterIdQuery(),
                new Object[]{lastId},
                Integer.class
        );
    }

    public PostModel extractPostById(final Integer postId) {

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

    public List<PostModel> getPostList(final Integer threadId,
                                       final Integer limit,
                                       final Boolean desc,
                                       final String sort,
                                       final Integer offset) {

        if (sort.equals("flat")) {
            return getPostsSortFlat(threadId, limit, desc, offset);
        } else if (sort.equals("tree")) {
            return getPostsSortTree(threadId, limit, desc, offset);
        } else {
            return getPostsSortParentTree(threadId, limit, desc, offset);
        }
    }

    public Integer getNewPostOffset(final Integer currOffset,
                                    final String sortName,
                                    final List<PostModel> postModelList) {

        if (sortName.equals("flat") || sortName.equals("tree")) {
            return currOffset + postModelList.size();
        } else {
            Integer newOffset = currOffset;
            for (int i = 0; i != postModelList.size(); ++i) {
                final Integer parentId = postModelList.get(i).getParent();
                if (parentId == null || parentId.equals(0)) {
                    ++newOffset;
                }
            }
            return newOffset;
        }
    }

    public boolean checkPostsPresence(final List<Integer> postIdList,
                                      final Integer threadId) {

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

    private List<PostModel> getPostsSortFlat(final Integer threadId,
                                             final Integer limit,
                                             final Boolean desc,
                                             final Integer offset) {

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

    private List<PostModel> getPostsSortTree(final Integer threadId,
                                             final Integer limit,
                                             final Boolean desc,
                                             final Integer offset) {

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

    private List<PostModel> getPostsSortParentTree(final Integer threadId,
                                                   final Integer limit,
                                                   final Boolean desc,
                                                   final Integer offset) {

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
