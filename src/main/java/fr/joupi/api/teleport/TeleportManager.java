package fr.joupi.api.teleport;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class TeleportManager {

    private final List<TeleportRequest> requests;

    public TeleportManager() {
        this.requests = new LinkedList<>();
    }

    public void addRequest(UUID senderId, UUID targetId) {
        getRequests().add(new TeleportRequest(senderId, targetId, getTeleportTicks()));
    }

    public List<TeleportRequest> getIncomingRequests(UUID uuid) {
        return getRequests().stream().filter(request -> request.getTargetId().equals(uuid)).collect(Collectors.toList());
    }

    public List<TeleportRequest> getOutgoingRequests(UUID uuid) {
        return getRequests().stream().filter(request -> request.getSenderId().equals(uuid)).collect(Collectors.toList());
    }

    public int getTeleportTicks() {
        return 250;
    }

}
