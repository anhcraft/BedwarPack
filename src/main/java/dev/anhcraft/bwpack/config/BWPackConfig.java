package dev.anhcraft.bwpack.config;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.config.schemas.BedwarArena;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BWPackConfig {
    private final Map<String, String> messages = new HashMap<>();
    private final Map<String, BedwarArena> arenas = new HashMap<>();

    public void reload(){
        BedwarPack bp = BedwarPack.getInstance();
        bp.saveDefaultConfig();
        bp.reloadConfig();
        FileConfiguration conf = bp.getConfig();
        ConfigurationSection msgConf = conf.getConfigurationSection("messages");
        for(String s : msgConf.getKeys(false)){
            messages.put(s, ChatUtil.formatColorCodes(msgConf.getString(s)));
        }
        ConfigurationSection arenaConf = conf.getConfigurationSection("arenas");
        try {
            for(String id : arenaConf.getKeys(false)){
                Arena a = BattleApi.getInstance().getArena(id);
                if(a == null){
                    bp.getLogger().warning("Arena not configured in arenas.yml: " + id);
                    continue;
                }
                arenas.put(id, ConfigHelper.readConfig(arenaConf.getConfigurationSection(id), ConfigSchema.of(BedwarArena.class)));
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public String getMessage(@Nullable String path){
        return messages.get(path);
    }

    @Nullable
    public BedwarArena getArena(@Nullable String id){
        return arenas.get(id);
    }
}
