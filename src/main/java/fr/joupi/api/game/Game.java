package fr.joupi.api.game;

import fr.joupi.api.game.entity.GameEntityManager;
import fr.joupi.api.game.event.GameLoadEvent;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.event.GameUnloadEvent;
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class Game<G extends GamePlayer, T extends GameTeam, S extends GameSettings> implements Listener {

    private final JavaPlugin plugin;

    private final String name, id;
    private final S settings;

    private final PhaseManager<?> phaseManager;
    private final GameEntityManager gameEntityManager;
    private GameHost<?> gameHost;

    private final List<GameListenerWrapper<?>> listeners;
    private final List<T> teams;
    private final List<BukkitTask> tasks;
    private final ConcurrentMap<UUID, G> players;

    private GameState state;

    public Game(JavaPlugin plugin, String name, S settings) {
        this.plugin = plugin;
        this.name = name;
        this.id = RandomStringUtils.randomAlphanumeric(10);
        this.settings = settings;
        this.phaseManager = new PhaseManager<>(this);
        this.gameEntityManager = new GameEntityManager(plugin);
        this.listeners = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.players = new ConcurrentHashMap<>();
        this.state = GameState.WAIT;
        load();
    }

    public abstract G defaultGamePlayer(UUID uuid, boolean spectator);

    public abstract T defaultGameTeam(GameTeamColor color);

    private void load() {
        getTeams().addAll(Arrays.stream(GameTeamColor.values()).limit(getSettings().getGameSize().getTeamNeeded()).map(this::defaultGameTeam).collect(Collectors.toList()));
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        debug("{0} loaded", getFullName());
        Bukkit.getPluginManager().callEvent(new GameLoadEvent(this));
    }

    public void unload() {
        getPhaseManager().unregisterPhases();
        getListeners().forEach(HandlerList::unregisterAll);
        HandlerList.unregisterAll(this);
        debug("{0} unloaded", getFullName());
        Bukkit.getPluginManager().callEvent(new GameUnloadEvent(this));
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
        return getPlayers().values().stream().filter(isSpectatorPredicate().negate()).collect(Collectors.toList());
    }

    public List<G> getSpectators() {
        return getPlayers().values().stream().filter(GamePlayer::isSpectator).collect(Collectors.toList());
    }

    public List<G> getPlayersWithTeam() {
        return getPlayers().values().stream().filter(haveTeamPredicate()).collect(Collectors.toList());
    }

    public List<G> getPlayersWithoutTeam() {
        return getPlayers().values().stream().filter(haveTeamPredicate().negate()).collect(Collectors.toList());
    }

    public List<T> getAliveTeams() {
        return getTeams().stream().filter(isNoPlayersAlivePredicate().negate()).collect(Collectors.toList());
    }

    public List<T> getReachableTeams() {
        return getTeams().stream().filter(gameTeam -> gameTeam.getSize() < getSettings().getGameSize().getTeamMaxPlayer()).collect(Collectors.toList());
    }

    public T getTeam(String teamName) {
        return getTeams().stream().filter(gameTeam -> gameTeam.getName().equals(teamName)).findFirst().orElse(null);
    }

    public Optional<T> getTeam(GamePlayer gamePlayer) {
        return getTeams().stream().filter(gameTeam -> gameTeam.isMember(gamePlayer)).findFirst();
    }

    public Optional<T> getRandomTeam() {
        return getReachableTeams().stream().skip(getReachableTeams().isEmpty() ? 0 : ThreadLocalRandom.current().nextInt(getReachableTeams().size())).findFirst();
    }

    private Optional<T> getTeamWithLeastPlayers() {
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

    public void ifHostedGame(Predicate<GameHost<?>> predicate, Consumer<GameHost<?>> consumer) {
        Optional.ofNullable(getGameHost())
                .filter(predicate)
                .ifPresent(consumer);
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

    public void ifContainsPlayer(UUID uuid, Consumer<Player> consumer) {
        ifContainsPlayer(Bukkit.getPlayer(uuid), consumer);
    }

    public void ifContainsPlayer(Player player, Consumer<Player> consumer) {
        Optional.of(player).filter(this::containsPlayer).ifPresent(consumer);
    }

    public void ifContainsPlayer(Player player, Runnable runnable) {
        Optional.of(player).filter(this::containsPlayer).ifPresent(p -> runnable.run());
    }

    public void joinGame(Player player) {
        joinGame(player, false);
    }

    public void joinGame(Player player, boolean spectator) {
        if (!getPlayers().containsKey(player.getUniqueId())) {
            G gamePlayer = defaultGamePlayer(player.getUniqueId(), spectator);
            getPlayers().put(player.getUniqueId(), gamePlayer);
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerJoinEvent<>(this, gamePlayer));
            debug("{0} {1} {2} game", player.getName(), (gamePlayer.isSpectator() ? "spectate" : "join"), getFullName());
        }
    }

    public void leaveGame(UUID uuid) {
        getPlayer(uuid).ifPresent(gamePlayer -> {
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerLeaveEvent<>(this, gamePlayer));
            getPlayers().remove(uuid);
            removePlayerToTeam(gamePlayer);
            debug("{0} leave {1}", gamePlayer.getPlayer().getName(), getFullName());
        });
    }

    public void endGame(GameManager gameManager) {
        getPlayers().values().stream().map(GamePlayer::getUuid).forEach(this::leaveGame);
        unload();
        gameManager.removeGame(this);
        debug("END OF GAME : {0}", getFullName());
    }

    private void broadcast(String message) {
        getPlayers().values().forEach(gamePlayer -> gamePlayer.sendMessage(message));
    }

    public void broadcast(String... messages) {
        Arrays.asList(messages).forEach(this::broadcast);
    }

    public void broadcast(Predicate<G> filter, String... messages) {
        getPlayers().values().stream().filter(filter)
                .forEach(gamePlayer -> gamePlayer.sendMessage(messages));
    }

    public void debug(String message, Object ... arguments) {
        System.out.println("[GameEngine] " + MessageFormat.format(message, arguments));
    }

    public String getFullName() {
        return getName() + (isGameHost() ? "Host" : "") + "-" + getSettings().getGameSize().getName() + "-" + getId();
    }

    private Predicate<GameTeam> isNoPlayersAlivePredicate() {
        return gameTeam -> gameTeam.getAlivePlayers().isEmpty();
    }

    private Predicate<GamePlayer> isSpectatorPredicate() {
        return GamePlayer::isSpectator;
    }

    private Predicate<GamePlayer> haveTeamPredicate() {
        return gamePlayer -> getTeam(gamePlayer).isPresent();
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

    public boolean containsPlayer(Player player) {
        return getPlayers().containsKey(player.getUniqueId());
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

    public String coloredMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
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