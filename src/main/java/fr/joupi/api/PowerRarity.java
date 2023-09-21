package fr.joupi.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PowerRarity {

    COMMON ("Commun"),
    RARE ("Rare"),
    EPIC ("Epique"),
    LEGENDARY ("Légendaire"),
    MARCHENOIR ("Marché Noir"),
    PRENIUM ("Prenium");

    private final String name;

}