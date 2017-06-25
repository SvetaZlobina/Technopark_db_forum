package api.models;

public class ForumModel {

    private Integer posts;
    private String slug;
    private Integer threads;
    private String title;
    private String user;
    private Integer id;

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(final Integer posts) {
        this.posts = posts;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(final Integer threads) {
        this.threads = threads;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }


}
