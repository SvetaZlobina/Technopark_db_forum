package api.queries;


import java.util.ArrayList;
import java.util.List;

public class ThreadQueryCreator {
    public static String getLastIdQuery() {
        return "SELECT max(id) FROM thread";
    }

    public static String getThreadCreationQuery() {
        return "INSERT INTO thread (title, author, forum, message, votes, slug, created) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    public static String getLastThreadExtractionQuery() {
        return "SELECT * FROM thread WHERE id > ?";
    }

    public static String getThreadBySlugQuery() {
        return "SELECT * FROM thread WHERE slug = ?::citext";
    }

    public static String getThreadByIdQuery() {
        return "SELECT * FROM thread WHERE id = ?";
    }

    public static String getIdByIdQuery() {
        return "SELECT id FROM thread WHERE id = ?";
    }

    public static String getIdBySlugQuery() {
        return "SELECT id FROM thread WHERE slug = ?::citext";
    }

    public static String getVoteStateQuery() {
        return "SELECT status FROM userThreadVote WHERE threadId = ? AND userId = ?";
    }

    public static String getVoteQuery(Integer voteStatus) {
        if (voteStatus.equals(-1)) {
            return  "UPDATE thread SET votes = votes + 2 WHERE id = ?; " +
                    "UPDATE userThreadVote SET status = 1 WHERE threadId = ? AND userId = ?";
        } else {
            return  "UPDATE thread SET votes = votes + 1 WHERE id = ?; " +
                    "INSERT INTO userThreadVote (threadId, userId, status) VALUES (?, ?, 1)";
        }
    }

    public static String getUnvoteQuery(Integer voteStatus) {
        if (voteStatus.equals(1)) {
            return  "UPDATE thread SET votes = votes - 2 WHERE id = ?; " +
                    "UPDATE userThreadVote SET status = -1 WHERE threadId = ? AND userId = ?";
        } else {
            return  "UPDATE thread SET votes = votes - 1 WHERE id = ?; " +
                    "INSERT INTO userThreadVote (threadId, userId, status) VALUES (?, ?, -1)";
        }
    }

    public static String getThreadListQuery(Integer limit, Boolean desc, String since) {
        String query = "SELECT * FROM thread WHERE forum = ? ";

        if (!since.isEmpty()) {
            if (desc) {
                query += String.format("AND created <= '%s' ", since);
            } else {
                query += String.format("AND created >= '%s' ", since);
            }
        }

        query += "ORDER BY created ";

        if (desc) {
            query += "DESC ";
        }

        if (limit > 0) {
            query += "LIMIT ? ";
        }

        return query;
    }

    public static String getThreadUpdateQuery(String title, String message) {
        final List<String> updateStrings = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            updateStrings.add(" title = ? ");
        }

        if (message != null && !message.isEmpty()) {
            updateStrings.add(" message = ? ");
        }

        return "UPDATE thread SET " + String.join(", ", updateStrings) + " WHERE id = ? ";
    }
}
