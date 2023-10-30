package fr.joupi.api.module.impl;

import fr.joupi.api.module.Module;

public class ExampleModule implements Module {

    @Override
    public void onEnable() {
        System.out.println("Module enable !");
    }

    @Override
    public void onDisable() {
        System.out.println("Module disable !");
    }

}
