package fr.joupi.api.game;

import com.google.common.collect.Lists;
import fr.joupi.api.Utils;
import fr.joupi.api.game.host.GameHostState;
import fr.joupi.api.game.utils.GameInfo;
import fr.joupi.api.threading.MultiThreading;
import javassist.compiler.MemberResolver;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
            Utils.ifPresentOrElse(getBestGame(gameName),
                    game -> joinGame(game, player),
                    () -> System.out.println("NO GAME AVAILABLE, A NEW GAME IS STARTING FOR PLAYER " + player.getName()));
        }
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
        System.out.println("ADD GAME " + game.getFullName());
    }

    public <P extends JavaPlugin> void addGame(P plugin, String gameName, GameSize gameSize) {
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
        Set<Class<? extends Game>> gameClasses = reflections.getSubTypesOf(Game.class);

        for (Class<? extends Game> gameClass : gameClasses) {
            try {
                GameInfo gameInfo = gameClass.getAnnotation(GameInfo.class);
                Constructor constructor = gameClass.getConstructor(plugin.getClass(), GameSize.class);

                if (gameInfo.name().equalsIgnoreCase(gameName)) {
                    Game newGame = (Game) constructor.newInstance(plugin, gameSize);
                    addGame(gameName, newGame);
                    return;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public <P extends JavaPlugin> void addHostGame(P plugin, String gameName, Player player, GameSize gameSize) {
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
        Set<Class<? extends Game>> gameClasses = reflections.getSubTypesOf(Game.class);

        for (Class<? extends Game> gameClass : gameClasses) {
            try {
                GameInfo gameInfo = gameClass.getAnnotation(GameInfo.class);
                Constructor constructor = gameClass.getConstructor(plugin.getClass(), Player.class, GameSize.class);

                if (gameInfo.name().equalsIgnoreCase(gameName)) {
                    Game newGame = (Game) constructor.newInstance(plugin, player, gameSize);
                    addGame(gameName + "host", newGame);
                    return;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }


    public void removeGame(Game game) {
        getGames().values().forEach(gameList -> gameList.remove(game));
        System.out.println("REMOVE GAME " + game.getFullName());
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
        return Arrays.stream(gamesName).map(this::getGames)
                .mapToInt(List::size).sum();
    }

    public int getSize(String gameName) {
        return getGames(gameName).size();
    }

    public int getSize() {
        return getGames().values().size();
    }

}