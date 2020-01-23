package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.IBedWar;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.bwpack.ActivePool;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.schemas.ExArena;
import dev.anhcraft.jvmkit.utils.PresentPair;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class PlayerListener implements Listener {
    private BedwarPack bp;

    public PlayerListener(BedwarPack bp) {
        this.bp = bp;
    }

    @EventHandler
    public void move(PlayerMoveEvent event){
        String n = event.getTo().getWorld().getName();
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            if(!bp.worlds.contains(n)) return;
            Collection<ActivePool> pools = bp.world2pools.get(n);
            if(!pools.isEmpty()) {
                Window w = BattleApi.getInstance().getGuiManager().getWindow(event.getPlayer());
                LocalGame lg = (LocalGame) w.getDataContainer().get("bpm1");
                BWTeam bwt = (BWTeam) w.getDataContainer().get("bpm3");
                if(lg == null) {
                    lg = ApiProvider.consume().getArenaManager().getGame(event.getPlayer());
                    if (lg == null || lg.getMode() != Mode.BEDWAR) {
                        return;
                    }
                    w.getDataContainer().put("bpm1", lg);
                }
                if(bwt == null){
                    IBedWar bw = (IBedWar) Mode.BEDWAR.getController();
                    if(bw == null) {
                        return;
                    }
                    TeamManager<BWTeam> tm = bw.getTeamManager(lg);
                    if(tm == null) {
                        return;
                    }
                    ExArena ea = bp.arenas.get(lg.getArena().getId());
                    if(ea == null) {
                        return;
                    }
                    bwt = tm.getTeam(event.getPlayer());
                    if(bwt == null) {
                        return;
                    }
                    w.getDataContainer().put("bpm3", bwt);
                }
                PresentPair<Game, BWTeam> pair = new PresentPair<>(lg, bwt);
                for (ActivePool pool : pools) {
                    for(ActivePool.Potion potion : pool.getPotions()){
                        if(!potion.getValidator().test(pair)) continue;
                        if(potion.isInPool(event.getPlayer())) {
                            potion.add(event.getPlayer());
                        } else {
                            potion.remove(event.getPlayer());
                        }
                    }
                }
            }
        }
    }
}
