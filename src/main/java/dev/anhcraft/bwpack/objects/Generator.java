package dev.anhcraft.bwpack.objects;

import dev.anhcraft.battle.api.misc.ConfigurableObject;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Schema
public class Generator extends ConfigurableObject {
    @Key("name")
    private String name;

    @Key("locations")
    private List<Location> locations = new ArrayList<>();

    @Key("tiers")
    private Tier[] tiers = new Tier[0];

    @NotNull
    public List<Location> getLocations() {
        return locations;
    }

    @NotNull
    public Tier[] getTiers() {
        return tiers;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null){
            switch (entry.getKey()){
                case "locations": {
                    return ((List<?>) value).stream()
                            .map(s -> LocationUtil.fromString(s.toString()))
                            .collect(Collectors.toList());
                }
                case "tiers": {
                    ConfigurationSection cs = (ConfigurationSection) value;
                    Set<String> x = cs.getKeys(false);
                    Tier[] tiers = new Tier[x.size()];
                    int i = 0;
                    for(String s : x){
                        try {
                            tiers[i++] = ConfigHelper.readConfig(cs.getConfigurationSection(s), ConfigSchema.of(Tier.class));
                        } catch (InvalidValueException e) {
                            e.printStackTrace();
                        }
                    }
                    return tiers;
                }
            }
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null){
            switch (entry.getKey()) {
                case "locations": {
                    return locations.stream().map(LocationUtil::toString).collect(Collectors.toList());
                }
                case "tiers": {
                    ConfigurationSection parent = new YamlConfiguration();
                    for(int i = 0; i < tiers.length; i++){
                        YamlConfiguration c = new YamlConfiguration();
                        ConfigHelper.writeConfig(c, ConfigSchema.of(Tier.class), tiers[i]);
                        parent.set(String.valueOf(i++), c);
                    }
                    return parent;
                }
            }
        }
        return value;
    }
}
