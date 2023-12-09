package fr.joupi.api.request.example;

import fr.joupi.api.request.Request;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class AnotherRequest extends Request {

    private final String field;

    public AnotherRequest(UUID sender, UUID target, String field) {
        super(sender, target);
        this.field = field;
    }

}
