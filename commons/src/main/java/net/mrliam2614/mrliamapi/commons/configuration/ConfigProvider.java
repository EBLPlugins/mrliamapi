package net.mrliam2614.mrliamapi.commons.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import net.mrliam2614.mrliamapi.commons.exceptions.BadConfigConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigProvider<T> {

    private final ObjectMapper mapper;
    @Getter
    private T config;
    private final Class<T> configClass;
    private final File filePath;

    public ConfigProvider(Path dataDirectory, String fileName, Class<T> configClass) {
        this.configClass = configClass;
        mapper = new ObjectMapper(new YAMLFactory());
        filePath = new File(dataDirectory.toFile(), fileName);
        if (!filePath.exists()) {
            filePath.getParentFile().mkdirs();
            try {
                config = configClass.getConstructor().newInstance();
                reloadConfig(true);
            } catch (NoSuchMethodException e) {
                throw new BadConfigConfiguration("Cannot create config: Missing no args constructor!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            loadConfig();
        }

    }

    private boolean loadConfig() {
        try {
            config = mapper.readValue(filePath, configClass);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveConfig() {
        try {
            mapper.writeValue(filePath, config);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void reloadConfig(boolean save) {
        if (save) saveConfig();
        loadConfig();
    }
}
