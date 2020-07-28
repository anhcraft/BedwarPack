package dev.anhcraft.bwpack.config.schemas;

import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class BedwarArena extends ConfigurableObject {
    @Key("local_generator")
    private Generator localGenerator;

    @Key("shared_generators")
    private List<Generator> sharedGenerators;

    @Key("shopkeepers")
    private List<Shopkeeper> shopkeepers;

    @Nullable
    public Generator getLocalGenerator() {
        return localGenerator;
    }

    @Nullable
    public List<Generator> getSharedGenerators() {
        return sharedGenerators;
    }

    @Nullable
    public List<Shopkeeper> getShopkeepers() {
        return shopkeepers;
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null){
            switch (entry.getKey()){
                case "shared_generators": {
                    ConfigurationSection cs = (ConfigurationSection) value;
                    List<Generator> igs = new ArrayList<>();
                    for(String s : cs.getKeys(false)){
                        try {
                            igs.add(ConfigHelper.readConfig(cs.getConfigurationSection(s), ConfigSchema.of(Generator.class)));
                        } catch (InvalidValueException e) {
                            e.printStackTrace();
                        }
                    }
                    return igs;
                }
                case "shopkeepers": {
                    ConfigurationSection cs = (ConfigurationSection) value;
                    List<Shopkeeper> skp = new ArrayList<>();
                    for(String s : cs.getKeys(false)){
                        try {
                            skp.add(ConfigHelper.readConfig(cs.getConfigurationSection(s), ConfigSchema.of(Shopkeeper.class)));
                        } catch (InvalidValueException e) {
                            e.printStackTrace();
                        }
                    }
                    return skp;
                }
            }
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null){
            switch (entry.getKey()) {
                case "shared_generators": {
                    ConfigurationSection parent = new YamlConfiguration();
                    int i = 0;
                    for (Generator ig : (List<Generator>) value) {
                        YamlConfiguration c = new YamlConfiguration();
                        ConfigHelper.writeConfig(c, ConfigSchema.of(Generator.class), ig);
                        parent.set(String.valueOf(i++), c);
                    }
                    return parent;
                }
                case "shopkeepers": {
                    ConfigurationSection parent = new YamlConfiguration();
                    int i = 0;
                    for (Shopkeeper skp : (List<Shopkeeper>) value) {
                        YamlConfiguration c = new YamlConfiguration();
                        ConfigHelper.writeConfig(c, ConfigSchema.of(Shopkeeper.class), skp);
                        parent.set(String.valueOf(i++), c);
                    }
                    return parent;
                }
            }
        }
        return value;
    }
}
