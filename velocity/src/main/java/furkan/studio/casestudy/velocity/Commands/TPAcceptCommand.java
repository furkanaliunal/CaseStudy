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

public class TPAcceptCommand implements SimpleCommand {

    private final TeleportManager teleportManager;

    public TPAcceptCommand() {
        this.teleportManager = CaseStudy.instance.getTeleportManager();
    }

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
