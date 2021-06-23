package dev.anhcraft.bwpack.config.schemas;

import dev.anhcraft.config.annotations.*;
import dev.anhcraft.jvmkit.utils.CollectionUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Tier {
    private static final SecureRandom RANDOMIZER = new SecureRandom();

    @Setting
    @Path("produce_delay")
    private long produceDelay;

    @Setting
    @Validation(notEmpty = true, notNull = true)
    private Map<String, ItemChoice> items;

    private double maxChance;

    public long getProduceDelay() {
        return produceDelay;
    }

    @NotNull
    public ItemChoice randomizeItem(){
        double x = RANDOMIZER.nextDouble() * maxChance;
        Iterator<ItemChoice> it = getItems().iterator();
        ItemChoice q = it.next();
        while (it.hasNext()){
            ItemChoice ic = it.next();
            double c = ic.getChance();
            if(x < c) {
                q = ic;
            }
        }
        return q;
    }

    @NotNull
    public Collection<ItemChoice> getItems() {
        return items.values();
    }

    @PostHandler
    private void handle(){
        for (ItemChoice it : items.values()) {
            maxChance = Math.max(maxChance, it.getChance());
        }
    }
}
