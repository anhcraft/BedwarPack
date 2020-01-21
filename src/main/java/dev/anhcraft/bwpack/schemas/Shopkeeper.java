package dev.anhcraft.bwpack.schemas;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema
public class Shopkeeper extends ConfigurableObject {
    @Key("entity_type")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
    private EntityType entityType = EntityType.VILLAGER;

    @Key("category")
    @Validation(notNull = true)
    private String category;

    @Key("locations")
    private List<String> locations = new ArrayList<>();

    @NotNull
    public EntityType getEntityType() {
        return entityType;
    }

    @NotNull
    public String getCategory() {
        return category;
    }

    @NotNull
    public List<Location> getLocations() {
        return locations.stream().map(LocationUtil::fromString).collect(Collectors.toList());
    }
}
