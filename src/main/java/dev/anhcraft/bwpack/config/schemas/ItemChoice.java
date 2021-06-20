package dev.anhcraft.bwpack.config.schemas;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class ItemChoice {
    private static final SecureRandom RANDOMIZER = new SecureRandom();

    @Setting
    @Validation(notNull = true)
    private Material material;

    @Setting
    @Path("amount.min")
    private int minAmount = 1;

    @Setting
    @Path("amount.max")
    private int maxAmount = 1;

    @Setting
    private double chance;

    @NotNull
    public Material getMaterial() {
        return material;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getChance() {
        return chance;
    }

    @NotNull
    public ItemStack createItem(){
        return new ItemStack(material, minAmount + RANDOMIZER.nextInt(maxAmount - minAmount + 1));
    }
}
