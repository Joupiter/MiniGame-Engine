package fr.joupi.api.game.party;

import com.google.common.collect.Lists;
import fr.joupi.api.Utils;
import fr.joupi.api.game.GameManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GamePartyManager {

    private final GameManager gameManager;

    private final List<GameParty> parties;
    private final Map<UUID, List<UUID>> invitations;

    public GamePartyManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.parties = new ArrayList<>();
        this.invitations = new HashMap<>();
    }

    public Optional<GameParty> getParty(Player player) {
        return getParties().stream().filter(gameParty -> gameParty.isMember(player.getUniqueId())).findFirst();
    }

    public void addParty(GameParty gameParty) {
        if (getParties().stream().noneMatch(party -> party.isMember(gameParty.getLeader()))) {
            getParties().add(gameParty);
            Utils.debug("Party - party of {0} has been added with name ({1}) and {2} max players",  gameParty.getPlayer().getName(), gameParty.getName(), gameParty.getMaxMembers());
        }
    }

    public void removeParty(Player player) {
        getParty(player).ifPresent(this::removeParty);
    }

    public void removeParty(GameParty gameParty) {
        getParties().remove(gameParty);
        Utils.debug("Party - party of {0} has been removed", gameParty.getPlayer().getName());
    }

    public void joinParty(Player player, GameParty gameParty) {
        if ((gameParty.isOpened() || canJoin(player, gameParty.getPlayer())) && !gameParty.isComplete()) {
            getPendingInvitation(gameParty.getPlayer()).remove(player.getUniqueId());
            leaveParty(player);
            gameParty.addMember(player.getUniqueId());
            Utils.debug("Party - {0} join {1} party {2}", player.getName(), gameParty.getPlayer().getName());
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
        getInvitations().remove(player.getUniqueId());
        getInvitations().values().stream().flatMap(List::stream).collect(Collectors.toList()).remove(player.getUniqueId());
    }

    public void sendInvitation(Player leader, Player invited) {
        Utils.ifPresentOrElse(getPendingInvitation(leader).stream().filter(invited.getUniqueId()::equals).findFirst(),
                uuid -> leader.sendMessage("Le joueur " + invited.getName() + " a déjà une invitation en attente."),
                () -> {
                    getInvitations().computeIfAbsent(leader.getUniqueId(), k -> Lists.newArrayList()).add(invited.getUniqueId());
                    scheduleInvitation(leader, invited);
                    invited.sendMessage("Vous avez été invité par " + leader.getName() + " ! Tapez /party join " + leader.getName() + " pour rejoindre.");
                    leader.sendMessage("Invitation envoyée à " + invited.getName() + " !");
                });
    }

    public void cancelInvitation(Player leader, Player invited) {
        Utils.ifPresentOrElse(getPendingInvitation(leader).stream().filter(invited.getUniqueId()::equals).findFirst(),
                uuid -> {
                    getInvitations().get(leader.getUniqueId()).remove(uuid);
                    invited.sendMessage("L'invitation de " + leader.getName() + " a été annulée.");
                    leader.sendMessage("L'invitation pour " + invited.getName() + " a été annulée.");
                }, () -> leader.sendMessage("Aucune invitation trouvée pour " + invited.getName() + "."));
    }

    private void scheduleInvitation(Player leader, Player invited) {
        getGameManager().getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getGameManager().getPlugin(), () ->
                getPendingInvitation(leader).stream().filter(invited.getUniqueId()::equals).findFirst().ifPresent(uuid -> {
                    getPendingInvitation(leader).remove(invited.getUniqueId());
                    Utils.debug("Party - L'invitation de {0} a {1} a expirer", leader.getName(), invited.getName());
                }), 1200);
    }

    public List<GameParty> getReachableParty() {
        return getParties().stream().filter(gameParty -> !gameParty.isComplete()).collect(Collectors.toList());
    }

    public List<GameParty> getCompleteParty() {
        return getParties().stream().filter(GameParty::isComplete).collect(Collectors.toList());
    }

    public List<UUID> getPendingInvitation(Player player) {
        return getInvitations().getOrDefault(player.getUniqueId(), Collections.emptyList());
    }

    public void canRemoveParty(GameParty gameParty) {
        if (gameParty.isEmpty())
            removeParty(gameParty);
    }

    public boolean canJoin(Player invited, Player leader) {
        return getPendingInvitation(leader).contains(invited.getUniqueId());
    }

    public boolean isPartyLeader(Player player) {
        return getParties().stream().anyMatch(gameParty -> gameParty.getLeader().equals(player.getUniqueId()));
    }

    public boolean isInParty(Player player) {
        return getParties().stream().map(GameParty::getMembers).anyMatch(uuids -> uuids.contains(player.getUniqueId()));
    }

    public void sendInvitationsDebug(Player player) {
        getInvitations().forEach((uuid, uuids) -> player.sendMessage(Bukkit.getPlayer(uuid).getName() + " - " + uuids.stream().map(Bukkit::getPlayer).map(Player::getName).collect(Collectors.joining(", "))));
    }

}