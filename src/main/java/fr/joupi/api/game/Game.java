package fr.joupi.api.game;

import fr.joupi.api.BooleanWrapper;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.PhaseManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public abstract class Game<G extends GamePlayer> implements Listener {

    private final JavaPlugin plugin;

    private final String name, id;
    private final GameSettings settings;
    private final PhaseManager<?> phaseManager;

    private final List<GameTeam> teams;
    private final ConcurrentMap<UUID, G> players;

    @Setter private GameState state;

    protected Game(JavaPlugin plugin, String name, GameSettings settings) {
        this.plugin = plugin;
        this.name = name;
        this.id = RandomStringUtils.randomAlphanumeric(10);
        this.settings = settings;
        this.phaseManager = new PhaseManager<>(this);
        this.teams = new LinkedList<>();
        this.players = new ConcurrentHashMap<>();
        this.state = GameState.WAIT;
        load();
    }

    public abstract G defaultGamePlayer(UUID uuid);

    private void load() {
        Arrays.stream(GameTeamColor.values()).collect(Collectors.toList()).stream()
                .limit(getSettings().getSize().getTeamNeeded())
                .forEach(gameTeamColor -> getTeams().add(new GameTeam(gameTeamColor)));

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    public void unload() {
        HandlerList.unregisterAll(this);
    }

    public List<G> getAlivePlayers() {
        return getPlayers().values().stream().filter(((Predicate<? super GamePlayer>) GamePlayer::isSpectator).negate()).collect(Collectors.toList());
    }

    public List<G> getSpectators() {
        return getPlayers().values().stream().filter(GamePlayer::isSpectator).collect(Collectors.toList());
    }

    public GameTeam getTeam(String teamName) {
        return getTeams().stream().filter(gameTeam -> gameTeam.getName().equals(teamName)).findFirst().orElse(null);
    }

    public Optional<GameTeam> getTeam(GamePlayer gamePlayer) {
        return getTeams().stream().filter(gameTeam -> gameTeam.isMember(gamePlayer)).findFirst();
    }

    public Optional<G> getPlayer(UUID uuid) {
        return Optional.ofNullable(getPlayers().get(uuid));
    }

    private Optional<GameTeam> getTeamWithLeastPlayers() {
        return getTeams().stream().filter(team -> team.getSize() < getSettings().getSize().getTeamMaxPlayer()).min(Comparator.comparingInt(GameTeam::getSize));
    }

    public void fillTeam() {
        getPlayers().values().forEach(gamePlayer -> getTeamWithLeastPlayers().ifPresent(gameTeam -> gameTeam.addMember(gamePlayer)));
    }

    public void checkGameState(GameState gameState, Runnable runnable) {
        if (getState().equals(gameState))
                runnable.run();
    }

    public void joinGame(Player player) {
        BooleanWrapper.of(getPlayers().containsKey(player.getUniqueId()))
                .ifFalse(() -> {
                    G gamePlayer = defaultGamePlayer(player.getUniqueId());
                    getPlayers().put(player.getUniqueId(), gamePlayer);
                    Bukkit.getServer().getPluginManager().callEvent(new GamePlayerJoinEvent<>(this, gamePlayer));
                    System.out.println(player.getName() + " join " + getFullName() + " game");
                });
    }

    public void leaveGame(UUID uuid) {
        getPlayer(uuid).ifPresent(gamePlayer -> {
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerLeaveEvent<>(this, gamePlayer));
            getTeam(gamePlayer).ifPresent(gameTeam -> gameTeam.removeMember(gamePlayer));
            System.out.println(gamePlayer.getPlayer().getName() + " leave " + getFullName() + " game");
        });
    }

    private void broadcast(String message) {
        getPlayers().values().stream().map(GamePlayer::getPlayer).forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public void broadcast(String... messages) {
        Arrays.asList(messages)
                .forEach(this::broadcast);
    }

    public String getFullName() {
        return getName() + "-" + getSettings().getSize().getName() + "-" + getId();
    }

    public boolean containsPlayer(UUID uuid) {
        return getPlayers().containsKey(uuid);
    }

    public int getAlivePlayersCount() {
        return getAlivePlayers().size();
    }

    public int getSpectatorsCount() {
        return getSpectators().size();
    }

    public int getTeamsCount() {
        return getTeams().size();
    }

    public int getSize() {
        return getPlayers().size();
    }

    public void sendDebugInfoMessage(Player player) {
        player.sendMessage("-----------------------------");
        player.sendMessage("Game: " + getFullName());
        player.sendMessage("Size: type=" + getSettings().getSize().getName() + ", min=" + getSettings().getSize().getMinPlayer() + ", max=" + getSettings().getSize().getMaxPlayer() + ", tn=" + getSettings().getSize().getTeamNeeded() + ", tm=" + getSettings().getSize().getTeamMaxPlayer());
        player.sendMessage("State: " + getState());
        player.sendMessage("Phase: " + getPhaseManager().getCurrentPhase().getClass().getSimpleName());

        player.sendMessage("Teams: " + getTeamsCount());
        getTeams().forEach(gameTeam -> player.sendMessage(gameTeam.getName() + ": " + gameTeam.getMembers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", "))));

        player.sendMessage("Players: " + getSize() + " (" + getAlivePlayersCount() + "|" + getSpectatorsCount() + ")");
        player.sendMessage("Alive players: " + getAlivePlayers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
        player.sendMessage("Spectator players: " + getSpectators().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
        player.sendMessage("-----------------------------");
    }

    public void sendDebugInfoMessage() {
        System.out.println("-----------------------------");
        System.out.println("Game: " + getFullName());
        System.out.println("Size: type=" + getSettings().getSize().getName() + ", min=" + getSettings().getSize().getMinPlayer() + ", max=" + getSettings().getSize().getMaxPlayer() + ", tn=" + getSettings().getSize().getTeamNeeded() + ", tm=" + getSettings().getSize().getTeamMaxPlayer());
        System.out.println("State: " + getState());
        System.out.println("Phase: " + getPhaseManager().getCurrentPhase().getClass().getSimpleName());

        System.out.println("Teams: " + getTeamsCount());
        getTeams().forEach(gameTeam -> System.out.println(gameTeam.getName() + ": " + gameTeam.getMembers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", "))));

        System.out.println("Players: " + getSize() + " (" + getAlivePlayersCount() + "|" + getSpectatorsCount() + ")");
        System.out.println("Alive players: " + getAlivePlayers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
        System.out.println("Spectator players: " + getSpectators().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
        System.out.println("-----------------------------");
    }

}
