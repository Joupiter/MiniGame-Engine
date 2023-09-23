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
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.PanelUI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class Game<P extends JavaPlugin> {

    private final P plugin;

    private final String name, id;
    private final GameSettings settings;
    private final PhaseManager<?> phaseManager;

    private final List<GameTeam> teams;
    private final ConcurrentMap<UUID, GamePlayer> players;

    private GameState state;

    protected Game(P plugin, String name, GameSettings settings) {
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

    private void load() {
        Arrays.stream(GameTeamColor.values()).collect(Collectors.toList())
                .stream()
                .limit(getSettings().getSize().getTeamNeeded())
                .forEach(gameTeamColor -> getTeams().add(new GameTeam(gameTeamColor)));

        getTeams().forEach(gameTeam -> System.out.println(gameTeam.toString()));
    }

    public List<GamePlayer> getAlivePlayers() {
        return getPlayers().values().stream().filter(((Predicate<? super GamePlayer>) GamePlayer::isSpectator).negate()).collect(Collectors.toList());
    }

    public List<GamePlayer> getSpectators() {
        return getPlayers().values().stream().filter(GamePlayer::isSpectator).collect(Collectors.toList());
    }

    public GameTeam getTeam(String teamName) {
        return getTeams().stream().filter(gameTeam -> gameTeam.getName().equals(teamName)).findFirst().orElse(null);
    }

    public Optional<GameTeam> getTeam(GamePlayer gamePlayer) {
        return getTeams().stream().filter(gameTeam -> gameTeam.isMember(gamePlayer)).findFirst();
    }

    public Optional<GamePlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(getPlayers().get(uuid));
    }

    private Optional<GameTeam> getTeamWithLeastPlayers() {
        return getTeams().stream().filter(team -> team.getSize() < getSettings().getSize().getTeamMaxPlayer()).min(Comparator.comparingInt(GameTeam::getSize));
    }

    public void fillTeam() {
        getPlayers().values().forEach(gamePlayer -> getTeamWithLeastPlayers().ifPresent(gameTeam -> gameTeam.addMember(gamePlayer)));
    }

    public void checkGameState(GameState gameState, Runnable runnable) {
        BooleanWrapper.of(getState().equals(gameState))
                .ifTrue(runnable);
    }

    public void joinGame(Player player) {
        BooleanWrapper.of(getPlayers().containsKey(player.getUniqueId()))
                .ifFalse(() -> {
                    GamePlayer gamePlayer = new GamePlayer(player.getUniqueId(), 0, 0, !getState().equals(GameState.WAIT));
                    getPlayers().put(gamePlayer.getUuid(), gamePlayer);
                    Bukkit.getServer().getPluginManager().callEvent(new GamePlayerJoinEvent(this, gamePlayer));
                    System.out.println(player.getName() + " join " + getFullName() + " game");
                });
    }

    public void leaveGame(UUID uuid) {
        getPlayer(uuid).ifPresent(gamePlayer -> {
            getPlayers().remove(gamePlayer.getUuid());
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerLeaveEvent(this, gamePlayer));
            getTeam(gamePlayer).ifPresent(gameTeam -> gameTeam.removeMember(gamePlayer));
            System.out.println(gamePlayer.getPlayer().getName() + " leave " + getFullName() + " game");
        });
    }

    public void broadcast(String message) {
        getPlayers().values().stream().map(GamePlayer::getPlayer).forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public void broadcast(String... messages) {
        Arrays.asList(messages)
                .forEach(this::broadcast);
    }

    public String getFullName() {
        return getName() + "-" + getSettings().getSize().getName() + "-" + getId();
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

}
