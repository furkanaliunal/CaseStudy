package io.github.furkanaliunal.casestudy.velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import io.github.furkanaliunal.casestudy.velocity.CaseStudy;
import io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportManager;
import io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportTypes.PendingTeleportation;
import io.github.furkanaliunal.casestudy.velocity.Utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Onwexrys
 * This class is used to deny a teleportation request.
 * @see SimpleCommand
 */
public class TPADenyCommand implements SimpleCommand{
    /**
     * TeleportManager is used to manage teleportation requests.
     * @see TeleportManager
     */
    private final TeleportManager teleportManager;

    /**
     * Constructor for TPAcceptCommand.
     * Assigns the teleportManager to the teleportManager field using plugin's instance.
     */
    public TPADenyCommand() {
        this.teleportManager = CaseStudy.instance.getTeleportManager();
    }


    /**
     * This method is what happens when command is executed.
     * @see com.velocitypowered.api.command.SimpleCommand.Invocation
     * @see net.kyori.adventure.audience.Audience
     * @see TeleportManager
     * @see Messages
     * @param invocation - invocation of the command
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
            Messages.sendMessage(targetPlayer, Messages.TPA_DENY_COMMAND_HELP);
            return;
        }
        final String strPlayer = arguments[0];
        final Optional<PendingTeleportation> optPendingTeleportation = teleportManager.getPendingTeleportationRequest(strPlayer, targetPlayer);
        if (optPendingTeleportation.isPresent()){
            teleportManager.cancelTeleportationRequest(optPendingTeleportation.get());
            Messages.sendMessage(targetPlayer, Messages.TPA_DENY_SUCCESSFUL, strPlayer);
            final Player player = CaseStudy.server.getPlayer(strPlayer).get();
            Messages.sendMessage(player, Messages.TPA_REQUEST_REJECTED, targetPlayer.getUsername());
            return;
        }
        Messages.sendMessage(targetPlayer, Messages.TPA_DENY_PLAYER_NOT_FOUND);
    }

    /**
     * This method is suggests commands to the player.
     * @see com.velocitypowered.api.command.SimpleCommand.Invocation
     * @see TeleportManager
     * @param invocation - invocation of the command
     * @return List of suggestions
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
