package net.mrliam2614.mrliamapi.commons.configuration.files.interfaces;

import java.util.List;
import java.util.Optional;

public interface Node {
    String getPath();
    void addChildren(Node node);

    Optional<Node> find(String path);

    List<Node> getChildrens();

    String toString();
}
