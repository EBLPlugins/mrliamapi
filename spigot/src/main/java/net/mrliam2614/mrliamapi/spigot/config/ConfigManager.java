package net.mrliam2614.mrliamapi.spigot.config;

import net.mrliam2614.mrliamapi.commons.configuration.annotation.ConfigField;
import net.mrliam2614.mrliamapi.commons.configuration.annotation.ConfigFile;
import net.mrliam2614.mrliamapi.commons.configuration.files.BetterYamlConfiguration;
import net.mrliam2614.mrliamapi.commons.exceptions.BadConfigConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ConfigManager {
    private JavaPlugin plugin;
    private HashMap<Class, Object> configClasses;
    private HashMap<Class, File> configFiles;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configClasses = new HashMap<>();
        this.configFiles = new HashMap<>();
    }

    public void register(Class configClass) {
        Object clazz = registerConfig(configClass);
        this.configClasses.put(configClass, clazz);

        if (!loadFile(configClass))
            throw new BadConfigConfiguration("Could not load config file");
    }

    private <T> T registerConfig(Class<T> configClass) {
        try {
            T clazz = configClass.getConstructor().newInstance();
            return clazz;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getConfig(Class<T> configClass) {
        return (T) this.configClasses.getOrDefault(configClass, null);
    }

    private boolean loadFile(Class configClass) {
        ConfigFile configFile = (ConfigFile) configClass.getAnnotation(ConfigFile.class);
        if (configFile == null) {
            return false;
        }

        String fileName = configFile.name();
        String filePath = configFile.path();

        if (!fileName.endsWith(".yml")) {
            fileName = fileName + ".yml";
        }

        File path = new File(plugin.getDataFolder().getPath() + File.separator + filePath);
        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new RuntimeException("Could not create path: " + path.getPath());
            }
        }

        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Could not create file: " + file.getPath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.configFiles.put(configClass, file);

        if (!loadFields(configClass))
            return false;
        else
            return true;
    }


    protected boolean loadFields(Class configClass) {
        System.out.println("Loading fields for " + configClass.getName());
        File file = this.configFiles.get(configClass);
        Object clazz = this.configClasses.get(configClass);
        try {
            BetterYamlConfiguration configuration = BetterYamlConfiguration.loadConfiguration(file);
            for (Field field : configClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ConfigField.class)) {
                    ConfigField configField = field.getAnnotation(ConfigField.class);
                    String path = configField.path();
                    if (configuration.contains(path)) {
                        if (configuration.get(path) == null) {
                            //Set the value of the Field to the default value of the primitive type
                            if (field.getType().isAssignableFrom(Number.class)) {
                                field.set(this, 0);
                            } else if (field.getType().isAssignableFrom(Boolean.class)) {
                                field.set(this, false);
                            }
                        } else {
                            Object value = configuration.get(path);
                            field.set(clazz, value);
                        }
                    } else {
                        configuration.set(path, field.get(clazz));
                    }
                }
            }
            configuration.save();
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected boolean saveFields(Class configClass) {
        File file = this.configFiles.get(configClass);
        Object clazz = this.configClasses.get(configClass);
        try {
            BetterYamlConfiguration configuration = BetterYamlConfiguration.loadConfiguration(file);
            for (Field field : configClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ConfigField.class)) {
                    ConfigField configField = field.getAnnotation(ConfigField.class);
                    String path = configField.path();
                    configuration.set(path, field.get(clazz));
                }
            }
            configuration.save();
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean reload(Class configClass) {
        return loadFields(configClass);
    }

    public void reloadAll() {
        for (Class configClass : this.configClasses.keySet()) {
            reload(configClass);
        }
    }

    public boolean saveConfig(Class configClass) {
        return saveFields(configClass);
    }

    public void saveAll() {
        for (Class configClass : this.configClasses.keySet()) {
            saveConfig(configClass);
        }
    }
}
