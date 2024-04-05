package net.mrliam2614.mrliamapi.commons.configuration;

import java.nio.file.Path;
import java.util.HashMap;

public class ConfigManager {
    private HashMap<String, ConfigProvider<?>> configs;

    public ConfigManager(){
        this.configs = new HashMap<>();
    }

    public <T> void registerConfiguration(Path filePath, String fileName, Class<T> clazz){
        configs.put(fileName, new ConfigProvider<T>(filePath, fileName, clazz));
    }

    private String getName(String fileName){
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public <T> ConfigProvider<T> getConfig(Class<T> clazz){
        for (ConfigProvider<?> config : configs.values()) {
            if (config.getConfig().getClass().equals(clazz)) {
                return (ConfigProvider<T>) config;
            }
        }
        return null;
    }

    public <T> boolean saveConfig(Class<T> clazz){
        ConfigProvider<T> config = getConfig(clazz);
        if(config == null) return false;
        config.saveConfig();
        return true;
    }
}
