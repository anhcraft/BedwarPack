package dev.anhcraft.bwpack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.bwpack.config.BWPackConfig;
import dev.anhcraft.bwpack.features.ItemGenerator;
import dev.anhcraft.bwpack.features.PotionPool;
import dev.anhcraft.bwpack.function.BWPackFunction;
import dev.anhcraft.bwpack.listeners.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public final class BedwarPack extends JavaPlugin {
    private static BedwarPack instance;
    public final Multimap<Game, ItemGenerator> itemGenerators = MultimapBuilder.hashKeys().arrayListValues().build();
    public final Multimap<Game, PotionPool> potionPools = HashMultimap.create();
    public final BWPackConfig config = new BWPackConfig();

    @NotNull
    public static BedwarPack getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        config.reload();

        BattleApi.getInstance().getGuiManager().registerGuiHandler(BWPackFunction.class);
        getServer().getPluginManager().registerEvents(new MarketListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new AddonListener(), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Iterator<Game> it = itemGenerators.keys().iterator(); it.hasNext(); ) {
                    Game g = it.next();
                    if(g == null || g.getPhase() != GamePhase.PLAYING){
                        for (ItemGenerator ag : itemGenerators.get(g)){
                            if (ag.getHologram() != null) {
                                for (ArmorStand x : ag.getHologram()) {
                                    x.remove();
                                }
                            }
                        }
                        it.remove();
                        continue;
                    }
                    for (ItemGenerator ag : itemGenerators.get(g)){
                        ag.handle();
                    }
                }
            }
        }.runTaskTimer(this, 0, 1);
    }
}
