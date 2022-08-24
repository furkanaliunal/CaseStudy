package furkan.studio.casestudy.bukkit.Utils;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class MongoUtils {
    public static String teleportHistoryCollection = null;
    @Getter
    private static MongoClient mongoClient = null;
    @Getter
    private static MongoDatabase mongoDatabase = null;
    @Getter
    private static MongoCollection collection = null;


    public static boolean initializeFromConfig(final FileConfiguration config){
        MongoUtils.teleportHistoryCollection = config.getString("MongoDB.TeleportHistoryCollection", "teleporthistory");
        return MongoUtils.initialize(
                config.getString("MongoDB.Hostname","localhost"),
                config.getInt("MongoDB.Port", 27017),
                config.getString("MongoDB.Database", "casestudy"),
                config.getString("MongoDB.Collection", teleportHistoryCollection),
                config.getString("MongoDB.Username", "onwexrys"),
                config.getString("MongoDB.Password", null)
        );
    }
    public static boolean initialize(final String host, final Integer port, final String stringDatabase, final String stringCollection, final String username, final String password) {
        String connectionString =
                ("mongodb+srv://" + username) +
                (password == null ? "" : ":" + password) +
                ("@" + host ) +
                (port.equals(27017) ? "" : (":" + port));

        ConnectionString connection = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connection)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoUtils.mongoClient = MongoClients.create(settings);
        MongoUtils.mongoDatabase = MongoUtils.mongoClient.getDatabase(stringDatabase);
        MongoUtils.collection = MongoUtils.mongoDatabase.getCollection(stringCollection);

        return true;
    }

    public static void close(){
        if (mongoClient != null) mongoClient.close();
    }
}
