package api.models;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;


public class PostPage implements JsonConvertible {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    List<Post> posts;
    String marker;

    @Override
    public ObjectNode toJsonNode() {
        final ArrayNode arrayJsonNode = mapper.createArrayNode();
        for (int i = 0; i != posts.size(); ++i) {
            arrayJsonNode.add(mapper.convertValue(posts.get(i), JsonNode.class));
        }

        final ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("marker", marker);
        jsonNode.set("posts", arrayJsonNode);

        return jsonNode;
    }

    public PostPage(List<Post> postList, String marker) {
        this.marker = marker;
        this.posts = postList;
    }
}
