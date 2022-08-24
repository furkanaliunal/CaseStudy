package furkan.studio.casestudy.velocity.Teleports.TeleportTypes;

import com.velocitypowered.api.proxy.Player;
import furkan.studio.casestudy.velocity.TeleportRules;
import furkan.studio.casestudy.velocity.Teleports.BaseTeleportation;
import lombok.Getter;

public class PendingTeleportation extends BaseTeleportation {
    @Getter
    private final long endTime;


    public PendingTeleportation(final Player player, final Player target) {
        super(player, target);
        this.endTime = System.currentTimeMillis() + TeleportRules.getTeleportRequestTimeoutInMillis();
    }

    public AcceptedTeleportation getAcceptedResult(){
        return new AcceptedTeleportation(super.getPlayer(), super.getTargetPlayer());
    }

}
