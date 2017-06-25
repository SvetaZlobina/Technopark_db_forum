package api.models;

public class StatusModel {

    private Integer post;
    private Integer thread;
    private Integer forum;
    private Integer user;

    public Integer getPost() {
        return post;
    }

    public void setPost(final Integer post) {
        this.post = post;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(final Integer thread) {
        this.thread = thread;
    }

    public Integer getForum() {
        return forum;
    }

    public void setForum(final Integer forum) {
        this.forum = forum;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(final Integer user) {
        this.user = user;
    }
}
