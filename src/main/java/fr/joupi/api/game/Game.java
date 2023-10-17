package fr.joupi.api.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.joupi.api.game.entity.GameEntityManager;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.host.GameHost;
import fr.joupi.api.game.host.GameHostState;
import fr.joupi.api.game.listener.GameListenerWrapper;
import fr.joupi.api.game.phase.PhaseManager;
import fr.joupi.api.game.team.GameTeam;
import fr.joupi.api.game.team.GameTeamColor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public abstract class Game<G extends GamePlayer, S extends GameSettings> implements Listener {

    private final JavaPlugin plugin;

    private final String name, id;
    private final S settings;

    private final PhaseManager<?> phaseManager;
    private final GameEntityManager gameEntityManager;
    @Setter private GameHost<?> gameHost;

    private final List<GameListenerWrapper<?>> listeners;
    private final List<GameTeam> teams;
    private final ConcurrentMap<UUID, G> players;
    private final Gson gson;

    @Setter private GameState state;

    protected Game(JavaPlugin plugin, String name, S settings) {
        this.plugin = plugin;
        this.name = name;
        this.id = RandomStringUtils.randomAlphanumeric(10);
        this.settings = settings;
        this.phaseManager = new PhaseManager<>(this);
        this.gameEntityManager = new GameEntityManager(plugin);
        this.listeners = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.players = new ConcurrentHashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.state = GameState.WAIT;
        load();
    }

    public abstract G defaultGamePlayer(UUID uuid, boolean spectator);

    private void load() {
        Arrays.stream(GameTeamColor.values()).collect(Collectors.toList()).stream()
                .limit(getSettings().getGameSize().getTeamNeeded())
                .forEach(gameTeamColor -> getTeams().add(new GameTeam(gameTeamColor)));

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        System.out.println(getFullName() + " loaded");
    }

    public void unload() {
        getPhaseManager().unregisterPhases();
        getListeners().forEach(HandlerList::unregisterAll);
        HandlerList.unregisterAll(this);
        System.out.println(getFullName() + " unloaded");
    }

    public void registerListeners(GameListenerWrapper<?>... listeners) {
        Arrays.asList(listeners)
                .forEach(this::registerListener);
    }

    public void registerListener(GameListenerWrapper<?> listener) {
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        getListeners().add(listener);
    }

    public List<G> getAlivePlayers() {
        return getPlayers().values().stream().filter(((Predicate<? super GamePlayer>) GamePlayer::isSpectator).negate()).collect(Collectors.toList());
    }

    public List<G> getSpectators() {
        return getPlayers().values().stream().filter(GamePlayer::isSpectator).collect(Collectors.toList());
    }

    public List<G> getPlayersWithTeam() {
        return getPlayers().values().stream().filter(this::haveTeam).collect(Collectors.toList());
    }

    public List<G> getPlayersWithoutTeam() {
        return getPlayers().values().stream().filter(gamePlayer -> !haveTeam(gamePlayer)).collect(Collectors.toList());
    }

    public List<GameTeam> getAliveTeams() {
        return getTeams().stream()
                .filter(((Predicate<? super GameTeam>) GameTeam::isNoPlayersAlive).negate())
                .collect(Collectors.toList());
    }

    public List<GameTeam> getReachableTeams() {
        return getTeams().stream().filter(gameTeam -> gameTeam.getSize() < getSettings().getGameSize().getTeamMaxPlayer()).collect(Collectors.toList());
    }

    public GameTeam getTeam(String teamName) {
        return getTeams().stream().filter(gameTeam -> gameTeam.getName().equals(teamName)).findFirst().orElse(null);
    }

    public Optional<GameTeam> getTeam(GamePlayer gamePlayer) {
        return getTeams().stream().filter(gameTeam -> gameTeam.isMember(gamePlayer)).findFirst();
    }

    public Optional<GameTeam> getRandomTeam() {
        return getReachableTeams().stream()
                .skip(getReachableTeams().isEmpty() ? 0 : new Random().nextInt(getReachableTeams().size())).findFirst();
    }

    private Optional<GameTeam> getTeamWithLeastPlayers() {
        return getTeams().stream()
                .filter(team -> team.getSize() < getSettings().getGameSize().getTeamMaxPlayer())
                .min(Comparator.comparingInt(GameTeam::getSize));
    }

    public void addPlayerToTeam(GamePlayer gamePlayer, GameTeam gameTeam) {
        removePlayerToTeam(gamePlayer);
        gameTeam.addMember(gamePlayer);
    }

    public void removePlayerToTeam(GamePlayer gamePlayer) {
        getTeam(gamePlayer).ifPresent(team -> team.removeMember(gamePlayer));
    }

    public void fillTeam() {
        getPlayersWithoutTeam().forEach(gamePlayer -> getTeamWithLeastPlayers().ifPresent(gameTeam -> gameTeam.addMember(gamePlayer)));
    }

    public Optional<G> getPlayer(UUID uuid) {
        return Optional.ofNullable(getPlayers().get(uuid));
    }

    public void checkSetting(boolean setting, Runnable runnable) {
        checkSetting(setting, runnable, () -> {});
    }

    public void checkSetting(boolean setting, Runnable trueRunnable, Runnable falseRunnable) {
        if (setting) trueRunnable.run();
        else falseRunnable.run();
    }

    public void ifHostedGame(Runnable runnable) {
        Optional.ofNullable(getGameHost())
                .ifPresent(host -> runnable.run());
    }

    public void ifHostedGame(Consumer<GameHost<?>> consumer) {
        ifHostedGame(() -> consumer.accept(getGameHost()));
    }

    public void ifHostedGame(Predicate<GameHost<?>> predicate, Runnable runnable) {
        Optional.ofNullable(getGameHost())
                .filter(predicate)
                .ifPresent(host -> runnable.run());
    }

    public void checkGameHostState(GameHostState hostState, Runnable runnable) {
        Optional.ofNullable(getGameHost())
                .filter(host -> host.getHostState().equals(hostState))
                .ifPresent(host -> runnable.run());
    }

    public void checkGameState(GameState gameState, Runnable runnable) {
        if (getState().equals(gameState))
            runnable.run();
    }

    public void joinGame(Player player) {
        joinGame(player, false);
    }

    public void joinGame(Player player, boolean spectator) {
        if (!getPlayers().containsKey(player.getUniqueId())) {
            G gamePlayer = defaultGamePlayer(player.getUniqueId(), spectator);
            getPlayers().put(player.getUniqueId(), gamePlayer);
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerJoinEvent<>(this, gamePlayer));
            System.out.println(player.getName() + (gamePlayer.isSpectator() ? " spectate " : " join ")+ getFullName() + " game");
        }
    }

    public void leaveGame(UUID uuid) {
        getPlayer(uuid).ifPresent(gamePlayer -> {
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerLeaveEvent<>(this, gamePlayer));
            removePlayerToTeam(gamePlayer);
            System.out.println(gamePlayer.getPlayer().getName() + " leave " + getFullName() + " game");
        });
    }

    public void endGame(GameManager gameManager) {
        getPlayers().values().stream().map(GamePlayer::getUuid).forEach(this::leaveGame);
        unload();
        gameManager.removeGame(this);
        System.out.println("END OF GAME : " + getFullName());
    }

    private void broadcast(String message) {
        getPlayers().values().forEach(gamePlayer -> gamePlayer.sendMessage(message));
    }

    public void broadcast(String... messages) {
        Arrays.asList(messages)
                .forEach(this::broadcast);
    }

    public String getFullName() {
        return getName() + (isGameHost() ? "Host" : "") + "-" + getSettings().getGameSize().getName() + "-" + getId();
    }

    public boolean isGameHost() {
        return Optional.ofNullable(getGameHost()).isPresent();
    }

    public boolean haveTeam(G gamePlayer) {
        return getTeam(gamePlayer).isPresent();
    }

    public boolean containsPlayer(UUID uuid) {
        return getPlayers().containsKey(uuid);
    }

    public boolean oneTeamAlive() {
        return getAliveTeamsCount() == 1;
    }

    public boolean canStart() {
        return getAlivePlayersCount() >= getSettings().getGameSize().getMinPlayer();
    }

    public boolean isFull() {
        return getAlivePlayersCount() == getSettings().getGameSize().getMaxPlayer();
    }

    public boolean canJoin() {
        return getAlivePlayersCount() < getSettings().getGameSize().getMaxPlayer();
    }

    public int getAliveTeamsCount() {
        return getAliveTeams().size();
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
        player.sendMessage("Size: type=" + getSettings().getGameSize().getName() + ", min=" + getSettings().getGameSize().getMinPlayer() + ", max=" + getSettings().getGameSize().getMaxPlayer() + ", tn=" + getSettings().getGameSize().getTeamNeeded() + ", tm=" + getSettings().getGameSize().getTeamMaxPlayer());
        player.sendMessage("State: " + getState());
        Optional.ofNullable(getPhaseManager().getCurrentPhase()).ifPresent(phase -> player.sendMessage("Phase: " + phase.getClass().getSimpleName()));
        Optional.ofNullable(getGameHost()).ifPresent(host -> host.sendDebugMessage(player));

        /*player.sendMessage("Locations: ");
        getSettings().getLocations().forEach((s, locations) -> player.sendMessage(s + ": " + locations.stream().map(Location::toString).collect(Collectors.joining(", "))));*/

        player.sendMessage("Team Alive: " + getAliveTeamsCount());
        player.sendMessage("Teams: " + getTeamsCount());
        getTeams().forEach(gameTeam -> player.sendMessage(gameTeam.getName() + ": " + gameTeam.getMembers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", "))));

        player.sendMessage("Players: " + getSize() + " (" + getAlivePlayersCount() + "|" + getSpectatorsCount() + ")");
        player.sendMessage("Alive players: " + getAlivePlayers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
        player.sendMessage("Spectator players: " + getSpectators().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
        player.sendMessage("-----------------------------");
    }

}