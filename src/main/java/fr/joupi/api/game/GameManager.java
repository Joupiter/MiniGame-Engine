package fr.joupi.api.game;

import com.google.common.collect.Lists;
import fr.joupi.api.game.host.GameHostState;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class GameManager {

    private final JavaPlugin plugin;
    private final ConcurrentMap<String, List<Game>> games;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.games = new ConcurrentHashMap<>();
    }

    public void findGame(Player player, String gameName) {
        if (Optional.ofNullable(getGames(gameName)).isPresent()) {
            getBestGame(gameName).ifPresentOrElse(
                    game -> {
                        getGame(player, currentGame -> currentGame.leaveGame(player.getUniqueId())); // Leave game of the player if he currently in a game (reduce bug)
                        game.joinGame(player);
                    }, () -> System.out.println("NO GAME AVAILABLE, A NEW GAME IS STARTING FOR PLAYER " + player.getName()));
        }
    }

    public void leave(Player player) {
        getGame(player, game -> game.leaveGame(player.getUniqueId()));
    }

    public void addGame(String gameName, Game game) {
        //Optional.ofNullable(getGames().get(gameName)).ifPresentOrElse(gameList -> gameList.add(game), () -> getGames().putIfAbsent(gameName, Lists.newArrayList(game)));
        getGames().computeIfAbsent(gameName, k -> Lists.newArrayList()).add(game);
        System.out.println("ADD GAME " + game.getFullName());
    }

    public void removeGame(Game game) {
        getGames().values().forEach(gameList -> gameList.remove(game));
        System.out.println("REMOVE GAME " + game.getFullName());
    }

    /*
        ex: getGamesWithMorePlayers("golemrush1vs1"); return all games
        ex: getGamesWithMorePlayers("golemrush10vs10"); return all games
     */

    public void getGame(Player player, Consumer<Game> consumer) {
        getGames().keySet().forEach(gameName -> getGame(gameName, player).ifPresent(consumer));
    }

    public Optional<Game> getGame(String gameName, Player player) {
        return getGames().get(gameName).stream().filter(game -> game.containsPlayer(player.getUniqueId())).findFirst();
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

    public Optional<Game> getBestGame(String gameName) {
        return getReachableGame(gameName).stream().findFirst();
    }

    public Optional<Game> getGameHost(Player player) {
        return getGamesHost().stream().filter(game -> game.getGameHost().getHostUuid().equals(player.getUniqueId())).findFirst();
    }

    /*
        Todo: NE PAS OUBLIER DE RETIRER LE COMMENTAIRE !!!!!!!!!!!!!!!!!!!
     */
    public List<Game> getReachableGame(String gameName) {
        return getGames(gameName/*, GameState.WAIT*/).stream()
                .filter(game -> game.getAlivePlayersCount() < game.getSettings().getGameSize().getMaxPlayer())
                .collect(Collectors.toList());
    }

    public List<Game> getReachableGamesWithMorePlayers(String gameName) {
        return getGamesWithMorePlayers(gameName, GameState.WAIT).stream()
                .filter(game -> game.getAlivePlayersCount() < game.getSettings().getGameSize().getMaxPlayer())
                .collect(Collectors.toList());
    }

    public List<Game> getReachableGamesWithLessPlayers(String gameName) {
        return getGamesWithLessPlayers(gameName, GameState.WAIT).stream()
                .filter(game -> game.getAlivePlayersCount() < game.getSettings().getGameSize().getMaxPlayer())
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
                .filter(game -> game.getGameHost() != null).collect(Collectors.toList());
    }

    public List<Game> getGamesHost(String gameName) {
        return getGames(gameName).stream().filter(game -> game.getGameHost() != null).collect(Collectors.toList());
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

    public int getPlayersCount(String gameName) {
        return getGames().get(gameName).stream().mapToInt(Game::getSize).sum();
    }

    public int getPlayersCount() {
        return getGames().values().stream().flatMap(List::stream).mapToInt(Game::getSize).sum();
    }

    public int getSize(String gameName) {
        return getGames().get(gameName).size();
    }

    public int getSize() {
        return getGames().values().size();
    }

}
