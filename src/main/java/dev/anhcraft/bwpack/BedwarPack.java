package dev.anhcraft.bwpack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.utils.BlockPosition;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.battle.utils.functions.Instruction;
import dev.anhcraft.bwpack.instructions.ExInstruction;
import dev.anhcraft.bwpack.instructions.TransactionInfo;
import dev.anhcraft.bwpack.listeners.BlockListener;
import dev.anhcraft.bwpack.listeners.GameListener;
import dev.anhcraft.bwpack.listeners.MarketListener;
import dev.anhcraft.bwpack.listeners.PlayerListener;
import dev.anhcraft.bwpack.schemas.AutoDyeItem;
import dev.anhcraft.bwpack.schemas.ExArena;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public final class BedwarPack extends JavaPlugin {
    public final Multimap<Game, ActiveGenerator> activeGenerators = MultimapBuilder.hashKeys().arrayListValues().build();
    public final Multimap<Game, BlockPosition> placedBlocks = MultimapBuilder.hashKeys().hashSetValues().build();
    public final Multimap<String, AutoDyeItem> autoDyeProducts = MultimapBuilder.hashKeys().arrayListValues().build();
    public final Multimap<String, FunctionLinker<TransactionInfo>> marketInstructions = MultimapBuilder.hashKeys().arrayListValues().build();
    public final Map<String, String> messages = new HashMap<>();
    public final Map<String, ExArena> arenas = new HashMap<>();
    public final List<String> categories = new ArrayList<>();
    public final List<String> worlds = new ArrayList<>();
    public final Multimap<String, ActivePool> world2pools = HashMultimap.create();
    private static BedwarPack instance;

    @NotNull
    public static BedwarPack getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        reloadConf();

        getServer().getPluginManager().registerEvents(new MarketListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

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

    public void reloadConf(){
        BedwarPack bp = BedwarPack.getInstance();
        bp.saveDefaultConfig();
        bp.reloadConfig();

        ConfigurationSection msg = bp.getConfig().getConfigurationSection("messages");
        for(String s : msg.getKeys(false)){
            bp.messages.put(s, ChatUtil.formatColorCodes(msg.getString(s)));
        }

        bp.categories.addAll(bp.getConfig().getStringList("products.category_filter"));
        bp.worlds.addAll(bp.getConfig().getStringList("worlds"));

        try {
            ConfigurationSection ady = bp.getConfig().getConfigurationSection("products.auto_dye_items");
            for (String p : ady.getKeys(false)) {
                ConfigurationSection ics = ady.getConfigurationSection(p);
                for (String i : ics.getKeys(false)) {
                    bp.autoDyeProducts.put(p, ConfigHelper.readConfig(ics.getConfigurationSection(i), ConfigSchema.of(AutoDyeItem.class)));
                }
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }

        ConfigurationSection epe = bp.getConfig().getConfigurationSection("products.extra_executions");
        for(String product : epe.getKeys(false)){
            for (String x : epe.getStringList(product)) {
                Instruction i = Objects.requireNonNull(Instruction.parse(x));
                int hash = Objects.hash(i.getNamespace(), i.getTarget());
                for(ExInstruction ins : ExInstruction.values()){
                    if(ins.getHash() == hash){
                        //noinspection unchecked
                        bp.marketInstructions.put(product, new FunctionLinker<>(i, info -> ins.getCallbackSupplier().get().call(i, info)));
                        break;
                    }
                }
            }
        }

        ConfigurationSection aa = bp.getConfig().getConfigurationSection("arenas");
        try {
            for(String s : aa.getKeys(false)){
                Arena a = BattleApi.getInstance().getArena(s);
                if(a == null){
                    getLogger().warning("Arena not configured in arenas.yml: " + s);
                    continue;
                }
                bp.arenas.put(s, ConfigHelper.readConfig(aa.getConfigurationSection(s), ConfigSchema.of(ExArena.class), new ExArena(a)));
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }
}
