package dev.anhcraft.bwpack.config.schemas;

import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection"})
@Configurable
public class Generator {
    @Setting
    private String name;

    @Setting
    @Validation(notNull = true, silent = true)
    private List<String> locations = new ArrayList<>();

    @Setting
    @Validation(notNull = true, silent = true)
    private Map<String, Tier> tiers = new HashMap<>();

    @Setting
    @Path("hologram.enabled")
    private boolean hologramEnabled;

    @Setting
    @Path("hologram.offset")
    private double hologramOffset;

    @Setting
    @Path("hologram.lines")
    private List<String> hologramLines;

    @Nullable
    public String getName() {
        return name;
    }

    @NotNull
    public List<Location> getLocations() {
        return locations.stream().map(LocationUtil::fromString).collect(Collectors.toList());
    }

    @NotNull
    public Collection<Tier> getTiers() {
        return tiers.values();
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
}
