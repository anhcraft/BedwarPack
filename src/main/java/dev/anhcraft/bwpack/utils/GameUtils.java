package dev.anhcraft.bwpack.utils;

import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.utils.BlockPosition;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class GameUtils {
    public static void addPlacedBlock(Game game, Block block){
        List<BlockPosition> list = (List<BlockPosition>) game.getDataContainer().get("placedBlocks");
        if(list == null) {
            list = new ArrayList<>();
            game.getDataContainer().put("placedBlocks", list);
        }
        list.add(BlockPosition.of(block));
    }

    public static boolean removePlacedBlock(Game game, Block block){
        List<BlockPosition> list = (List<BlockPosition>) game.getDataContainer().get("placedBlocks");
        return list != null && list.remove(BlockPosition.of(block));
    }

    public static boolean isPlacedBlock(Game game, Block block){
        List<BlockPosition> list = (List<BlockPosition>) game.getDataContainer().get("placedBlocks");
        return list != null && list.contains(BlockPosition.of(block));
    }
}
