package dev.anhcraft.bwpack.stats;

import dev.anhcraft.battle.api.stats.IntCounter;
import org.jetbrains.annotations.NotNull;

public class BedDestroyStat extends IntCounter {
    @Override
    public @NotNull String getId() {
        return "bed_destruction";
    }
}
