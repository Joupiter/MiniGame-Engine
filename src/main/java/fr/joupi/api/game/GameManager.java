package fr.joupi.api.game;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Getter
public class GameManager {

    private final JavaPlugin plugin;
    private final ConcurrentMap<String, List<Game<?>>> games;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.games = new ConcurrentHashMap<>();
    }

    public void findGame(Player player, String gameName) {

    }

    /*
        ex: getGamesWithMorePlayers("golemrush1vs1"); return all games
        ex: getGamesWithMorePlayers("golemrush10vs10"); return all games
     */

    public List<Game<?>> getGamesWithMorePlayers(String gameName, GameState state) {
        return getGamesWithMorePlayers(gameName).stream()
                .filter(game -> game.getState().equals(state))
                .collect(Collectors.toList());
    }

    public List<Game<?>> getGamesWithMorePlayers(String gameName) {
        return getGames(gameName).stream()
                .max(Comparator.comparingInt(Game::getSize))
                .stream().collect(Collectors.toList());
    }

    public List<Game<?>> getGamesWithLessPlayers(String gameName, GameState state) {
        return getGamesWithLessPlayers(gameName).stream()
                .filter(game -> game.getState().equals(state))
                .collect(Collectors.toList());
    }

    public List<Game<?>> getGamesWithLessPlayers(String gameName) {
        return getGames(gameName).stream()
                .min(Comparator.comparingInt(Game::getSize))
                .stream().collect(Collectors.toList());
    }

    public List<Game<?>> getReachableGames(String gameName) {
        return getGames(gameName, GameState.WAIT).stream()
                .filter(game -> game.getAlivePlayersCount() < game.getSettings().getSize().getMaxPlayer())
                .collect(Collectors.toList());
    }

    public List<Game<?>> getGames(String gameName, GameState gameState) {
        return getGames(gameName).stream()
                .filter(game -> game.getState().equals(gameState))
                .collect(Collectors.toList());
    }

    public List<Game<?>> getGames(String gameName) {
        return getGames().get(gameName);
    }

    public int getSize(String gameName) {
        return getGames().get(gameName).stream().mapToInt(Game::getSize).sum();
    }

    public int getSize() {
        return getGames().values().stream().mapToInt(List::size).sum();
    }

}
