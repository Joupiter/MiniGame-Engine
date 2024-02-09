package fr.joupi.api.game;

import com.google.common.collect.Lists;
import fr.joupi.api.Utils;
import fr.joupi.api.game.host.GameHostState;
import fr.joupi.api.game.party.GameParty;
import fr.joupi.api.game.party.GamePartyManager;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class GameManager {

    private final JavaPlugin plugin;

    private final GamePartyManager partyManager;
    private final ConcurrentMap<String, List<Game>> games;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.partyManager = new GamePartyManager(this);
        this.games = new ConcurrentHashMap<>();
    }

    public void findGame(Player player, String gameName) {
        if (Optional.ofNullable(getGames(gameName)).isPresent()) {
            Utils.ifPresentOrElse(getBestGame(gameName),
                    game -> joinGame(game, player),
                    () -> Utils.debug("NO GAME AVAILABLE, A NEW GAME IS STARTING FOR PLAYER {0}", player.getName()));
        }
    }

    public void joinGameWithParty(Player leader) {
        getPartyManager().getPartyLedByPlayer(leader).ifPresent(gameParty -> {
            getGame(leader).filter(canJoinGameWithParty(gameParty))
                    .ifPresent(game -> getPartyManager().getPlayersNotInGame(gameParty).forEach(player -> joinGame(game, player)));
        });
    }

    public void joinGame(Game game, Player player) {
        leaveGame(player);
        game.joinGame(player);
    }

    public void joinGame(String id, Player player) {
        leaveGame(player);
        getGame(id).ifPresent(game -> game.joinGame(player));
    }

    public void spectateGame(String id, Player player) {
        leaveGame(player);
        getGame(id).ifPresent(game -> game.joinGame(player, true));
    }

    public void leaveGame(Player player) {
        getGame(player, game -> game.leaveGame(player.getUniqueId()));
    }

    public void addGame(String gameName, Game game) {
        getGames().computeIfAbsent(gameName, k -> Lists.newArrayList()).add(game);
        Utils.debug("ADD GAME {0}", game.getFullName());
    }

    public void removeGame(Game game) {
        getGames().values().forEach(games -> games.remove(game));
        Utils.debug("REMOVE GAME {0}", game.getFullName());
    }

    public void getGame(Player player, Consumer<Game> consumer) {
        getGames().keySet().forEach(gameName -> getGame(gameName, player).ifPresent(consumer));
    }

    public Optional<Game> getGame(Player player) {
        return getGames().values().stream()
                .flatMap(List::stream)
                .filter(game -> game.containsPlayer(player.getUniqueId())).findFirst();
    }

    public Optional<Game> getGame(String gameName, Player player) {
        return getGames().get(gameName).stream()
                .filter(game -> game.containsPlayer(player.getUniqueId())).findFirst();
    }

    public Optional<Game> getGame(String gameName, String id) {
        return getGames(gameName).stream()
                .filter(game -> game.getId().equals(id)).findFirst();
    }

    public Optional<Game> getGame(String id) {
        return getGames().values().stream()
                .flatMap(List::stream)
                .filter(game -> game.getId().equals(id)).findFirst();
    }

    public Optional<Game> getGame(String gameName, World world) {
        return getGames(gameName).stream()
                .filter(game -> game.getSettings().getWorld().equals(world)).findFirst();
    }

    public Optional<Game> getGame(World world) {
        return getGames().values().stream()
                .flatMap(List::stream)
                .filter(game -> game.getSettings().getWorld().equals(world)).findFirst();
    }

    public Optional<Game> getBestGame(String gameName) {
        return getReachableGame(gameName).stream().findFirst();
    }

    public Optional<Game> getGameHost(Player player) {
        return getGamesHost().stream().filter(game -> game.getGameHost().getHostUuid().equals(player.getUniqueId())).findFirst();
    }

    public List<Game> getGamesWithMorePlayers(String gameName, GameState state) {
        return getGamesWithMorePlayers(gameName).stream()
                .filter(game -> game.getState().equals(state))
                .collect(Collectors.toList());
    }

    public List<Game> getGamesWithMorePlayers(String gameName) {
        return getGames(gameName).stream()
                .max(Comparator.comparingInt(Game::getSize))
                .stream().collect(Collectors.toList());
    }

    public List<Game> getGamesWithLessPlayers(String gameName, GameState state) {
        return getGamesWithLessPlayers(gameName).stream()
                .filter(game -> game.getState().equals(state))
                .collect(Collectors.toList());
    }

    public List<Game> getGamesWithLessPlayers(String gameName) {
        return getGames(gameName).stream()
                .min(Comparator.comparingInt(Game::getSize))
                .stream().collect(Collectors.toList());
    }

    public List<Game> getGamesHost(Player player) {
        return getGames().values().stream().flatMap(List::stream)
                .filter(Game::isGameHost)
                .filter(game -> game.getGameHost().getHostUuid().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    public List<Game> getReachableGame(String gameName) {
        return getGames(gameName/*, GameState.WAIT*/).stream()
                .filter(Game::canJoin)
                .collect(Collectors.toList());
    }

    public List<Game> getReachableGamesWithMorePlayers(String gameName) {
        return getGamesWithMorePlayers(gameName, GameState.WAIT).stream()
                .filter(Game::canJoin)
                .collect(Collectors.toList());
    }

    public List<Game> getReachableGamesWithLessPlayers(String gameName) {
        return getGamesWithLessPlayers(gameName, GameState.WAIT).stream()
                .filter(Game::canJoin)
                .collect(Collectors.toList());
    }

    public List<Game> getGames(String gameName, GameState gameState) {
        return getGames(gameName).stream()
                .filter(game -> game.getState().equals(gameState))
                .collect(Collectors.toList());
    }

    public List<Game> getGames(String gameName) {
        return getGames().getOrDefault(gameName, Collections.emptyList());
    }

    public List<Game> getGamesHost() {
        return getGames().values().stream()
                .flatMap(List::stream)
                .filter(Game::isGameHost).collect(Collectors.toList());
    }

    public List<Game> getGamesHost(String gameName) {
        return getGames(gameName).stream()
                .filter(Game::isGameHost)
                .collect(Collectors.toList());
    }

    public List<Game> getGamesHost(String gameName, GameHostState gameHostState) {
        return getGamesHost(gameName).stream()
                .filter(game -> game.getGameHost().getHostState().equals(gameHostState))
                .collect(Collectors.toList());
    }

    public List<Game> getGamesHost(String gameName, GameHostState gameHostState, GameState gameState) {
        return getGamesHost(gameName, gameHostState).stream()
                .filter(game -> game.getState().equals(gameState))
                .collect(Collectors.toList());
    }

    public List<Game> getEmptyGames() {
        return getGames().values().stream().flatMap(List::stream)
                .filter(game -> game.getAlivePlayers().isEmpty())
                .collect(Collectors.toList());
    }

    private Predicate<Game> canJoinGameWithParty(GameParty gameParty) {
        return game -> gameParty.getSize() + game.getSize() < game.getSettings().getGameSize().getMaxPlayer();
    }

    public boolean isInGame(Player player) {
        return getGame(player).isPresent();
    }

    public boolean isNotInGame(Player player) {
        return !isInGame(player);
    }

    public int getPlayersCount(String... gamesName) {
        return Arrays.stream(gamesName).map(this::getGames)
                .flatMap(List::stream)
                .mapToInt(Game::getSize).sum();
    }

    public int getPlayersCount(String gameName) {
        return getGames(gameName).stream()
                .mapToInt(Game::getSize).sum();
    }

    public int getPlayersCount() {
        return getGames().values().stream()
                .flatMap(List::stream)
                .mapToInt(Game::getSize).sum();
    }

    public int getSize(String... gamesName) {
        return Arrays.stream(gamesName).mapToInt(this::getSize).sum();
    }

    public int getSize(String gameName) {
        return getGames(gameName).size();
    }

    public int getSize() {
        return getGames().values().size();
    }

}