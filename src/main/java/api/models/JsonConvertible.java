package api.models;


import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonConvertible {
    ObjectNode toJsonNode();
}
