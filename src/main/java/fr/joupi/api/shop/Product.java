package fr.joupi.api.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class Product {

    private final String name;
    private final ItemStack icon;

}
