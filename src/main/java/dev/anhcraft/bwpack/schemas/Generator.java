package dev.anhcraft.bwpack.schemas;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
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
    private List<String> locations = new ArrayList<>();

    @Key("tiers")
    private Tier[] tiers = new Tier[0];

    @Key("hologram.enabled")
    private boolean hologramEnabled;

    @Key("hologram.offset")
    private double hologramOffset;

    @Key("hologram.lines")
    private List<String> hologramLines;

    @NotNull
    public List<Location> getLocations() {
        return locations.stream().map(LocationUtil::fromString).collect(Collectors.toList());
    }

    @NotNull
    public Tier[] getTiers() {
        return tiers;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public boolean isHologramEnabled() {
        return hologramEnabled;
    }

    public double getHologramOffset() {
        return hologramOffset;
    }

    @Nullable
    public List<String> getHologramLines() {
        return hologramLines;
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null){
            if (entry.getKey().equals("tiers")) {
                ConfigurationSection cs = (ConfigurationSection) value;
                Set<String> x = cs.getKeys(false);
                Tier[] tiers = new Tier[x.size()];
                int i = 0;
                for (String s : x) {
                    try {
                        tiers[i++] = ConfigHelper.readConfig(cs.getConfigurationSection(s), ConfigSchema.of(Tier.class));
                    } catch (InvalidValueException e) {
                        e.printStackTrace();
                    }
                }
                return tiers;
            }
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null){
            if (entry.getKey().equals("tiers")) {
                ConfigurationSection parent = new YamlConfiguration();
                for (int i = 0; i < tiers.length; i++) {
                    YamlConfiguration c = new YamlConfiguration();
                    ConfigHelper.writeConfig(c, ConfigSchema.of(Tier.class), tiers[i]);
                    parent.set(String.valueOf(i++), c);
                }
                return parent;
            }
        }
        return value;
    }
}
