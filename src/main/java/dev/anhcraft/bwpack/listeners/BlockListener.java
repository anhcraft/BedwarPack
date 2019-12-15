package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.events.game.GamePhaseChangeEvent;
import dev.anhcraft.battle.api.events.game.GameQuitEvent;
import dev.anhcraft.battle.api.game.*;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.mode.BattleBedWar;
import dev.anhcraft.battle.api.mode.Mode;
import dev.anhcraft.battle.utils.BlockPosition;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.objects.ActiveGenerator;
import dev.anhcraft.bwpack.objects.ExArena;
import dev.anhcraft.bwpack.objects.Generator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockListener implements Listener {
    private BedwarPack bp;

    public BlockListener(BedwarPack bp) {
        this.bp = bp;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFrom(BlockFormEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockRedstone(BlockRedstoneEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            event.setNewCurrent(0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onIgniteBlock(BlockIgniteEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplodeBlock(BlockExplodeEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            Game game = ApiProvider.consume().getGameManager().getGame(event.getPlayer());
            if(game != null && !bp.placedBlocks.get(game).contains(BlockPosition.of(event.getBlock()))){
                event.setDropItems(false);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())){
            if(event.getBlock().getType() == Material.TNT){
                event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
                event.setBuild(false);
                event.setCancelled(true);
                return;
            }
            Game game = ApiProvider.consume().getGameManager().getGame(event.getPlayer());
            if(game != null){
                bp.placedBlocks.get(game).add(BlockPosition.of(event.getBlock()));
            }
        }
    }
}
