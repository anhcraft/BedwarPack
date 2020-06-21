package dev.anhcraft.bwpack.schemas;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.PrettyEnum;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class ItemChoice extends ConfigurableObject {
    private static final SecureRandom RANDOMIZER = new SecureRandom();

    @Key("material")
    @Validation(notNull = true)
    @PrettyEnum
    private Material material;

    @Key("amount.min")
    private int minAmount = 1;

    @Key("amount.max")
    private int maxAmount = 1;

    @Key("chance")
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
