package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.IBedWar;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.bwpack.utils.GameUtils;
import dev.anhcraft.craftkit.cb_common.NMSVersion;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event){
        Game game = ApiProvider.consume().getArenaManager().getGame(event.getPlayer());
        if(game != null && game.getMode() == Mode.BEDWAR && !GameUtils.removePlacedBlock(game, event.getBlock())){
            event.setDropItems(false);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event){
        LocalGame game = ApiProvider.consume().getArenaManager().getGame(event.getPlayer());
        if(game != null && game.getMode() == Mode.BEDWAR){
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
            if(NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 && event.getBlock().getType().name().contains("WOOL")){
                IBedWar bw = (IBedWar) Mode.BEDWAR.getController();
                if(bw == null) return;
                TeamManager<BWTeam> teamManager = bw.getTeamManager(game);
                if(teamManager == null) return;
                BWTeam team = teamManager.getTeam(event.getPlayer());
                if(team == null) return;
                event.getBlock().setType(Material.getMaterial(team.getColor().name()+"_WOOL"));
            }
            GameUtils.addPlacedBlock(game, event.getBlock());
        }
    }
}
