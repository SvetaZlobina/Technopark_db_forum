package api.queries;


public class ForumQueryCreator {
    public static String getForumCreationQuery() {
        return "INSERT into forum (title, \"user\", slug) values (?, ?, ?)";
    }

    public static String getForumExtractionQuery() {
        return "SELECT * FROM forum WHERE slug = ?::citext";
    }

    public static String getThreadIncrementQuery() {
        return "UPDATE forum SET threads = threads + 1 WHERE slug = ?";
    }

    public static String getPostIncreaseQuery() {
        return "UPDATE forum SET posts = posts + ? WHERE slug = ?";
    }

    public static String getDBForumSlugQuery() {
        return "SELECT slug FROM forum WHERE slug = ?::citext";
    }

    public static String getUserAdditionQuery() {
        return "INSERT INTO userForum (forum_slug, user_nickname) VALUES (?, ?) ON CONFLICT DO NOTHING";
    }

    public static String getForumUsersExtractionQuery(final Integer limit,
                                                      final Boolean desc,
                                                      final String since) {

        String query = "SELECT u.* FROM " +
                "userForum link JOIN \"user\" u ON u.nickname = link.user_nickname " +
                "WHERE link.forum_slug = ?::citext ";

        if (!since.isEmpty()) {
            query += desc ? " AND u.nickname < ?::citext " : " AND u.nickname > ?::citext ";
        }

        query += " ORDER BY u.nickname ";

        if (desc) {
            query += " DESC ";
        }

        if (limit > 0) {
            query += " LIMIT ? ";
        }

        return query;
    }
}
