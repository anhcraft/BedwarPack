package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.IBedWar;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.events.PlayerPrePurchaseEvent;
import dev.anhcraft.battle.api.events.PlayerPurchaseEvent;
import dev.anhcraft.battle.api.events.gui.GuiOpenEvent;
import dev.anhcraft.battle.api.gui.GuiManager;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.instructions.TransactionInfo;
import dev.anhcraft.bwpack.schemas.AutoDyeItem;
import dev.anhcraft.bwpack.schemas.ExArena;
import dev.anhcraft.jvmkit.utils.Triplet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MarketListener implements Listener {
    private BedwarPack bp;

    public MarketListener(BedwarPack bp) {
        this.bp = bp;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked().hasMetadata("bpskp")){
            Category ctg = (Category) event.getRightClicked().getMetadata("bpskp").get(0).value();
            event.setCancelled(true);
            GuiManager gm = ApiProvider.consume().getGuiManager();
            Window w = gm.getWindow(event.getPlayer());
            w.getDataContainer().put("mkctg", ctg);
            w.getDataContainer().put("bpomvs", true);
            gm.openTopGui(event.getPlayer(), NativeGui.MARKET_PRODUCT_MENU);
        }
    }

    @EventHandler
    public void onPreBuy(PlayerPrePurchaseEvent event){
        if(bp.categories.contains(event.getCategory().getId())) {
            GuiManager gm = ApiProvider.consume().getGuiManager();
            Window w = gm.getWindow(event.getPlayer());
            for (FunctionLinker<TransactionInfo> ins : bp.marketInstructions.get(event.getProduct().getId())) {
                ins.call(new TransactionInfo(w, event.getPlayer(), event));
            }
        }
    }

    @EventHandler
    public void onBuy(PlayerPurchaseEvent event){
        if(bp.categories.contains(event.getCategory().getId())) {
            GuiManager gm = ApiProvider.consume().getGuiManager();
            Window w = gm.getWindow(event.getPlayer());
            for (FunctionLinker<TransactionInfo> ins : bp.marketInstructions.get(event.getProduct().getId())) {
                ins.call(new TransactionInfo(w, event.getPlayer(), null));
            }
            BWTeam bwt = (BWTeam) w.getDataContainer().get("bpm3");
            for (AutoDyeItem adi : bp.autoDyeProducts.get(event.getProduct().getId())) {
                event.getPlayer().getInventory().addItem(adi.createItem(bwt.getColor()));
            }
        }
    }

    @EventHandler
    public void onGuiOpen(GuiOpenEvent event){
        if(event.getGui().getId().equals(NativeGui.MARKET_PRODUCT_MENU)){
            Window w = event.getWindow();
            Category ctg = (Category) w.getDataContainer().get("mkctg");
            if(ctg != null && bp.categories.contains(ctg.getId())){
                LocalGame g = ApiProvider.consume().getArenaManager().getGame(event.getPlayer());
                if(g == null || g.getMode() != Mode.BEDWAR) {
                    event.setCancelled(true);
                    return;
                }
                IBedWar bw = (IBedWar) Mode.BEDWAR.getController();
                if(bw == null) {
                    event.setCancelled(true);
                    return;
                }
                TeamManager<BWTeam> tm = bw.getTeamManager(g);
                if(tm == null) {
                    event.setCancelled(true);
                    return;
                }
                ExArena ea = bp.arenas.get(g.getArena().getId());
                if(ea == null) {
                    event.setCancelled(true);
                    return;
                }
                BWTeam bwt = tm.getTeam(event.getPlayer());
                if(bwt == null) {
                    event.setCancelled(true);
                    return;
                }
                if(w.getDataContainer().remove("bpomvs") == null) {
                    double x = ea.getOpenableCategoryDistance();
                    if(bwt.getCenterSpawnPoint().distanceSquared(event.getPlayer().getLocation()) >= x*x){
                        event.getPlayer().sendMessage(bp.messages.get("too_far_from_base"));
                        event.setCancelled(true);
                        return;
                    }
                }
                event.getWindow().getDataContainer().put("bpm1", g);
                event.getWindow().getDataContainer().put("bpm2", tm);
                event.getWindow().getDataContainer().put("bpm3", bwt);
            }
        }
    }
}
