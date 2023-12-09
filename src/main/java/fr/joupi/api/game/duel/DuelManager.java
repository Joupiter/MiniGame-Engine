package fr.joupi.api.game.duel;

import com.google.common.collect.ImmutableList;
import fr.joupi.api.Spigot;
import fr.joupi.api.Utils;
import fr.joupi.api.duelgame.DuelGame;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class DuelManager {

    private final Spigot plugin;
    private final ConcurrentMap<UUID, DuelRequest> requests;

    public DuelManager(Spigot plugin) {
        this.plugin = plugin;
        this.requests = new ConcurrentHashMap<>();
    }

    public Optional<DuelRequest> getRequest(UUID sender, UUID target) {
        return getOutgoingRequests(sender).stream().filter(request -> request.getTarget().equals(target)).findFirst();
    }

    public Optional<DuelRequest> getRequest(Player sender, Player target) {
        return getRequest(sender.getUniqueId(), target.getUniqueId());
    }

    public void addRequest(DuelRequest request) {
        getRequests().put(request.getId(), request);
        scheduleRequest(request.getSender(), request.getTarget());
    }

    public void removeRequest(UUID sender, UUID target) {
        getRequest(sender, target).map(DuelRequest::getId).ifPresent(getRequests()::remove);
    }

    public void sendRequest(Player sender, Player target, DuelRequest request) {
        Utils.ifPresentOrElse(getRequest(sender, target),
                k -> sender.sendMessage(String.format("[Duel] Le joueur %s a déjà une invitation de duel en attente.", target.getName())),
                () -> {
                    addRequest(request);
                    target.sendMessage(String.format("[Duel] Vous avez été invité par %s ! Tapez /duel accept %s pour rejoindre.", sender.getName(), sender.getName()));
                    sender.sendMessage(String.format("[Duel] Invitation envoyée à %s !", target.getName()));
                });
    }

    public void acceptRequest(DuelRequest request) {
        getRequests().remove(request.getId());
        getPlugin().getGameManager().addGame("duel", new DuelGame(getPlugin(), request));
    }

    public void cancelRequest(Player sender, Player target) {
        Utils.ifPresentOrElse(getRequest(sender, target),
                uuid -> {
                    removeRequest(sender.getUniqueId(), target.getUniqueId());
                    target.sendMessage(String.format("[Duel] L'invitation de %s a été annulée.", sender.getName()));
                    sender.sendMessage(String.format("[Duel] L'invitation pour %s a été annulée.", target.getName()));
                }, () -> sender.sendMessage(String.format("[Duel] Aucune invitation trouvée pour %s.", target.getName())));
    }

    private void scheduleRequest(UUID sender, UUID target) {
        getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(),
                () -> getRequest(sender, target).ifPresent(request -> {
                    request.getSenderPlayer().ifPresent(player -> player.sendMessage("[Duel] L'invitation a expirer"));
                    getRequests().remove(request.getId());
                }), 500);
    }

    public void onLeave(Player player) {
        getAllRequests(player.getUniqueId()).stream().map(DuelRequest::getId).forEach(getRequests()::remove);
    }

    public List<DuelRequest> getAllRequests(UUID uuid) {
        return ImmutableList.<DuelRequest>builder().addAll(getRequests(request -> request.getTarget().equals(uuid) || request.getSender().equals(uuid))).build();
    }

    public List<DuelRequest> getRequests(Predicate<DuelRequest> predicate) {
        return getRequests().values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<DuelRequest> getIncomingRequests(UUID uuid) {
        return getRequests(request -> request.getTarget().equals(uuid));
    }

    public List<DuelRequest> getOutgoingRequests(UUID uuid) {
        return getRequests(request -> request.getSender().equals(uuid));
    }

}