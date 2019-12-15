package dev.anhcraft.bwpack.objects;

import dev.anhcraft.battle.api.misc.ConfigurableObject;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private List<Location> locations = new ArrayList<>();

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
        return locations;
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("locations")) {
            return ((List<?>) value).stream()
                    .map(s -> LocationUtil.fromString(s.toString()))
                    .collect(Collectors.toList());
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("locations")) {
            return locations.stream().map(LocationUtil::toString).collect(Collectors.toList());
        }
        return value;
    }
}
