package api.models;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PostDetail implements JsonConvertible {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    private Post post;
    private Thread thread;
    private User author;
    private Forum forum;

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public ObjectNode toJsonNode() {
        final ObjectNode node = mapper.createObjectNode();
        node.set("forum", mapper.convertValue(forum, JsonNode.class));
        node.set("author", mapper.convertValue(author, JsonNode.class));
        node.set("thread", mapper.convertValue(thread, JsonNode.class));
        node.set("post", mapper.convertValue(post, JsonNode.class));

        return node;
    }
}
