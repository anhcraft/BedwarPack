package dev.anhcraft.bwpack.schemas;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class Tier extends ConfigurableObject {
    private static final SecureRandom RANDOMIZER = new SecureRandom();

    @Key("produce_delay")
    private long produceDelay;

    @Key("items")
    @Validation(notEmptyList = true)
    private List<ItemChoice> items = new ArrayList<>();

    private double maxChance;

    public long getProduceDelay() {
        return produceDelay;
    }

    @NotNull
    public ItemChoice randomizeItem(){
        double x = RANDOMIZER.nextDouble() * maxChance;
        ItemChoice q = items.get(0);
        for (int i = 1; i < items.size(); i++){
            ItemChoice ic = items.get(i);
            double c = ic.getChance();
            if(x < c) {
                q = ic;
            }
        }
        return q == null ? Objects.requireNonNull(RandomUtil.pickRandom(items)) : q;
    }

    @NotNull
    public List<ItemChoice> getItems() {
        return items;
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("items")){
            ConfigurationSection cs = (ConfigurationSection) value;
            List<ItemChoice> itlt = new ArrayList<>();
            for(String s : cs.getKeys(false)){
                try {
                    ItemChoice x = ConfigHelper.readConfig(cs.getConfigurationSection(s), ConfigSchema.of(ItemChoice.class));
                    if(maxChance < x.getChance()) {
                        maxChance = x.getChance();
                    }
                    itlt.add(x);
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            itlt.sort(Comparator.comparingDouble(ItemChoice::getChance).reversed());
            return itlt;
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("items")){
            ConfigurationSection parent = new YamlConfiguration();
            int i = 0;
            for(ItemChoice ic : (List<ItemChoice>) value){
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, ConfigSchema.of(ItemChoice.class), ic);
                parent.set(String.valueOf(i++), c);
            }
            return parent;
        }
        return value;
    }
}
