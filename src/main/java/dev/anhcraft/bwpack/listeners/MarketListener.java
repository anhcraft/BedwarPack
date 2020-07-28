package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.gui.GuiManager;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.market.Category;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MarketListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked().hasMetadata("bpskp")){
            event.setCancelled(true);
            Category ctg = (Category) event.getRightClicked().getMetadata("bpskp").get(0).value();
            GuiManager gm = ApiProvider.consume().getGuiManager();
            Window w = gm.getWindow(event.getPlayer());
            w.getDataContainer().put("mkctg", ctg);
            gm.openTopGui(event.getPlayer(), NativeGui.MARKET_PRODUCT_MENU);
        }
    }
}
