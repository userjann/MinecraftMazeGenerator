package ch.mc.jr.labyrinth;

import org.bukkit.plugin.java.JavaPlugin;

public final class Labyrinth extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("maze").setExecutor(new Command());
    }

    @Override
    public void onDisable() {

    }

}
