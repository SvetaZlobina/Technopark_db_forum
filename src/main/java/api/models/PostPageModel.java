package api.models;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;


public class PostPageModel implements JsonConvertible {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    private List<PostModel> postModels;
    private String marker;

    @Override
    public ObjectNode toJsonNode() {

        final ArrayNode arrayJsonNode = mapper.createArrayNode();
        for (int i = 0; i != postModels.size(); ++i) {
            arrayJsonNode.add(mapper.convertValue(postModels.get(i), JsonNode.class));
        }

        final ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("marker", marker);
        jsonNode.set("posts", arrayJsonNode);

        return jsonNode;
    }

    public PostPageModel(final List<PostModel> postModelList,
                         final String marker) {
        this.marker = marker;
        this.postModels = postModelList;
    }
}
