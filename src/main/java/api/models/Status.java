package api.models;

public class Status {
    private Integer post;
    private Integer thread;
    private Integer forum;
    private Integer user;

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public Integer getForum() {
        return forum;
    }

    public void setForum(Integer forum) {
        this.forum = forum;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}
