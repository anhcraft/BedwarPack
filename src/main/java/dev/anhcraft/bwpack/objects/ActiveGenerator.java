package dev.anhcraft.bwpack.objects;

import dev.anhcraft.battle.api.arena.team.BWTeam;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActiveGenerator {
    private Location location;
    private Generator generator;
    private int level;
    private long nextSpawn;
    private BWTeam owner;

    public ActiveGenerator(@NotNull Location location, @NotNull Generator generator, @Nullable BWTeam owner) {
        this.generator = generator;
        this.location = location;
        this.owner = owner;
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
        return generator.getTiers()[level];
    }

    public boolean levelUp(){
        if(level == generator.getTiers().length - 1) return false;
        else {
            level++;
            return true;
        }
    }

    public void handle(){
        long t = System.currentTimeMillis();
        if(t >= nextSpawn){
            location.getWorld().dropItemNaturally(location, getTier().randomizeItem().createItem());
            nextSpawn = t + getTier().getProduceDelay()*50;
        }
    }

    @Nullable
    public BWTeam getOwner() {
        return owner;
    }
}
