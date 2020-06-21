package dev.anhcraft.bwpack.instructions;

import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.utils.functions.Instruction;
import dev.anhcraft.bwpack.ActivePool;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.schemas.ExArena;
import dev.anhcraft.jvmkit.lang.enumeration.RegEx;
import org.jetbrains.annotations.NotNull;

public class PotionPoolRemove implements InstructionCallback<TransactionInfo> {
    @Override
    public void call(@NotNull Instruction instruction, @NotNull TransactionInfo info) {
        if(info.getEvent() == null) {
            if (instruction.getArgs().length == 0) {
                BedwarPack.getInstance().getLogger().warning("Missing instructions arguments: " + ExInstruction.REMOVE_POTION_POOL.name());
                return;
            }
            String s = instruction.getArgs()[0].trim();
            boolean isNum = RegEx.INTEGER.valid(s);
            if (isNum || s.equals("*")) {
                LocalGame game = (LocalGame) info.getWindow().getDataContainer().get("bpm1");
                BWTeam bwt = (BWTeam) info.getWindow().getDataContainer().get("bpm3");
                ExArena ea = BedwarPack.getInstance().arenas.get(game.getArena().getId());
                if (ea != null) {
                    ActivePool activePool = ea.getActivePools().get(bwt);
                    if (activePool != null) {
                        if (isNum) {
                            activePool.removePotion(Integer.parseInt(s));
                        } else {
                            activePool.removeAllPotion();
                        }
                    }
                }
            } else {
                BedwarPack.getInstance().getLogger().warning("Invalid arg[0] " + ExInstruction.REMOVE_POTION_POOL.name());
            }
        }
    }
}
