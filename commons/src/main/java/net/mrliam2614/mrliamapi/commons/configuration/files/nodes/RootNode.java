package net.mrliam2614.mrliamapi.commons.configuration.files.nodes;

import net.mrliam2614.mrliamapi.commons.configuration.files.interfaces.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RootNode implements Node{
    private final String path;
    private final List<Node> childrens;

    public RootNode(){
        this.path = "root";
        childrens = new ArrayList<>();
    }

    @Override
    public String getPath() {
        return this.path;
    }

    public void addChildren(Node node){
        childrens.add(node);
    }

    @Override
    public Optional<Node> find(String path) {
        return find(this, path);
    }

    @Override
    public List<Node> getChildrens() {
        return this.childrens;
    }

    public Optional<Node> find(Node searchIn, String path) {
        String firstPath = path.split("\\.")[0];
        String restPath = path.replaceFirst(firstPath + ".", "");

        if(searchIn instanceof ValuedNode){
            return Optional.of(searchIn);
        }

        for(Node subNodes : searchIn.getChildrens()){
            if(subNodes.getPath().equals(firstPath)){
                return find(subNodes, restPath);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "root: {" + childrens.toString() + " }";
    }
}
