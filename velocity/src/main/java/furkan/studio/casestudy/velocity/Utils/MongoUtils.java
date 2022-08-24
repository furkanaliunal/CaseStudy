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


/**
 * Created by Onwexrys
 * This is library class to initialize MongoDB connection.
 */
public class MongoUtils {
    /**
     * Reference of history collection
     */
    public static String teleportHistoryCollection = null;
    /**
     * MongoClient instance
     * @see MongoClient
     */
    @Getter
    private static MongoClient mongoClient = null;

    /**
     * MongoDatabase instance
     * @see MongoDatabase
     */
    @Getter
    private static MongoDatabase mongoDatabase = null;

    /**
     * MongoCollection instance
     * @see MongoCollection
     */
    @Getter
    private static MongoCollection collection = null;



    /**
     * Reads the MongoDB connection data from config and uses it to initialize MongoDB connection.
     * Alternative way to initialize MongoDB {@link MongoUtils#initialize(boolean, String, Integer, String, String, String, String)} method.
     * @param config
     * @see Configuration
     * @return
     */
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

    /**
     * Initializes MongoDB connection.
     * @param isSRVEnabled true if SRV is enabled, false otherwise.
     * @param host hostname or IP address of the MongoDB server.
     * @param port port number of the MongoDB server.
     * @param stringDatabase database name.
     * @param stringCollection collection name.
     * @param username username of the MongoDB server.
     * @param password password of the MongoDB server.
     * @return
     */
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


    /**
     * Closes the MongoDB connection.
     */
    public static void close(){
        if (mongoClient != null) mongoClient.close();
    }
}
