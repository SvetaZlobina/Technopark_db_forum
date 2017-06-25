package api.queries;


public class ServiceQueryCreator {
    public static String getClearQuery() {
        return "TRUNCATE \"user\" CASCADE ; TRUNCATE post; TRUNCATE userForum";
    }

    public static String getUserCountQuery() {
        return "SELECT count(*) FROM \"user\"";
    }

    public static String getForumCountQuery() {
        return "SELECT count(*) FROM forum";
    }

    public static String getThreadCountQuery() {
        return "SELECT count(*) FROM thread";
    }

    public static String getPostCountQuery() {
        return "SELECT count(*) FROM post";
    }

}
