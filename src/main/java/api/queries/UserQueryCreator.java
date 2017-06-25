package api.queries;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserQueryCreator {
    public static String getUserCreationQuery() {
        return "INSERT into \"user\" (nickname, fullname, about, email) values (?, ?, ?, ?)";
    }

    public static String getIdByNicknameQuery() {
        return "SELECT id FROM \"user\" WHERE nickname = ?::citext";
    }

    public static String getCountUsersQuery(int userCount) {
        return "SELECT COUNT(*) FROM \"user\" WHERE nickname in (" +
                String.join(", ", Collections.nCopies(userCount, "?")) + " )";
    }

    public static String getDBNameQuery() {
        return "SELECT nickname FROM \"user\" WHERE nickname = ?::citext";
    }

    public static String getUserByNicknameQuery() {
        return "SELECT * FROM \"user\" WHERE nickname = ?::citext";
    }

    public static String getUserByNicknameOrEmailQuery() {
        return "SELECT * FROM \"user\" WHERE nickname = ?::citext OR email = ?::citext";
    }

    public static String getUserUpdateQuery(boolean hasEmail, boolean hasAbout, boolean hasFullname) {
        final List<String> updateArray = new ArrayList<>();

        if (hasEmail) {
            updateArray.add("email = ? ");
        }

        if (hasAbout) {
            updateArray.add("about = ? ");

        }

        if (hasFullname) {
            updateArray.add("fullname = ? ");
        }

        return "UPDATE \"user\" SET " + String.join(", ", updateArray) + " WHERE nickname = ?::citext; ";
    }
}
