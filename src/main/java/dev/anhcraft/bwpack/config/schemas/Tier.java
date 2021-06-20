package dev.anhcraft.bwpack.config.schemas;

import dev.anhcraft.config.annotations.*;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Tier {
    private static final SecureRandom RANDOMIZER = new SecureRandom();

    @Setting
    @Path("produce_delay")
    private long produceDelay;

    @Setting
    @Validation(notEmpty = true, notNull = true)
    private List<ItemChoice> items;

    private double maxChance;

    public long getProduceDelay() {
        return produceDelay;
    }

    @NotNull
    public ItemChoice randomizeItem(){
        double x = RANDOMIZER.nextDouble() * maxChance;
        ItemChoice q = items.get(0);
        for (int i = 1; i < items.size(); i++){
            ItemChoice ic = items.get(i);
            double c = ic.getChance();
            if(x < c) {
                q = ic;
            }
        }
        return q == null ? Objects.requireNonNull(RandomUtil.pickRandom(items)) : q;
    }

    @NotNull
    public List<ItemChoice> getItems() {
        return items;
    }

    @PostHandler
    private void handle(){
        for (ItemChoice it : items) {
            maxChance = Math.max(maxChance, it.getChance());
        }
    }
}
