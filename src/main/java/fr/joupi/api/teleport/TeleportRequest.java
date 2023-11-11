package fr.joupi.api.teleport;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class TeleportRequest {

    private final UUID requestId, senderId, targetId;

    private int ticksLeft;

    public TeleportRequest(UUID senderId, UUID targetId, int ticksLeft) {
        this.requestId = UUID.randomUUID();
        this.senderId = senderId;
        this.targetId = targetId;
        this.ticksLeft = ticksLeft;
    }

}
