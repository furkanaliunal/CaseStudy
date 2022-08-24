package furkan.studio.casestudy.velocity.Utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;


public class MongoUtils {
    public static String teleportHistoryCollection = null;
    @Getter
    private static MongoClient mongoClient = null;
    @Getter
    private static MongoDatabase mongoDatabase = null;
    @Getter
    private static MongoCollection collection = null;


    public static boolean initializeFromConfig(final Configuration config){
        MongoUtils.teleportHistoryCollection = config.getOrDefault("MongoDB.TeleportHistoryCollection", "teleporthistory");
        return MongoUtils.initialize(
                config.getOrDefault("MongoDB.SRVEnabled", false),
                config.getOrDefault("MongoDB.Hostname","localhost"),
                config.getOrDefault("MongoDB.Port", 27017),
                config.getOrDefault("MongoDB.Database", "casestudy"),
                config.getOrDefault("MongoDB.Collection", teleportHistoryCollection),
                config.getOrDefault("MongoDB.Username", "onwexrys"),
                config.getOrDefault("MongoDB.Password", null)
        );
    }
    public static boolean initialize(final boolean isSRVEnabled, final String host, final Integer port, final String stringDatabase, final String stringCollection, final String username, final String password) {
        String connectionString =
                (isSRVEnabled ? "mongodb+srv://" : "mongodb://") +
                (username) +
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
