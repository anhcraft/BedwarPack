package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.IBedWar;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
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
                Location location = event.getTo();
                PresentPair<Game, BWTeam> pair = null;
                boolean fpc = bp.getConfig().getBoolean("performance.fast_pool_check");
                if(!fpc){
                    LocalGame g = ApiProvider.consume().getArenaManager().getGame(event.getPlayer());
                    if(g == null || g.getMode() != Mode.BEDWAR) {
                        return;
                    }
                    IBedWar bw = (IBedWar) Mode.BEDWAR.getController();
                    if(bw == null) {
                        return;
                    }
                    TeamManager<BWTeam> tm = bw.getTeamManager(g);
                    if(tm == null) {
                        return;
                    }
                    ExArena ea = bp.arenas.get(g.getArena().getId());
                    if(ea == null) {
                        return;
                    }
                    BWTeam bwt = tm.getTeam(event.getPlayer());
                    if(bwt == null) {
                        return;
                    }
                    pair = new PresentPair<>(g, bwt);
                }
                for (ActivePool pool : pools) {
                    if(pool.getBoundingBox() == null) continue;
                    if (pool.getBoundingBox().contains(location)) {
                        for(ActivePool.Potion potion : pool.getPotions()){
                            if(!fpc && !potion.getValidator().test(pair)){
                                continue;
                            }
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
}
