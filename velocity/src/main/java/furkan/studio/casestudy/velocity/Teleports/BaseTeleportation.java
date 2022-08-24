package furkan.studio.casestudy.velocity.Teleports;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;

public abstract class BaseTeleportation {
    @Getter
    private final Player player;
    @Getter
    private final Player targetPlayer;
    @Getter
    private final RegisteredServer targetServer;

    public BaseTeleportation(final Player player, final Player target) {
        this.player = player;
        this.targetPlayer = target;
        this.targetServer = this.targetPlayer.getCurrentServer().get().getServer();
    }

}
