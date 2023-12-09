package fr.joupi.api.game.party;

import com.google.common.collect.ImmutableList;
import fr.joupi.api.Utils;
import fr.joupi.api.game.GameManager;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class GamePartyManager {

    private final GameManager gameManager;

    private final List<GameParty> parties;
    private final List<GamePartyRequest> requests;

    public GamePartyManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.parties = new ArrayList<>();
        this.requests = new ArrayList<>();
    }

    public Optional<GameParty> getParty(Player player) {
        return getParty(player.getUniqueId());
    }

    public Optional<GameParty> getParty(UUID uuid) {
        return getParties().stream().filter(gameParty -> gameParty.isMember(uuid)).findFirst();
    }

    public Optional<GameParty> getPartyLedByPlayer(Player player) {
        return getPartyLedByPlayer(player.getUniqueId());
    }

    public Optional<GameParty> getPartyLedByPlayer(UUID uuid) {
        return getParties().stream().filter(gameParty -> gameParty.isLeader(uuid)).findFirst();
    }

    public Optional<GamePartyRequest> getRequest(UUID sender, UUID target) {
        return getOutgoingRequests(sender).stream().filter(request -> request.getTarget().equals(target)).findFirst();
    }

    public Optional<GamePartyRequest> getRequest(Player sender, Player target) {
        return getOutgoingRequests(sender.getUniqueId()).stream().filter(request -> request.getTarget().equals(target.getUniqueId())).findFirst();
    }

    public void addParty(GameParty gameParty) {
        Utils.ifEmpty(getParty(gameParty.getLeader()), () -> {
            getParties().add(gameParty);
            Utils.debug("Party - party of {0} has been added with name ({1}) and {2} max players",  gameParty.getPlayer().getName(), gameParty.getName(), gameParty.getMaxMembers());
        });
    }

    public void removeParty(Player player) {
        getParty(player).ifPresent(this::removeParty);
    }

    public void removeParty(GameParty gameParty) {
        getParties().remove(gameParty);
        Utils.debug("Party - party of {0} has been removed", gameParty.getPlayer().getName());
    }

    public void canRemoveParty(GameParty gameParty) {
        Optional.of(gameParty).filter(GameParty::isEmpty).ifPresent(this::removeParty);
    }

    public void joinParty(Player player, GameParty gameParty) {
        if ((gameParty.isOpened() || canJoin(player, gameParty.getPlayer())) && !gameParty.isComplete()) {
            removeRequest(gameParty.getLeader(), player.getUniqueId());
            leaveParty(player);
            gameParty.addMember(player.getUniqueId());
            Utils.debug("Party - {0} join {1} party", player.getName(), gameParty.getPlayer().getName());
        }
    }

    public void joinParty(Player player, Player leader) {
        getParty(leader).ifPresent(gameParty -> joinParty(player, gameParty));
    }

    public void leaveParty(Player player, GameParty gameParty) {
        gameParty.canSetNewRandomLeader(player);
        gameParty.removeMember(player.getUniqueId());
        canRemoveParty(gameParty);
        Utils.debug("Party - {0} leave {1} party", player.getName(), gameParty.getPlayer().getName());
    }

    public void leaveParty(Player player) {
        getParty(player).ifPresent(gameParty -> leaveParty(player, gameParty));
    }

    public void onLeave(Player player) {
        leaveParty(player);
        getAllRequests(player.getUniqueId()).forEach(getRequests()::remove);
    }

    public void addRequest(UUID sender, UUID target) {
        getRequests().add(new GamePartyRequest(sender, target));
        scheduleRequest(sender, target);
    }

    public void removeRequest(UUID sender, UUID target) {
        getOutgoingRequests(sender).stream().filter(request -> request.getTarget().equals(target)).findFirst().ifPresent(getRequests()::remove);
    }

    public void sendRequest(Player sender, Player target) {
        Utils.ifPresentOrElse(getRequest(sender, target),
                request -> sender.sendMessage("Le joueur " + target.getName() + " a déjà une invitation en attente."),
                () -> {
                    addRequest(sender.getUniqueId(), target.getUniqueId());
                    target.sendMessage("Vous avez été invité par " + sender.getName() + " ! Tapez /party join " + sender.getName() + " pour rejoindre.");
                    sender.sendMessage("Invitation envoyée à " + target.getName() + " !");
                });
    }

    public void cancelRequest(Player leader, Player invited) {
        Utils.ifPresentOrElse(getRequest(leader, invited),
                uuid -> {
                    removeRequest(leader.getUniqueId(), invited.getUniqueId());
                    invited.sendMessage("L'invitation de " + leader.getName() + " a été annulée.");
                    leader.sendMessage("L'invitation pour " + invited.getName() + " a été annulée.");
                }, () -> leader.sendMessage("Aucune invitation trouvée pour " + invited.getName() + "."));
    }

    private void scheduleRequest(UUID sender, UUID target) {
        getGameManager().getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getGameManager().getPlugin(),
                () -> getRequest(sender, target).ifPresent(getRequests()::remove), 100);
    }

    public List<GameParty> getReachableParty() {
        return getParties().stream().filter(gameParty -> !gameParty.isComplete()).collect(Collectors.toList());
    }

    public List<GameParty> getCompleteParty() {
        return getParties().stream().filter(GameParty::isComplete).collect(Collectors.toList());
    }

    public List<Player> getPlayersNotInGame(GameParty gameParty) {
        return gameParty.getPlayers().stream()
                .filter(getGameManager()::isNotInGame)
                .collect(Collectors.toList());
    }

    public List<Player> getPlayersInGame(GameParty gameParty) {
        return gameParty.getPlayers().stream()
                .filter(getGameManager()::isInGame)
                .collect(Collectors.toList());
    }

    public ImmutableList<GamePartyRequest> getAllRequests(UUID uuid) {
        return ImmutableList.<GamePartyRequest>builder().addAll(getIncomingRequests(uuid)).addAll(getOutgoingRequests(uuid)).build();
    }

    public List<GamePartyRequest> getIncomingRequests(UUID uuid) {
        return getRequests().stream().filter(request -> request.getTarget().equals(uuid)).collect(Collectors.toList());
    }

    public List<GamePartyRequest> getOutgoingRequests(UUID uuid) {
        return getRequests().stream().filter(request -> request.getSender().equals(uuid)).collect(Collectors.toList());
    }

    public boolean canJoin(Player invited, Player leader) {
        return getRequest(leader, invited).isPresent();
    }

    public boolean isPartyLeader(Player player) {
        return getParties().stream().anyMatch(gameParty -> gameParty.getLeader().equals(player.getUniqueId()));
    }

    public boolean isInParty(Player player) {
        return getParties().stream().map(GameParty::getMembers).anyMatch(uuids -> uuids.contains(player.getUniqueId()));
    }

    public void sendInvitationsDebug(Player player) {
        getIncomingRequests(player.getUniqueId()).forEach(request -> player.sendMessage(request.getSenderPlayer().getName() + " a inviter " + request.getTargetPlayer().getName()));
        player.sendMessage("--------------------------");
        getOutgoingRequests(player.getUniqueId()).forEach(request -> player.sendMessage(request.getSenderPlayer().getName() + " a inviter " + request.getTargetPlayer().getName()));
    }

}