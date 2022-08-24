package furkan.studio.casestudy.velocity;

import com.velocitypowered.api.proxy.Player;
import furkan.studio.casestudy.velocity.Utils.Configuration;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TeleportRules {
    @Getter
    private static final Map<String, String> restrictedServers = new HashMap<>();

    @Getter
    private static int teleportRequestTimeoutSeconds, teleportListHistorySize, countdownPerTeleportRequestSeconds;
    @Getter
    private static long teleportRequestTimeoutInMillis, countdownPerTeleportRequestInMillis;

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

    public static boolean canPlayerJoinServer(final String serverName, final Player player){
        final String permission = restrictedServers.get(serverName);
        if (permission == null) return true;
        if (player.hasPermission(permission)) return true;
        return false;
    }
}
