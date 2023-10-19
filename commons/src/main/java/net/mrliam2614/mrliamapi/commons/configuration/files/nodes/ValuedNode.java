package net.mrliam2614.mrliamapi.commons.configuration.files.nodes;

import net.mrliam2614.mrliamapi.commons.configuration.files.interfaces.Node;

import java.util.List;
import java.util.Optional;

public class ValuedNode implements Node {
    private String path;
    private Object value;

    public ValuedNode(String path, Object value){
        this.path = path;
        this.value = value;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void addChildren(Node node) {
        throw new UnsupportedOperationException("Cannot add a children node to a Valued Node!");
    }

    @Override
    public Optional<Node> find(String path) {
        return Optional.empty();
    }

    @Override
    public List<Node> getChildrens() {
        return null;
    }

    public <T> T as(Class<T> clazz){
        return (T) value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getPath() + "{" + getValue() + " }";
    }
}
