package api.models;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class PostModel {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")

    private Timestamp created;
    private Integer thread;
    private String forum;
    private Boolean isEdited;
    private String message;
    private String author;
    private Integer parent;
    private Integer id;

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(final Timestamp created) {
        this.created = created;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(final Integer thread) {
        this.thread = thread;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(final String forum) {
        this.forum = forum;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(final Boolean edited) {
        isEdited = edited;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public Integer getParent() {
        return parent == null ? 0 : parent;
    }

    public void setParent(final Integer parent) {
        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }
}
