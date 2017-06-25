package api.models;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;


public class ThreadModel {

    private Integer id;
    private String title;
    private String author;
    private String forum;
    private String message;
    private Integer votes;
    private String slug;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Timestamp created;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(final String forum) {
        this.forum = forum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Integer getVotes() {
        return votes == null ? 0 : votes;
    }

    public void setVotes(final int votes) {
        this.votes = votes;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(final String slug) {
        this.slug = slug;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(final Timestamp created) {
        this.created = created;
    }
}

