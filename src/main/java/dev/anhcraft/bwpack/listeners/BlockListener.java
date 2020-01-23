package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.utils.BlockPosition;
import dev.anhcraft.bwpack.BedwarPack;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
    public void onPistonExtend(BlockPistonExtendEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplodeBlock(BlockExplodeEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            event.blockList().clear();
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplodeBlock(EntityExplodeEvent event){
        if(bp.worlds.contains(event.getLocation().getWorld().getName())) {
            event.blockList().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())) {
            Game game = ApiProvider.consume().getArenaManager().getGame(event.getPlayer());
            if(game != null && !bp.placedBlocks.get(game).contains(BlockPosition.of(event.getBlock()))){
                event.setDropItems(false);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event){
        if(bp.worlds.contains(event.getBlock().getWorld().getName())){
            if(event.getBlock().getType() == Material.TNT){
                event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
                event.setBuild(false);
                event.setCancelled(true);
                ItemStack i = event.getItemInHand();
                if(event.getHand() == EquipmentSlot.HAND) {
                    i.setAmount(i.getAmount() - 1);
                    event.getPlayer().getInventory().setItemInMainHand(i);
                } else {
                    event.getPlayer().getInventory().setItemInOffHand(i);
                }
                return;
            }
            Game game = ApiProvider.consume().getArenaManager().getGame(event.getPlayer());
            if(game != null){
                bp.placedBlocks.get(game).add(BlockPosition.of(event.getBlock()));
            }
        }
    }
}
