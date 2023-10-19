package net.mrliam2614.mrliamapi.commons.configuration.files;

import net.mrliam2614.mrliamapi.commons.configuration.files.nodes.RootNode;
import net.mrliam2614.mrliamapi.commons.configuration.files.nodes.SubNode;
import net.mrliam2614.mrliamapi.commons.configuration.files.nodes.ValuedNode;
import net.mrliam2614.mrliamapi.commons.configuration.files.interfaces.Node;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BetterYamlConfiguration {
    private final File file;
    private RootNode rootNode;

    private Yaml yaml;
    /*
    Contains, get, set, save
     */

    public static BetterYamlConfiguration loadConfiguration(File file) throws IOException {
        return new BetterYamlConfiguration(file);
    }

    public BetterYamlConfiguration(File file) throws IOException {
        this.file = file;
        this.rootNode = new RootNode();

        readFile();
    }

    private void readFile() throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        yaml = new Yaml(options);
        FileInputStream fileInputStream = new FileInputStream(file);
        Map<String, Object> objectMap = new HashMap<>();
        Map<String, Object> receivedMap = yaml.load(fileInputStream);

        if(receivedMap != null)
            objectMap.putAll(receivedMap);

        fileInputStream.close();
        createTree(rootNode, objectMap);
    }

    private void createTree(Node parentNode, Map<String, Object> objectMap) {

        for (String path : objectMap.keySet()) {
            if (isMap(objectMap.get(path))) {
                SubNode subNode = new SubNode(path);
                parentNode.addChildren(subNode);

                Map<String, Object> subObject = (Map<String, Object>) objectMap.get(path);
                createTree(subNode, subObject);
            } else {
                Object object = objectMap.get(path);
                ValuedNode valuedNode = new ValuedNode(path, object);

                parentNode.addChildren(valuedNode);

            }
        }
    }

    private boolean isMap(Object object) {
        return object instanceof Map<?, ?>;
    }

    public boolean contains(String path) {
        Optional<Node> value = rootNode.find(path);
        return value.isPresent();
    }

    public Object get(String path) {
        Optional<Node> valuedNode = rootNode.find(path);
        if (valuedNode.isEmpty()) return null;
        ValuedNode node = (ValuedNode) valuedNode.get();
        return node.getValue();
    }

    public Optional<Node> getNode(String path) {
        return rootNode.find(path);
    }

    public void set(String path, Object value) {
        System.out.println("Setting " + path + " to " + value);
        Optional<Node> valuedNode = rootNode.find(path);
        if (valuedNode.isEmpty()) {
            createNode(path, value);
        }else {
            ValuedNode node = (ValuedNode) valuedNode.get();
            node.setValue(value);
        }
    }

    private void createNode(String path, Object value) {
        String[] pathArray = path.split("\\.");
        Node parentNode = rootNode;
        for (int i = 0; i < pathArray.length - 1; i++) {
            String subPath = pathArray[i];
            Optional<Node> subNode = parentNode.find(subPath);
            if (subNode.isEmpty()) {
                SubNode node = new SubNode(subPath);
                parentNode.addChildren(node);
                parentNode = node;
            } else {
                parentNode = subNode.get();
            }
        }
        ValuedNode node = new ValuedNode(pathArray[pathArray.length - 1], value);
        parentNode.addChildren(node);
    }

    public void save() throws IOException {
        System.out.println("Saving file " + file.getName());
        Map<String, Object> objectMap = saveTree(rootNode, new HashMap<>());

        FileWriter fileWriter = new FileWriter(file);

        yaml.dump(objectMap, fileWriter);

        System.out.println("SAVED");
        System.out.println(objectMap.toString());
        System.out.println("\n\n\n");

        fileWriter.close();
    }

    private Map<String, Object> saveTree(Node parentNode, Map<String, Object> objectMap) {
        for (Node node : parentNode.getChildrens()) {
            if (node instanceof SubNode) {
                SubNode subNode = (SubNode) node;
                Map<String, Object> subObjectMap = saveTree(subNode, new HashMap<>());
                objectMap.put(subNode.getPath(), subObjectMap);
            } else {
                ValuedNode valuedNode = (ValuedNode) node;
                objectMap.put(valuedNode.getPath(), valuedNode.getValue());
            }
        }
        return objectMap;
    }
}
