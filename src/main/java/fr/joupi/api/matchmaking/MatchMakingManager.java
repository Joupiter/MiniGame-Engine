package fr.joupi.api.matchmaking;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class MatchMakingManager {

    private final Map<UUID, User> users;

    public MatchMakingManager() {
        this.users = new HashMap<>();
    }

    public Optional<User> getUser(UUID uuid) {
        return Optional.ofNullable(getUsers().get(uuid));
    }

    public void addUser(UUID uuid) {
        getUsers().putIfAbsent(uuid, new User(uuid));
    }

    public void removeUser(UUID uuid) {
        getUsers().remove(uuid);
    }

}
