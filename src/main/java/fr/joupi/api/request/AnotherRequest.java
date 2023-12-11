package fr.joupi.api.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AnotherRequest extends Request {

    private final String field;

    public AnotherRequest(UUID sender, UUID target, String field) {
        super(sender, target);
        this.field = field;
    }

}