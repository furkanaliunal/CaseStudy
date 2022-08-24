package furkan.studio.casestudy.velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import furkan.studio.casestudy.velocity.CaseStudy;
import furkan.studio.casestudy.velocity.Utils.Messages;
import furkan.studio.casestudy.velocity.TeleportRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TPACommand implements SimpleCommand {

    private final CaseStudy plugin;


    public TPACommand() {
        this.plugin = CaseStudy.instance;
    }
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

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }

    private List<String> getAllPlayers(){
        final List<String> players = new ArrayList<>();
        CaseStudy.server.getAllServers().forEach(server -> {
            server.getPlayersConnected().forEach(player -> {
                players.add(player.getUsername());
            });
        });
        return players;
    }
    private Optional<Player> getPlayer(String name){
        return CaseStudy.server.getPlayer(name);
    }
}
