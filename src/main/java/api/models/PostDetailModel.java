package api.models;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PostDetailModel implements JsonConvertible {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    private PostModel postModel;
    private ThreadModel threadModel;
    private UserModel author;
    private ForumModel forumModel;

    public ForumModel getForum() {
        return forumModel;
    }

    public void setForum(final ForumModel forumModel) {
        this.forumModel = forumModel;
    }

    public UserModel getAuthor() {
        return author;
    }

    public void setAuthor(final UserModel author) {
        this.author = author;
    }

    public ThreadModel getThread() {
        return threadModel;
    }

    public void setThread(final ThreadModel threadModel) {
        this.threadModel = threadModel;
    }

    public PostModel getPost() {
        return postModel;
    }

    public void setPost(final PostModel postModel) {
        this.postModel = postModel;
    }

    @Override
    public ObjectNode toJsonNode() {
        final ObjectNode node = mapper.createObjectNode();
        node.set("forum", mapper.convertValue(forumModel, JsonNode.class));
        node.set("author", mapper.convertValue(author, JsonNode.class));
        node.set("thread", mapper.convertValue(threadModel, JsonNode.class));
        node.set("post", mapper.convertValue(postModel, JsonNode.class));

        return node;
    }
}
