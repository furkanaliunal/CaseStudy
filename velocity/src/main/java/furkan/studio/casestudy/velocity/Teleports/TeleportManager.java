package furkan.studio.casestudy.velocity.Teleports;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import furkan.studio.casestudy.velocity.CaseStudy;
import furkan.studio.casestudy.velocity.Teleports.TeleportTypes.AcceptedTeleportation;
import furkan.studio.casestudy.velocity.Teleports.TeleportTypes.PendingTeleportation;
import furkan.studio.casestudy.velocity.Utils.Messages;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by OnwexryS
 * This class is used store any teleportation request and manage them.
 * @see ProxyServer - ProxyServer class is used to initialize {@link ScheduledTask}.
 * @see ScheduledTask - ScheduledTasks class is used to run tasks.
 * @see TimeUnit - TimeUnit class is used to set repeat interval for ScheduledTasks.
 * @see Messages - Messages class is used to send messages to players.
 * @see AcceptedTeleportation - AcceptedTeleportation class is used to store accepted teleportation request.
 * @see PendingTeleportation - PendingTeleportation class is used to store pending teleportation request.
 */
public class TeleportManager {
    /**
     * Insance of ProxyServer.
     */
    private final ProxyServer server;
    /**
     * Task to check pending teleportation requests.
     */
    @Getter
    private final ScheduledTask pendingTeleportsTimeOutScheduler;
    /**
     * List of pending teleportation requests.
     */
    private final List<PendingTeleportation> pendingTeleports;
    /**
     * Task to check accepted teleportation requests.
     */
    @Getter
    private final ScheduledTask acceptedTeleportsProcessScheduler;
    /**
     * List of accepted teleportation requests to process.
     */
    private final List<AcceptedTeleportation> acceptedTeleports;
    /**
     * Task to add countdown for teleportation requests.
     */
    @Getter
    private final ScheduledTask countdownPerTeleportRequestsScheduler;
    /**
     * List of countdown for teleportation requests.
     * If player's uuid is not exists in map, it will be added to map right after it is created.
     */
    @Getter
    private final Map<UUID, Long> countdownPerTeleportRequestsMap;

    /**
     * Constructor for TeleportManager.
     * It runs every single taks in separate thread, belong to this class
     * @see ScheduledTask - ScheduledTasks class is used to run tasks.
     * @see TimeUnit - TimeUnit class is used to set repeat interval for ScheduledTasks.
     * @see ProxyServer - ProxyServer class is used to initialize {@link ScheduledTask}.
     */
    public TeleportManager(){
        server = CaseStudy.server;
        this.pendingTeleports = new ArrayList<>();
        this.acceptedTeleports = new ArrayList<>();
        this.countdownPerTeleportRequestsMap = new HashMap<>();

        this.pendingTeleportsTimeOutScheduler = server.getScheduler().buildTask(CaseStudy.instance, () -> {
            final List<PendingTeleportation> toRemove = new ArrayList<>();
            pendingTeleports.forEach(tp -> {
                if (tp.getEndTime() <= System.currentTimeMillis()){
                    toRemove.add(tp);
                    Messages.sendMessage(tp.getPlayer(), Messages.TPA_REQUEST_TIMED_OUT, tp.getTargetPlayer().getUsername());
                }
            });
            pendingTeleports.removeAll(toRemove);
        }).repeat(1L, TimeUnit.SECONDS).schedule();

        this.acceptedTeleportsProcessScheduler = server.getScheduler().buildTask(CaseStudy.instance, () -> {
            if (acceptedTeleports.isEmpty()) return;
            final AcceptedTeleportation processingTeleportation = acceptedTeleports.remove(0);
            processingTeleportation.run();
        }).repeat(50L, TimeUnit.MILLISECONDS).schedule();

        this.countdownPerTeleportRequestsScheduler = server.getScheduler().buildTask(CaseStudy.instance, () -> {
            long now = System.currentTimeMillis();
            countdownPerTeleportRequestsMap.forEach((uuid, endTime) -> {
                if (endTime <= now){
                    countdownPerTeleportRequestsMap.remove(uuid);
                }
            });
        }).repeat(1L, TimeUnit.SECONDS).schedule();
    }

    /**
     * Add pending teleportation request to list.
     * @see PendingTeleportation - PendingTeleportation class is used to store pending teleportation request.
     * @see TeleportManager#createTeleportationRequest(Player, Player)
     * @see Messages - Messages class is used to send messages to players.
     * @param player - player who is sending teleportation request.
     * @param targetPlayer - player who is receiving teleportation request.
     * @return
     */
    public Messages createTeleportationRequest(final Player player, final Player targetPlayer) {
        if (hasSendPendingTeleportRequest(player, targetPlayer)) return Messages.TPA_ALREADY_SEND;
        if (countdownPerTeleportRequestsMap.containsKey(player.getUniqueId())) return Messages.TPA_REQUEST_IN_COUNTDOWN;
        final PendingTeleportation teleportation = new PendingTeleportation(player, targetPlayer);
        pendingTeleports.add(teleportation);
        return Messages.TPA_REQUEST_SENT;
    }

    /**
     * Util class to get countdown seconds for teleportation request.
     * @param player - player who is sending teleportation request.
     * @return - countdown for teleportation requests.
     */
    public long getLeftSecondsForCountDown(final Player player){
        Long value = countdownPerTeleportRequestsMap.get(player.getUniqueId());
        if (value == null) return 0L;
        return (value - System.currentTimeMillis()) / 1000L;
    }

    /**
     * Check if player has already sent pending teleportation request to other player.
     * @param player - player who is sending teleportation request.
     * @param targetPlayer - player who is receiving teleportation request.
     * @return
     */
    public boolean hasSendPendingTeleportRequest(final Player player, final Player targetPlayer) {
        for (PendingTeleportation teleportation : pendingTeleports) {
            if (teleportation.getPlayer().getUniqueId().equals(player.getUniqueId()) && teleportation.getTargetPlayer().getUniqueId().equals(targetPlayer.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes pending teleportation request from list.
     * Adds accepted teleportation request to acceptedTeleports list.
     * @see PendingTeleportation#getAcceptedResult() - This method is used to get AcceptedTeleportation clone of this object.
     * @param teleportation
     */
    public void processTeleportationRequest(final PendingTeleportation teleportation) {
        pendingTeleports.remove(teleportation);
        acceptedTeleports.add(teleportation.getAcceptedResult());
    }

    /**
     * Cancels pending teleportation request.
     * @param teleportation
     */
    public void cancelTeleportationRequest(final PendingTeleportation teleportation){
        pendingTeleports.remove(teleportation);
    }

    /**
     * Gets pending teleportation request of player from list.
     * @param player
     * @return
     */
    public Collection<String> getReceivedPendingTeleportationRequestsOfPlayer(final Player player) {
        return pendingTeleports.stream().filter(teleportation -> {
            if (teleportation.getTargetPlayer().getUniqueId().equals(player.getUniqueId())){
                return true;
            }
            return false;
        }).map(teleportation -> teleportation.getPlayer().getUsername()).collect(Collectors.toList());
    }

    /**
     * Gets pending teleportation request of player to a target player from list.
     * @param player
     * @param targetPlayer
     * @return
     */
    public Optional<PendingTeleportation> getPendingTeleportationRequest(final String player, final Player targetPlayer) {
        return pendingTeleports.stream().filter(teleportation -> {
            if (teleportation.getPlayer().getUsername().equals(player) && teleportation.getTargetPlayer().getUniqueId().equals(targetPlayer.getUniqueId())){
                return true;
            }
            return false;
        }).findFirst();
    }
}
