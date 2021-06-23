package dev.anhcraft.bwpack.config.schemas;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class BedwarArena {
    @Setting
    @Path("local_generator")
    private Generator localGenerator;

    @Setting
    @Path("shared_generators")
    private Map<String, Generator> sharedGenerators;

    @Setting
    private Map<String, Shopkeeper> shopkeepers;

    @Nullable
    public Generator getLocalGenerator() {
        return localGenerator;
    }

    @Nullable
    public Collection<Generator> getSharedGenerators() {
        return sharedGenerators.values();
    }

    @Nullable
    public Collection<Shopkeeper> getShopkeepers() {
        return shopkeepers.values();
    }
}
