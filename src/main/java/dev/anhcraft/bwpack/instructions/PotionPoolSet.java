package dev.anhcraft.bwpack.instructions;

import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.effect.potion.BattlePotionEffectType;
import dev.anhcraft.battle.utils.functions.Instruction;
import dev.anhcraft.bwpack.ActivePool;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.TargetPlayer;
import dev.anhcraft.bwpack.schemas.ExArena;
import dev.anhcraft.craftkit.cb_common.BoundingBox;
import dev.anhcraft.jvmkit.lang.enumeration.RegEx;
import dev.anhcraft.jvmkit.utils.EnumUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PotionPoolSet implements InstructionCallback<TransactionInfo> {
    @Override
    public void call(@NotNull Instruction instruction, @NotNull TransactionInfo info) {
        if(info.getEvent() == null) {
            if (instruction.getArgs().length < 5) {
                BedwarPack.getInstance().getLogger().warning("Missing instructions arguments: " + ExInstruction.SET_POTION_POOL.name());
                return;
            }
            String slot = instruction.getArgs()[0].trim();
            if (!RegEx.INTEGER.valid(slot)) {
                BedwarPack.getInstance().getLogger().warning("Invalid arg[0] " + ExInstruction.SET_POTION_POOL.name());
                return;
            }
            BattlePotionEffectType type = (BattlePotionEffectType) EnumUtil.findEnum(BattlePotionEffectType.class, instruction.getArgs()[1].trim().toUpperCase());
            if(type == null){
                BedwarPack.getInstance().getLogger().warning("Arg[1]: No potion type matched " + ExInstruction.SET_POTION_POOL.name());
                return;
            }
            String amplifier = instruction.getArgs()[2].trim();
            if (!RegEx.INTEGER.valid(amplifier)) {
                BedwarPack.getInstance().getLogger().warning("Invalid arg[2] " + ExInstruction.SET_POTION_POOL.name());
                return;
            }
            String radius = instruction.getArgs()[3].trim();
            if (!RegEx.DECIMAL.valid(radius)) {
                BedwarPack.getInstance().getLogger().warning("Invalid arg[3] " + ExInstruction.SET_POTION_POOL.name());
                return;
            }
            TargetPlayer target = (TargetPlayer) EnumUtil.findEnum(TargetPlayer.class, instruction.getArgs()[4].trim().toUpperCase());
            if(target == null){
                BedwarPack.getInstance().getLogger().warning("Arg[4]: No target player matched " + ExInstruction.SET_POTION_POOL.name());
                return;
            }
            LocalGame game = (LocalGame) info.getWindow().getDataContainer().get("bpm1");
            BWTeam bwt = (BWTeam) info.getWindow().getDataContainer().get("bpm3");
            ExArena ea = BedwarPack.getInstance().arenas.get(game.getArena().getId());
            if (ea != null) {
                ActivePool activePool = ea.getActivePools().get(bwt);
                if (activePool == null) {
                    ea.getActivePools().put(bwt, activePool = new ActivePool(Objects.hash(bwt, game)));
                    BedwarPack.getInstance().world2pools.put(bwt.getCenterSpawnPoint().getWorld().getName(), activePool);
                }
                double r = Double.parseDouble(radius);
                activePool.setPotion(Integer.parseInt(slot), new ActivePool.Potion(
                        type.asBukkit(),
                        Integer.parseInt(amplifier),
                        BoundingBox.of(bwt.getCenterSpawnPoint(), r, r, r),
                        f -> {
                            if(f.getFirst().equals(game)) {
                                switch (target){
                                    case ALL:
                                        return true;
                                    case ENEMIES:
                                        return !f.getSecond().equals(bwt);
                                    case TEAMMATES:
                                        return f.getSecond().equals(bwt);
                                }
                            }
                            return false;
                        }
                ));
            }
        }
    }
}
