package fr.joupi.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class UserStats {

    private Map<String, Object> stats;

}
