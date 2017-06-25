package api.queries;


public class ServiceQueryCreator {
    public static String getClearQuery() {
        return "TRUNCATE \"User\" CASCADE ; TRUNCATE Post; TRUNCATE UserForumLink";
    }

    public static String getUserCountQuery() {
        return "SELECT count(*) FROM \"User\"";
    }

    public static String getForumCountQuery() {
        return "SELECT count(*) FROM Forum";
    }

    public static String getThreadCountQuery() {
        return "SELECT count(*) FROM Thread";
    }

    public static String getPostCountQuery() {
        return "SELECT count(*) FROM Post";
    }

}
