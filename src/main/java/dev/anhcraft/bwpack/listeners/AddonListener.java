package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.api.events.ConfigReloadEvent;
import dev.anhcraft.bwpack.BedwarPack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AddonListener implements Listener {
    @EventHandler
    public void reloadConf(ConfigReloadEvent event) {
        BedwarPack.getInstance().config.reload();
    }
}
