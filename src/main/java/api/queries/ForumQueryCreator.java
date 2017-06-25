package api.queries;


public class ForumQueryCreator {
    public static String getForumCreationQuery() {
        return "INSERT into Forum (title, \"user\", slug) values (?, ?, ?)";
    }

    public static String getForumExtractionQuery() {
        return "SELECT * FROM Forum WHERE slug = ?::citext";
    }

    public static String getThreadIncrementQuery() {
        return "UPDATE Forum SET threads = threads + 1 WHERE slug = ?";
    }

    public static String getPostIncreaseQuery() {
        return "UPDATE Forum SET posts = posts + ? WHERE slug = ?";
    }

    public static String getDBForumSlugQuery() {
        return "SELECT slug FROM Forum WHERE slug = ?::citext";
    }

    public static String getUserAdditionQuery() {
        return "INSERT INTO UserForumLink (forum_slug, user_nickname) VALUES (?, ?) ON CONFLICT DO NOTHING";
    }

    public static String getForumUsersExtractionQuery(final Integer limit, final Boolean desc, final String since) {
        String query = "SELECT u.* FROM " +
                "UserForumLink link JOIN \"User\" u ON u.nickname = link.user_nickname " +
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
