package io.github.furkanaliunal.casestudy.velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import io.github.furkanaliunal.casestudy.velocity.CaseStudy;
import io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportManager;
import io.github.furkanaliunal.casestudy.velocity.Utils.Messages;
import io.github.furkanaliunal.casestudy.velocity.TeleportRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Onwexrys
 * This class is used to send a teleportation request or see the history of teleports.
 * @see SimpleCommand for more information.
 */
public class TPACommand implements SimpleCommand {
    /**
     * Instance of the plugin to reduce unnecessary code.
     */
    private final CaseStudy plugin;

    /**
     * Constructor for TPACommand.
     */
    public TPACommand() {
        this.plugin = CaseStudy.instance;
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
        CommandSource source = invocation.source();
        if (!(source instanceof Player)){
            Messages.sendMessage(source, Messages.MUST_BE_PLAYER);
            return;
        }
        String[] arguments = invocation.arguments();
        Player player = (Player) source;
        if (arguments.length == 1){
            final String target = arguments[0];
            if (target.equalsIgnoreCase("list")){
                Messages.sendTPAHistory(player);
                return;
            }
            Optional<Player> optTargetPlayer = getPlayer(target);
            if (!optTargetPlayer.isPresent()){
                Messages.sendMessage(source, Messages.TPA_PLAYER_NOT_FOUND);
                return;
            }
            Player targetPlayer = optTargetPlayer.get();
            if (player.getUniqueId().equals(targetPlayer.getUniqueId())){
                Messages.sendMessage(source, Messages.TPA_CANT_TPA_TO_YOURSELF);
                return;
            }
            final String targetServer = targetPlayer.getCurrentServer().get().getServer().getServerInfo().getName();
            if (!TeleportRules.canPlayerJoinServer(targetServer, player)) {
                Messages.sendMessage(source, Messages.TPA_CANT_TPA_THIS_SERVER);
                return;
            }
            final Messages result = plugin.getTeleportManager().createTeleportationRequest(player, targetPlayer);
            if (result == Messages.TPA_ALREADY_SEND){
                Messages.sendMessage(source, result);
                return;
            }
            if (result == Messages.TPA_REQUEST_IN_COUNTDOWN){
                Messages.sendMessage(source, result, plugin.getTeleportManager().getLeftSecondsForCountDown(player)+"");
                return;
            }
            Messages.sendMessage(source, Messages.TPA_REQUEST_SENT, targetPlayer.getUsername());
            plugin.getTeleportManager().getCountdownPerTeleportRequestsMap().put(player.getUniqueId(), TeleportRules.getCountdownPerTeleportRequestInMillis() + System.currentTimeMillis());
            Messages.sendMessage(targetPlayer, Messages.TPA_REQUEST_RECEIVED, player.getUsername());
            Messages.sendMessage(targetPlayer, Messages.TPA_CONFIRM, player.getUsername());
            Messages.sendMessage(targetPlayer, Messages.TPA_DENY, player.getUsername());
            return;
        }
        Messages.sendMessage(player, Messages.TPA_COMMAND_HELP);

    }


    /**
     * This method is suggests commands to the player.
     * @see com.velocitypowered.api.command.SimpleCommand.Invocation
     * @see TeleportManager
     * @param invocation - invocation of the command
     * @return list of commands
     */
    @Override
    public List<String> suggest(Invocation invocation) {
        final String[] arguments = invocation.arguments();
        final List<String> suggestions = new ArrayList<>();
        if (arguments.length > 1) return suggestions;
        suggestions.add("list");
        suggestions.addAll(getAllPlayers());
        if (invocation.source() instanceof Player){
            suggestions.remove(((Player) invocation.source()).getUsername());
        }
        return suggestions;
    }

    /**
     * This method is used to check if player has permission to run this command
     * @see SimpleCommand
     * @param invocation - invocation of the command
     * @return true if player has permission, false otherwise
     */
    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }

    /**
     * Util method to names of all players
     * @return
     */
    private List<String> getAllPlayers(){
        final List<String> players = new ArrayList<>();
        CaseStudy.server.getAllServers().forEach(server -> {
            server.getPlayersConnected().forEach(player -> {
                players.add(player.getUsername());
            });
        });
        return players;
    }

    /**
     * Util method to get player by name
     * @param name - name of the player
     * @return player if found, empty otherwise
     */
    private Optional<Player> getPlayer(String name){
        return CaseStudy.server.getPlayer(name);
    }
}
