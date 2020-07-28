package dev.anhcraft.bwpack.features;

import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.bwpack.utils.TargetPlayer;
import dev.anhcraft.craftkit.cb_common.BoundingBox;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PotionPool {
    public static class PotionStack {
        private final List<Player> players = new ArrayList<>();
        private final PotionEffectType type;
        private final int amplifier;
        private final BoundingBox boundingBox;
        private final TargetPlayer target;
        private final BWTeam owner;

        public PotionStack(PotionEffectType type, int amplifier, BoundingBox boundingBox, TargetPlayer target, BWTeam owner) {
            this.type = type;
            this.amplifier = amplifier;
            this.boundingBox = boundingBox;
            this.target = target;
            this.owner = owner;
        }

        @NotNull
        public BWTeam getOwner() {
            return owner;
        }

        @NotNull
        public List<Player> getPlayers() {
            return players;
        }

        @NotNull
        public PotionEffectType getType() {
            return type;
        }

        public int getAmplifier() {
            return amplifier;
        }

        @NotNull
        public BoundingBox getBoundingBox() {
            return boundingBox;
        }

        @NotNull
        public TargetPlayer getTarget() {
            return target;
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
    }

    private final Map<Integer, PotionStack> potions = new HashMap<>();
    private final int hash;

    public PotionPool(BWTeam team, Game game) {
        this.hash = Objects.hash(team, game);
    }

    public boolean check(BWTeam team, Game game){
        return Objects.hash(team, game) == hash;
    }

    public void removePotion(@Nullable PotionEffectType pet, @Nullable TargetPlayer target){
        for (Iterator<Map.Entry<Integer, PotionStack>> it = potions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, PotionStack> e = it.next();
            if(pet != null && e.getValue().type != pet) continue;
            if(target != null && e.getValue().target != target) continue;
            e.getValue().removeAll();
            it.remove();
        }
    }

    public void removeAllPotion(){
        potions.values().forEach(PotionStack::removeAll);
        potions.clear();
    }

    public void addPotion(PotionStack potion){
        PotionStack x = potions.put(Objects.hash(potion.type, potion.target), potion);
        if(x != null){
            x.removeAll();
        }
    }

    @NotNull
    public Collection<PotionStack> getPotions() {
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
        PotionPool that = (PotionPool) o;
        return hash == that.hash;
    }
}
