package dev.anhcraft.bwpack.listeners;

import com.google.common.collect.ImmutableList;
import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.BattleGuiManager;
import dev.anhcraft.battle.api.events.PlayerPrePurchaseEvent;
import dev.anhcraft.battle.api.events.PlayerPurchaseEvent;
import dev.anhcraft.battle.api.events.gui.GuiOpenEvent;
import dev.anhcraft.battle.api.game.BWTeam;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.api.game.TeamManager;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.mode.BattleBedWar;
import dev.anhcraft.battle.api.mode.Mode;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.objects.AutoDyeItem;
import dev.anhcraft.bwpack.objects.ExArena;
import dev.anhcraft.jvmkit.utils.Triplet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MarketListener implements Listener {
    private static final ImmutableList<String> DYEABLE = ImmutableList.of(
            "_SHULKER_BOX",
            "_GLAZED_TERRACOTTA",
            "_BED",
            "_BANNER",
            "_CARPET",
            "_CONCRETE",
            "_CONCRETE_POWDER",
            "_STAINED_GLASS",
            "_STAINED_GLASS_PANE",
            "_DYE",
            "_TERRACOTTA",
            "_WALL_BANNER",
            "_WOOL"
    );
    private BedwarPack bp;

    public MarketListener(BedwarPack bp) {
        this.bp = bp;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked().hasMetadata("bpskp")){
            Category ctg = (Category) event.getRightClicked().getMetadata("bpskp").get(0).value();
            event.setCancelled(true);
            BattleGuiManager gm = ApiProvider.consume().getGuiManager();
            Window w = gm.getWindow(event.getPlayer());
            w.getDataContainer().put("mkctg", ctg);
            w.getDataContainer().put("bpomvs", true);
            gm.openTopGui(event.getPlayer(), NativeGui.MARKET_PRODUCT_MENU);
        }
    }

    @EventHandler
    public void onPreBuy(PlayerPrePurchaseEvent event){
        if(bp.categories.contains(event.getCategory().getId())) {
            BattleGuiManager gm = ApiProvider.consume().getGuiManager();
            Window w = gm.getWindow(event.getPlayer());
            for (FunctionLinker<Triplet<Window, Player, PlayerPrePurchaseEvent>> ins : bp.exInstructions.get(event.getProduct().getId())) {
                ins.call(new Triplet<>(w, event.getPlayer(), event));
            }
        }
    }

    @EventHandler
    public void onBuy(PlayerPurchaseEvent event){
        if(bp.categories.contains(event.getCategory().getId())) {
            BattleGuiManager gm = ApiProvider.consume().getGuiManager();
            Window w = gm.getWindow(event.getPlayer());
            for (FunctionLinker<Triplet<Window, Player, PlayerPrePurchaseEvent>> ins : bp.exInstructions.get(event.getProduct().getId())) {
                ins.call(new Triplet<>(w, event.getPlayer(), null));
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
                LocalGame g = ApiProvider.consume().getGameManager().getGame(event.getPlayer());
                if(g == null || g.getMode() != Mode.BEDWAR) {
                    event.setCancelled(true);
                    return;
                }
                BattleBedWar bw = (BattleBedWar) Mode.BEDWAR.getController();
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