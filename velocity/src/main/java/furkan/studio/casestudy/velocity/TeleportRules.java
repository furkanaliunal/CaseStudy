package furkan.studio.casestudy.velocity;

import com.velocitypowered.api.proxy.Player;
import furkan.studio.casestudy.velocity.Utils.Configuration;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Onwexrys
 * This class is used to restrict teleportation requests on specific servers and to keep teleportation rule data.
 */
public class TeleportRules {
    /**
     * Map of the restricted servers.
     */
    @Getter
    private static final Map<String, String> restrictedServers = new HashMap<>();
    /**
     * Teleportation rules.
     */
    @Getter
    private static int teleportRequestTimeoutSeconds, teleportListHistorySize, countdownPerTeleportRequestSeconds;
    /**
     * Teleportation rules.
     */
    @Getter
    private static long teleportRequestTimeoutInMillis, countdownPerTeleportRequestInMillis;


    /**
     * Load and initialize the teleportation rules.
     * @param config Configuration object of the plugin.
     */
    public static void initializeFromConfig(final Configuration config){
        final Map<String, String> restrictionConfig = config.get("RestrictedServers");
        restrictionConfig.forEach((key, value) -> {
            restrictedServers.put(key, value);
        });
        teleportRequestTimeoutSeconds = config.get("Configuration.TeleportRequestTimeout");
        teleportListHistorySize = config.get("Configuration.TeleportListHistorySize");
        countdownPerTeleportRequestSeconds = config.get("Configuration.CountdownPerTeleportRequest");
        teleportRequestTimeoutInMillis = teleportRequestTimeoutSeconds * 1000;
        countdownPerTeleportRequestInMillis = countdownPerTeleportRequestSeconds * 1000;
    }

    /**
     * Check if the player is allowed to teleport to the server.
     * @param serverName Name of the server.
     * @param player Player object.
     * @return
     */
    public static boolean canPlayerJoinServer(final String serverName, final Player player){
        final String permission = restrictedServers.get(serverName);
        if (permission == null) return true;
        if (player.hasPermission(permission)) return true;
        return false;
    }
}
