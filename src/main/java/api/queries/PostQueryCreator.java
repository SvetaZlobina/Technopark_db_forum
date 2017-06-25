package api.queries;


import java.util.Collections;

public class PostQueryCreator {
    public static String getMaxIdQuery() {
        return "SELECT max(id) FROM post";
    }

    public static String getPostIdsAfterIdQuery() {
        return "SELECT id FROM post WHERE id > ? ORDER BY id";
    }

    public static String getPostByIdQuery() {
        return "SELECT * FROM post WHERE id = ? ORDER BY id";
    }

    public static String getCountPostsQuery(int postCount) {
        return "SELECT count(*) FROM post WHERE thread = ? AND id in ( " +
                String.join(", ", Collections.nCopies(postCount, "?")) + " )";
    }

    public static String getPostCreationQuery() {
        return "INSERT INTO post (id, root, root_thread_marker, material_path, parent, author, message, forum, thread, created)\n" +
                "  SELECT  subq.id,\n" +
                "    CASE subq.parent WHEN 0 THEN subq.id ELSE parent_post.root END,\n" +
                "    CASE subq.parent WHEN 0 THEN ? ELSE NULL END, \n" +
                "    CASE subq.parent WHEN 0 THEN array[subq.id]::int[] ELSE array_append(parent_post.material_path, subq.id) END,\n" +
                "    ?, ?, ?, ?, ?, ? FROM\n" +
                "    (SELECT nextval('post_id_seq')::int id, ? parent) subq LEFT JOIN\n" +
                "    post parent_post ON parent_post.id = ?;";
    }

    public static String getPostUpdateQuery() {
        return "UPDATE post SET message = ?, isEdited = TRUE WHERE id = ?";
    }

    public static String getPostsSortParentTreeQuery(final Integer limit,
                                                     final Boolean desc,
                                                     final Integer offset) {
        String subQuery = " (SELECT id FROM post WHERE root_thread_marker = ? ORDER BY id ";
        if (desc) {
            subQuery += " DESC ";
        }

        if (limit > 0) {
            subQuery += " LIMIT ? ";
        }

        if (offset > 0) {
            subQuery += " OFFSET ? ";
        }

        subQuery += ") root_ids ";

        String query = "SELECT p.* FROM post p JOIN " +
                subQuery +
                " ON p.root = root_ids.id\n" +
                "ORDER BY p.material_path ";

        if (desc) {
            query += " DESC ";
        }

        return query;
    }

    public static String getPostsSortTreeQuery(final Integer limit,
                                               final Boolean desc,
                                               final Integer offset) {
        String query = "SELECT * FROM post WHERE thread = ? ORDER BY material_path ";

        if (desc) {
            query += "DESC\n ";
        }

        if (limit > 0) {
            query += "LIMIT ? ";
        }

        if (offset > 0) {
            query += "OFFSET ?\n";
        }

        return query;
    }

    public static String getPostsSortFlat(final Integer limit,
                                          final Boolean desc,
                                          final Integer offset) {

        String query = "SELECT * FROM post WHERE thread = ? ";
        if (desc) {
            query += " ORDER BY created DESC, id DESC ";
        } else {
            query += " ORDER BY created, id ";
        }

        if (limit > 0) {
            query += "LIMIT ? ";
        }

        if (offset > 0) {
            query += "OFFSET ?";
        }

        return query;
    }
}
