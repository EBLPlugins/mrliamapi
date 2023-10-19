package net.mrliam2614.mrliamapi.commons.configuration.files.nodes;

import net.mrliam2614.mrliamapi.commons.configuration.files.interfaces.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubNode implements Node {
    private String path;
    private List<Node> childrens;

    public SubNode(String path){
        this.path = path;
        this.childrens = new ArrayList<>();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void addChildren(Node node) {
        childrens.add(node);
    }
    public Optional<Node> find(String path){
        for(Node n : childrens){
            if(n.getPath().equals(path))
                return Optional.of(n);
        }
        return Optional.empty();
    }

    @Override
    public List<Node> getChildrens() {
        return this.childrens;
    }

    @Override
    public String toString() {
        return getPath() + " {" + childrens.toString() + " }";
    }
}
