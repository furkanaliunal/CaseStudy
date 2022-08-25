package io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportTypes;

import com.velocitypowered.api.proxy.Player;
import io.github.furkanaliunal.casestudy.velocity.TeleportRules;
import io.github.furkanaliunal.casestudy.velocity.Teleports.BaseTeleportation;
import lombok.Getter;

/**
 * Created by OnwexryS
 * This class is used store pending teleportation request.
 * @see BaseTeleportation - BaseTeleportation class is used to store any teleportation request.
 */
public class PendingTeleportation extends BaseTeleportation {
    /**
     * Target time to set teleportation request timed out.
     */
    @Getter
    private final long endTime;

    /**
     * Constructor for PendingTeleportation.
     * @see BaseTeleportation - superclass
     * @param player - player who accepted the teleportation request
     * @param target - player who sent the teleportation request
     */
    public PendingTeleportation(final Player player, final Player target) {
        super(player, target);
        this.endTime = System.currentTimeMillis() + TeleportRules.getTeleportRequestTimeoutInMillis();
    }

    /**
     * This method is used to get AcceptedTeleportation clone of this object.
     * @return AcceptedTeleportation - AcceptedTeleportation object
     */
    public AcceptedTeleportation getAcceptedResult(){
        return new AcceptedTeleportation(super.getPlayer(), super.getTargetPlayer());
    }

}
