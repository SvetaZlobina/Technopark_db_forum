package api.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserModel {

    private Integer id;
    private String email;
    private String about;
    private String fullname;
    private String nickname;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(final String about) {
        this.about = about;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(final String fullname) {
        this.fullname = fullname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public ObjectNode convertToObjectNode(ObjectMapper mapper) {
        final ObjectNode node = mapper.createObjectNode();
        node.put("id", id);
        node.put("fullname", fullname);
        node.put("about", about);
        node.put("email", email);
        node.put("nickname", nickname);

        return node;
    }
}
