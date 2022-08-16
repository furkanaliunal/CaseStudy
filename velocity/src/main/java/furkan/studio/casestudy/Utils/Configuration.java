package furkan.studio.casestudy.Utils;


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

public class Configuration {
    private final Path dataDirectory;
    private final String configFileName;
    private final Yaml yaml;
    @Getter
    private Map<Object, Object> config;

    public Configuration(final Path dataDirectory, final String configFileName) {
        this.dataDirectory = dataDirectory;
        this.configFileName = configFileName;
        this.yaml = new Yaml(getDumperOptions());
    }

    private DumperOptions getDumperOptions(){
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        return options;
    }


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

    public void save(){
        try {

            FileWriter fileWriter = new FileWriter(dataDirectory.resolve(configFileName).toString());
            yaml.dump(config, fileWriter);
            fileWriter.close();
        } catch (IOException e) {}
    }

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
    public <T> T getOrDefault(final String key, final T defaultValue){
        T result = get(key);
        if (result == null || result.toString().isEmpty()){
            return defaultValue;
        }
        return result;
    }
}


