package dev.anhcraft.bwpack;

import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.craftkit.cb_common.BoundingBox;
import dev.anhcraft.jvmkit.utils.PresentPair;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class ActivePool {
    public static class Potion {
        private final List<Player> players = new ArrayList<>();
        private PotionEffectType type;
        private int amplifier;
        private BoundingBox boundingBox;
        private Predicate<PresentPair<Game, BWTeam>> validator;

        public Potion(PotionEffectType type, int amplifier, BoundingBox boundingBox, Predicate<PresentPair<Game, BWTeam>> validator) {
            this.type = type;
            this.amplifier = amplifier;
            this.boundingBox = boundingBox;
            this.validator = validator;
        }

        public boolean isInPool(@NotNull Player player){
            return boundingBox.contains(player.getLocation());
        }

        public void add(@NotNull Player player){
            for(Player p : players){
                if(p.equals(player)) return;
            }
            players.add(player);
            player.addPotionEffect(new PotionEffect(type, 999999, amplifier), true);
        }

        public void remove(@NotNull Player player){
            for (Iterator<Player> it = players.iterator(); it.hasNext(); ) {
                Player p = it.next();
                if(p.equals(player)) {
                    p.removePotionEffect(type);
                    it.remove();
                    break;
                }
            }
        }

        public void removeAll(){
            for(Player p : players){
                p.removePotionEffect(type);
            }
            players.clear();
        }

        @NotNull
        public Predicate<PresentPair<Game, BWTeam>> getValidator() {
            return validator;
        }
    }

    private Map<Integer, Potion> potions = new HashMap<>();
    private BoundingBox boundingBox;
    private int hash;

    public ActivePool(int hash) {
        this.hash = hash;
    }

    private synchronized void recalculate(){
        boundingBox = null;
        if(!potions.isEmpty()) {
            for (Potion p : potions.values()) {
                if (boundingBox == null) {
                    boundingBox = new BoundingBox(p.boundingBox);
                } else {
                    boundingBox.union(p.boundingBox);
                }
            }
            boundingBox.expand(1, 1, 1);
        }
    }

    @Nullable
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @NotNull
    public Potion getPotion(int index){
        return potions.get(index);
    }

    public void removePotion(int index){
        Potion x = potions.remove(index);
        if(x != null){
            x.removeAll();
        }
        recalculate();
    }

    public void removeAllPotion(){
        potions.values().forEach(Potion::removeAll);
        potions.clear();
        boundingBox = null;
    }

    public void setPotion(int index, Potion potion){
        Potion x = potions.put(index, potion);
        if(x != null){
            x.removeAll();
        }
        recalculate();
    }

    @NotNull
    public Collection<Potion> getPotions() {
        return potions.values();
    }

    @Override
    public int hashCode(){
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivePool that = (ActivePool) o;
        return hash == that.hash;
    }
}
