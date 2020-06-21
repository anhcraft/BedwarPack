package dev.anhcraft.bwpack.schemas;

import dev.anhcraft.battle.utils.EnumUtil;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class AutoDyeItem {
    @Key("material")
    private String material;

    @Key("amount")
    private int amount;

    @NotNull
    public String getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    public ItemStack createItem(@NotNull DyeColor color){
        return new ItemStack(EnumUtil.getEnum(Material.values(), String.format(material, color.name())), amount);
    }
}
