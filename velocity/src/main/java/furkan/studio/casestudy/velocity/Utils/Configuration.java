package furkan.studio.casestudy.velocity.Utils;


import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Onwexrys
 * This class is used to manage configuration files.
 * VelocityAPI doesn't have a built-in configuration system, so we have to use a custom one.
 * @see Yaml
 */
public class Configuration {
    /**
     * Used to store the path of the configuration file.
     */
    private final Path dataDirectory;
    /**
     * Used to store the configuration file's name.
     */
    private final String configFileName;
    /**
     * Used to store the configuration file's content.
     */
    private final Yaml yaml;
    /**
     * Used to get data from yaml file.
     */
    @Getter
    private Map<Object, Object> config;

    /**
     * Constructor for Configuration.
     * @param dataDirectory - The path of the configuration file.
     * @param configFileName - The name of the configuration file.
     */
    public Configuration(final Path dataDirectory, final String configFileName) {
        this.dataDirectory = dataDirectory;
        this.configFileName = configFileName;
        this.yaml = new Yaml(getDumperOptions());
    }

    /**
     * This method is used as to get recommended options for the yaml file.
     * @return
     */
    private DumperOptions getDumperOptions(){
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        return options;
    }

    /**
     * This method is used to load the configuration file.
     * @see Yaml
     * @see Path
     * @see Files
     * @return
     */
    public boolean reload() {
        try {
            final Path pluginsFolder = dataDirectory;
            final Path path = pluginsFolder.resolve(configFileName);
            if (!path.toFile().exists()){
                pluginsFolder.toFile().mkdirs();
                Files.copy(getClass().getClassLoader().getResourceAsStream(configFileName), dataDirectory.resolve(configFileName));
            }
            config = yaml.load(new FileInputStream(path.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This method is experimental and not sure if it works properly.
     * This method is used to save the configuration file.
     */
    public void save(){
        try {

            FileWriter fileWriter = new FileWriter(dataDirectory.resolve(configFileName).toString());
            yaml.dump(config, fileWriter);
            fileWriter.close();
        } catch (IOException e) {}
    }

    /**
     * Method to get a value from the configuration file.
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(final String key){
        final String[] keys = key.split(Pattern.quote("."));
        if (keys.length == 1){
            return (T) config.get(key);
        }
        Map<Object, Object> map = config;
        try{
            for (int i = 0; i < keys.length-1; i++) {
                map = (Map<Object, Object>) map.get(keys[i]);
                if (map == null){
                    return null;
                }
            }
        }catch(ClassCastException e){
            return null;
        }

        return (T) map.get(keys[keys.length-1]);
    }

    /**
     * Method to get a value from the configuration file. If the value is not exist, it will return the default value.
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T getOrDefault(final String key, final T defaultValue){
        T result = get(key);
        if (result == null || result.toString().isEmpty()){
            return defaultValue;
        }
        return result;
    }
}


