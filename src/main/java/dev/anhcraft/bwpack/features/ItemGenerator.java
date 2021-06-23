package dev.anhcraft.bwpack.features;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.bwpack.config.schemas.Generator;
import dev.anhcraft.bwpack.config.schemas.Tier;
import dev.anhcraft.jvmkit.utils.CollectionUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ItemGenerator {
    private final Location location;
    private final Generator generator;
    private int level;
    private long nextSpawn;
    private final BWTeam owner;
    private final List<ArmorStand> hologram;

    public ItemGenerator(@NotNull Location location, @NotNull Generator generator, @Nullable BWTeam owner, List<ArmorStand> hologram) {
        this.generator = generator;
        this.location = location;
        this.owner = owner;
        this.hologram = hologram;
        this.nextSpawn = System.currentTimeMillis();
    }

    public long getNextSpawn() {
        return nextSpawn;
    }

    public int getLevel() {
        return level;
    }

    @NotNull
    public Generator getGenerator() {
        return generator;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Tier getTier(){
        return Objects.requireNonNull(CollectionUtil.getElementAt(level, generator.getTiers()));
    }

    public boolean levelUp(){
        if(level == generator.getTiers().size() - 1) return false;
        else {
            level++;
            return true;
        }
    }

    public void handle(){
        long t = System.currentTimeMillis();
        if(hologram != null && generator.getHologramLines() != null) {
            long delta = Math.max(0, nextSpawn - t);
            InfoReplacer holder = new InfoHolder("")
                    .inform("tier", level + 1)
                    .inform("time_left", BattleApi.getInstance().formatShortFormDateMinutes(new Date(delta)))
                    .compile();
            int i = hologram.size() - 1;
            for(ArmorStand ent : hologram) {
                String str = generator.getHologramLines().get(i);
                ent.setCustomName(holder.replace(str));
                i--;
            }
        }
        if(t >= nextSpawn){
            location.getWorld().dropItemNaturally(location, getTier().randomizeItem().createItem());
            nextSpawn = t + getTier().getProduceDelay()*50;
        }
    }

    @Nullable
    public BWTeam getOwner() {
        return owner;
    }

    @Nullable
    public List<ArmorStand> getHologram() {
        return hologram;
    }
}
