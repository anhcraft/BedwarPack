package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.IBedWar;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.bwpack.features.PotionPool;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.utils.TargetPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void move(PlayerMoveEvent event){
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            Player p = event.getPlayer();
            LocalGame game = BattleApi.getInstance().getArenaManager().getGame(p);
            if(game == null || game.getMode() != Mode.BEDWAR) return;
            IBedWar bw = (IBedWar) Mode.BEDWAR.getController();
            if(bw == null) return;
            TeamManager<BWTeam> teamManager = bw.getTeamManager(game);
            if(teamManager == null) return;
            BWTeam team = teamManager.getTeam(p);
            if(team == null) return;
            Location loc = p.getLocation();
            for(PotionPool pool : BedwarPack.getInstance().potionPools.get(game)) {
                for(PotionPool.PotionStack potion : pool.getPotions()) {
                    if(potion.getTarget() == TargetPlayer.TEAMMATES && !potion.getOwner().equals(team)) continue;
                    if(potion.getTarget() == TargetPlayer.ENEMIES && potion.getOwner().equals(team)) continue;
                    if (potion.getBoundingBox().contains(loc)) {
                        potion.add(p);
                    } else {
                        potion.remove(p);
                    }
                }
            }
        }
    }
}
