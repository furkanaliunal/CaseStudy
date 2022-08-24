package furkan.studio.casestudy.velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import furkan.studio.casestudy.velocity.CaseStudy;
import furkan.studio.casestudy.velocity.Utils.Messages;
import furkan.studio.casestudy.velocity.Teleports.TeleportManager;
import furkan.studio.casestudy.velocity.Teleports.TeleportTypes.PendingTeleportation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Onwexrys
 * This class is used to accept a teleportation request.
 * @see SimpleCommand
 */
public class TPAcceptCommand implements SimpleCommand {
    /**
     * TeleportManager is used to manage teleportation requests.
     * @see TeleportManager
     */
    private final TeleportManager teleportManager;

    /**
     * Constructor for TPAcceptCommand.
     * Assigns the teleportManager to the teleportManager field using plugin's instance.
     */
    public TPAcceptCommand() {
        this.teleportManager = CaseStudy.instance.getTeleportManager();
    }

    /**
     * This method is what happens when command is executed.
     * @see com.velocitypowered.api.command.SimpleCommand.Invocation
     * @see net.kyori.adventure.audience.Audience
     * @see TeleportManager
     * @see Messages
     * @param invocation
     */
    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)){
            Messages.sendMessage(invocation.source(), Messages.MUST_BE_PLAYER);
            return;
        }
        String[] arguments = invocation.arguments();
        final Player targetPlayer = (Player)invocation.source();
        if (arguments.length != 1) {
            Messages.sendMessage(targetPlayer, Messages.TPA_ACCEPT_COMMAND_HELP);
            return;
        }
        final String player = arguments[0];

        final Optional<PendingTeleportation> optPendingTeleportation = teleportManager.getPendingTeleportationRequest(player, targetPlayer);
        if (optPendingTeleportation.isPresent()){
            teleportManager.processTeleportationRequest(optPendingTeleportation.get());
            return;
        }
        Messages.sendMessage(targetPlayer, Messages.TPA_ACCEPT_PLAYER_NOT_FOUND);
    }

    /**
     * This method is suggests commands to the player.
     * @see com.velocitypowered.api.command.SimpleCommand.Invocation
     * @see TeleportManager
     * @param invocation
     * @return
     */
    @Override
    public List<String> suggest(Invocation invocation) {
        final List<String> suggestions = new ArrayList<>();
        if (!(invocation.source() instanceof Player)) return suggestions;
        final String[] arguments = invocation.arguments();

        final Player player = (Player)invocation.source();
        if (arguments.length != 0) return suggestions;

        suggestions.addAll(teleportManager.getReceivedPendingTeleportationRequestsOfPlayer(player));
        return suggestions;
    }
}
