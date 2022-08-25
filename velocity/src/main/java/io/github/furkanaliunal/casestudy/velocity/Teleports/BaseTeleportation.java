package io.github.furkanaliunal.casestudy.velocity.Teleports;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportTypes.AcceptedTeleportation;
import io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportTypes.PendingTeleportation;
import lombok.Getter;

/**
 * Created by OnwexryS
 * This abstract class is used store any teleportation request.
 * @see AcceptedTeleportation - AcceptedTeleportation class is used to store accepted teleportation request.
 * @see PendingTeleportation - PendingTeleportation class is used to store pending teleportation request.
 */
public abstract class BaseTeleportation {
    /**
     * Player who sent the teleportation request.
     */
    @Getter
    private final Player player;
    /**
     * Player who will accept the teleportation request.
     */
    @Getter
    private final Player targetPlayer;
    /**
     * Server where the player will teleport to.
     */
    @Getter
    private final RegisteredServer targetServer;

    /**
     * Constructor for BaseTeleportation.
     * @param player - player who sent the teleportation request
     * @param target - player who will accept the teleportation request
     */
    public BaseTeleportation(final Player player, final Player target) {
        this.player = player;
        this.targetPlayer = target;
        this.targetServer = this.targetPlayer.getCurrentServer().get().getServer();
    }

}
