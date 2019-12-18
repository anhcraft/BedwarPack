package dev.anhcraft.bwpack;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.events.PlayerPrePurchaseEvent;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.utils.BlockPosition;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.bwpack.listeners.BlockListener;
import dev.anhcraft.bwpack.listeners.GameListener;
import dev.anhcraft.bwpack.listeners.MarketListener;
import dev.anhcraft.bwpack.objects.ActiveGenerator;
import dev.anhcraft.bwpack.objects.AutoDyeItem;
import dev.anhcraft.bwpack.objects.ExArena;
import dev.anhcraft.jvmkit.utils.Triplet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public final class BedwarPack extends JavaPlugin {
    public final Multimap<Game, ActiveGenerator> activeGenerators = MultimapBuilder.hashKeys().arrayListValues().build();
    public final Multimap<Game, BlockPosition> placedBlocks = MultimapBuilder.hashKeys().hashSetValues().build();
    public final Multimap<String, AutoDyeItem> autoDyeProducts = MultimapBuilder.hashKeys().arrayListValues().build();
    public final Multimap<String, FunctionLinker<Triplet<Window, Player, PlayerPrePurchaseEvent>>> exInstructions = MultimapBuilder.hashKeys().arrayListValues().build();
    public final Map<String, String> messages = new HashMap<>();
    public final Map<String, ExArena> arenas = new HashMap<>();
    public final List<String> categories = new ArrayList<>();
    public final List<String> worlds = new ArrayList<>();

    @Override
    public void onEnable() {
        new ConfigLoader(this).reloadConf();

        getServer().getPluginManager().registerEvents(new MarketListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Iterator<Game> it = activeGenerators.keys().iterator(); it.hasNext(); ) {
                    Game g = it.next();
                    if(g == null || g.getPhase() != GamePhase.PLAYING){
                        it.remove();
                        continue;
                    }
                    for (ActiveGenerator ag : activeGenerators.get(g)){
                        ag.handle();
                    }
                }
            }
        }.runTaskTimer(this, 0, 1);
    }
}
