package dev.anhcraft.bwpack.listeners;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.IBedWar;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.events.game.BedBreakEvent;
import dev.anhcraft.battle.api.events.game.GamePhaseChangeEvent;
import dev.anhcraft.battle.api.events.game.GameQuitEvent;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.bwpack.features.ItemGenerator;
import dev.anhcraft.bwpack.features.PotionPool;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.config.schemas.BedwarArena;
import dev.anhcraft.bwpack.config.schemas.Generator;
import dev.anhcraft.bwpack.config.schemas.Shopkeeper;
import dev.anhcraft.bwpack.stats.BedDestroyStat;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import dev.anhcraft.craftkit.entity.ArmorStand;
import dev.anhcraft.craftkit.entity.TrackedEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

public class GameListener implements Listener {
    private List<TrackedEntity<ArmorStand>> createArmorStand(Generator gen, Location location, LocalGame game){
        if(!gen.isHologramEnabled()) return null;
        if(gen.getHologramLines() == null || gen.getHologramLines().isEmpty()) return null;
        List<TrackedEntity<ArmorStand>> list = new ArrayList<>();
        for(int i = gen.getHologramLines().size() - 1; i >= 0; i--){
            ArmorStand x = ArmorStand.spawn(location.clone());
            location.add(0, gen.getHologramOffset(), 0);
            x.setVisible(false);
            x.setNameVisible(true);
            x.setViewers(new ArrayList<>(game.getPlayers().keySet()));
            TrackedEntity<ArmorStand> te = BedwarPack.getInstance().craftExtension.trackEntity(x);
            te.setViewDistance(16 * Bukkit.getViewDistance());
            list.add(te);
        }
        return list;
    }

    @EventHandler
    public void onPhaseUpdate(GamePhaseChangeEvent event){
        if(event.getGame().getMode() == Mode.BEDWAR && event.getGame() instanceof LocalGame){
            LocalGame game = (LocalGame) event.getGame();
            if(event.getNewPhase() == GamePhase.PLAYING) {
                IBedWar bw = (IBedWar) Mode.BEDWAR.getController();
                BedwarArena ea;
                if (bw != null && (ea = BedwarPack.getInstance().config.getArena(game.getArena().getId())) != null) {
                    if (ea.getLocalGenerator() != null) {
                        TeamManager<BWTeam> tm = bw.getTeamManager(game);
                        if (tm == null) return;
                        List<Location> genLocs = new ArrayList<>(ea.getLocalGenerator().getLocations());
                        List<BWTeam> bwts = tm.getTeams().stream()
                                .filter(t -> tm.countPlayers(t) > 0)
                                .collect(Collectors.toList());
                        if(!bwts.isEmpty()) {
                            outer:
                            while (!genLocs.isEmpty()) {
                                for (Iterator<Location> it = genLocs.iterator(); it.hasNext(); ) {
                                    Location genLoc = it.next();
                                    BWTeam chosenTeam = null;
                                    double nearestDist = 0;
                                    for (BWTeam bwt : bwts) {
                                        double dist = genLoc.distanceSquared(bwt.getCenterSpawnPoint());
                                        if (chosenTeam != null && dist >= nearestDist) {
                                            continue;
                                        }
                                        chosenTeam = bwt;
                                        nearestDist = dist;
                                    }
                                    game.addInvolvedWorld(genLoc.getWorld());
                                    List<TrackedEntity<ArmorStand>> as = createArmorStand(ea.getLocalGenerator(), genLoc, game);
                                    ItemGenerator ag = new ItemGenerator(genLoc, ea.getLocalGenerator(), chosenTeam, as);
                                    BedwarPack.getInstance().itemGenerators.put(game, ag);
                                    bwts.remove(chosenTeam);
                                    it.remove();
                                    // when no teams left, the outer loop needs to be stopped
                                    if(bwts.isEmpty()) break outer;
                                }
                            }
                        }
                    }
                    if (ea.getSharedGenerators() != null) {
                        for (Generator gen : ea.getSharedGenerators()) {
                            for (Location loc : gen.getLocations()) {
                                game.addInvolvedWorld(loc.getWorld());
                                ItemGenerator ag = new ItemGenerator(loc, gen, null, createArmorStand(gen, loc, game));
                                BedwarPack.getInstance().itemGenerators.put(game, ag);
                            }
                        }
                    }
                    Market mk = ApiProvider.consume().getMarket();
                    if (ea.getShopkeepers() != null) {
                        for (Shopkeeper sk : ea.getShopkeepers()) {
                            Optional<Category> ctg = mk.getCategories().stream()
                                    .filter(c -> c.getId().equals(sk.getCategory()))
                                    .findAny();
                            if (!ctg.isPresent()) continue;
                            Category ct = ctg.get();
                            for (Location loc : sk.getLocations()) {
                                game.addInvolvedWorld(loc.getWorld());
                                Entity ent = loc.getWorld().spawnEntity(loc, sk.getEntityType());
                                ent.setInvulnerable(true);
                                ent.setMetadata("bpskp", new FixedMetadataValue(BedwarPack.getInstance(), ct));
                                if (ct.getIcon().name() != null) {
                                    ent.setCustomNameVisible(true);
                                    ent.setCustomName(ChatUtil.formatColorCodes(ct.getIcon().name()));
                                }
                                if (ent instanceof LivingEntity) {
                                    LivingEntity x = (LivingEntity) ent;
                                    x.setAI(false);
                                    x.setCanPickupItems(false);
                                    x.setRemoveWhenFarAway(false);
                                }
                            }
                        }
                    }
                }
            } else if(event.getNewPhase() == GamePhase.END) {
                BedwarPack.getInstance().potionPools.removeAll(game).forEach(PotionPool::removeAllPotion);
                // BedwarPack.getInstance().itemGenerators.removeAll(game); // this one is removed automatically
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreakBed(BedBreakEvent event){
        Objects.requireNonNull(BattleApi.getInstance().getPlayerData(event.getPlayer())).getStats().of(BedDestroyStat.class).increase(event.getPlayer());
    }
}
