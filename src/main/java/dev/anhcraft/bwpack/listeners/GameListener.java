package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.events.game.GamePhaseChangeEvent;
import dev.anhcraft.battle.api.events.game.GameQuitEvent;
import dev.anhcraft.battle.api.game.*;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.mode.BattleBedWar;
import dev.anhcraft.battle.api.mode.Mode;
import dev.anhcraft.battle.utils.BlockPosition;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.objects.ActiveGenerator;
import dev.anhcraft.bwpack.objects.ExArena;
import dev.anhcraft.bwpack.objects.Generator;
import dev.anhcraft.bwpack.objects.Shopkeeper;
import dev.anhcraft.craftkit.cb_common.nbt.CompoundTag;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import dev.anhcraft.craftkit.utils.EntityUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class GameListener implements Listener {
    private BedwarPack bp;

    public GameListener(BedwarPack bp) {
        this.bp = bp;
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void onPhaseUpdate(GamePhaseChangeEvent event){
        if(event.getNewPhase() == GamePhase.PLAYING && event.getGame().getMode() == Mode.BEDWAR && event.getGame() instanceof LocalGame){
            BattleBedWar bw = (BattleBedWar) Mode.BEDWAR.getController();
            ExArena ea;
            if(bw != null && (ea = bp.arenas.get(event.getGame().getArena().getId())) != null){
                if(ea.getLocalGenerator() != null){
                    TeamManager<BWTeam> x = bw.getTeamManager((LocalGame) event.getGame());
                    if(x == null) return;
                    List<Location> genLocs = new ArrayList<>(ea.getLocalGenerator().getLocations());
                    List<BWTeam> bwts = new ArrayList<>(x.getTeams());
                    outer:
                    while (!genLocs.isEmpty()){
                        for (Iterator<Location> it = genLocs.iterator(); it.hasNext(); ) {
                            if(bwts.isEmpty()) break outer;
                            Location genLoc = it.next();
                            BWTeam chosenTeam = null;
                            double nearestDist = 0;
                            for(BWTeam bwt : bwts){
                                double dist = genLoc.distanceSquared(bwt.getCenterSpawnPoint());
                                if(chosenTeam != null && dist >= nearestDist){
                                    continue;
                                }
                                chosenTeam = bwt;
                                nearestDist = dist;
                            }
                            bp.activeGenerators.put(event.getGame(), new ActiveGenerator(genLoc, ea.getLocalGenerator(), chosenTeam));
                            bwts.remove(chosenTeam);
                            it.remove();
                        }
                    }
                }
                for(Generator gen : ea.getSharedGenerators()){
                    for (Location loc : gen.getLocations()){
                        bp.activeGenerators.put(event.getGame(), new ActiveGenerator(loc, gen, null));
                    }
                }
                Market mk = ApiProvider.consume().getMarket();
                for(Shopkeeper sk : ea.getShopkeepers()){
                    Optional<Category> ctg = mk.getCategories().stream().filter(c -> c.getId().equals(sk.getCategory())).findAny();
                    if(!ctg.isPresent()) continue;
                    Category ct = ctg.get();
                    for(Location loc : sk.getLocations()){
                        Entity ent = loc.getWorld().spawnEntity(loc, sk.getEntityType());
                        ent.setInvulnerable(true);
                        ent.setMetadata("bpskp", new FixedMetadataValue(bp, ct));
                        if(ct.getIcon().name() != null){
                            ent.setCustomNameVisible(true);
                            ent.setCustomName(ChatUtil.formatColorCodes(ct.getIcon().name()));
                        }
                        if(ent instanceof LivingEntity){
                            LivingEntity x = (LivingEntity) ent;
                            x.setAI(false);
                            x.setCanPickupItems(false);
                            x.setRemoveWhenFarAway(false);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(GameQuitEvent event){
        if(event.getGame().getMode() == Mode.BEDWAR){
            Window w = ApiProvider.consume().getGuiManager().getWindow(event.getGamePlayer().toBukkit());
            w.getDataContainer().remove("bpm1");
            w.getDataContainer().remove("bpm2");
            w.getDataContainer().remove("bpm3");
        }
    }
}
