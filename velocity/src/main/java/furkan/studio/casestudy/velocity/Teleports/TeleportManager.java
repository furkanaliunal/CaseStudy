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

public class TeleportManager {
    private final ProxyServer server;
    @Getter
    private final ScheduledTask pendingTeleportsTimeOutScheduler;
    private final List<PendingTeleportation> pendingTeleports;
    @Getter
    private final ScheduledTask acceptedTeleportsProcessScheduler;
    private final List<AcceptedTeleportation> acceptedTeleports;

    @Getter
    private final ScheduledTask countdownPerTeleportRequestsScheduler;
    @Getter
    private final Map<UUID, Long> countdownPerTeleportRequestsMap;

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

    public Messages createTeleportationRequest(final Player player, final Player targetPlayer) {
        if (hasSendPendingTeleportRequest(player, targetPlayer)) return Messages.TPA_ALREADY_SEND;
        if (countdownPerTeleportRequestsMap.containsKey(player.getUniqueId())) return Messages.TPA_REQUEST_IN_COUNTDOWN;
        final PendingTeleportation teleportation = new PendingTeleportation(player, targetPlayer);
        pendingTeleports.add(teleportation);
        return Messages.TPA_REQUEST_SENT;
    }
    public long getLeftSecondsForCountDown(final Player player){
        Long value = countdownPerTeleportRequestsMap.get(player.getUniqueId());
        if (value == null) return 0L;
        return (value - System.currentTimeMillis()) / 1000L;
    }

    public boolean hasSendPendingTeleportRequest(final Player player, final Player targetPlayer) {
        for (PendingTeleportation teleportation : pendingTeleports) {
            if (teleportation.getPlayer().getUniqueId().equals(player.getUniqueId()) && teleportation.getTargetPlayer().getUniqueId().equals(targetPlayer.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    public boolean hasReceivedPendingTeleportRequest(final Player target) {
        return pendingTeleports.stream().anyMatch(teleportation -> teleportation.getTargetPlayer().getUniqueId().equals(target.getUniqueId()));
    }

    public void processTeleportationRequest(final PendingTeleportation teleportation) {
        pendingTeleports.remove(teleportation);
        acceptedTeleports.add(teleportation.getAcceptedResult());
    }
    public void cancelTeleportationRequest(final PendingTeleportation teleportation){
        pendingTeleports.remove(teleportation);
    }

    public Collection<String> getReceivedPendingTeleportationRequestsOfPlayer(final Player player) {
        return pendingTeleports.stream().filter(teleportation -> {
            if (teleportation.getTargetPlayer().getUniqueId().equals(player.getUniqueId())){
                return true;
            }
            return false;
        }).map(teleportation -> teleportation.getPlayer().getUsername()).collect(Collectors.toList());
    }
    public Optional<PendingTeleportation> getPendingTeleportationRequest(final String player, final Player targetPlayer) {
        return pendingTeleports.stream().filter(teleportation -> {
            if (teleportation.getPlayer().getUsername().equals(player) && teleportation.getTargetPlayer().getUniqueId().equals(targetPlayer.getUniqueId())){
                return true;
            }
            return false;
        }).findFirst();
    }
}
