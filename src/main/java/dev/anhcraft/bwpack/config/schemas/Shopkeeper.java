package dev.anhcraft.bwpack.config.schemas;

import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection"})
@Configurable
public class Shopkeeper {
    @Setting
    @Path("entity_type")
    @Validation(notNull = true, silent = true)
    private EntityType entityType = EntityType.VILLAGER;

    @Setting
    @Validation(notNull = true)
    private String category;

    @Setting
    @Validation(notNull = true, silent = true)
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
