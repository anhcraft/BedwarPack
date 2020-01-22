package dev.anhcraft.bwpack.instructions;

import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.utils.functions.Instruction;
import dev.anhcraft.bwpack.ActiveGenerator;
import dev.anhcraft.bwpack.BedwarPack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SharedGenUpgrade implements InstructionCallback<TransactionInfo> {
    @Override
    public void call(@NotNull Instruction instruction, @NotNull TransactionInfo info) {
        if(instruction.getArgs().length == 0){
            BedwarPack.getInstance().getLogger().warning("Missing instructions arguments: " + ExInstruction.SHARED_GEN_UPGRADE.name());
            return;
        }
        LocalGame game = (LocalGame) info.getWindow().getDataContainer().get("bpm1");
        if(info.getEvent() == null) {
            ActiveGenerator xag = (ActiveGenerator) info.getWindow().getDataContainer().remove("sgu");
            if (xag != null) {
                String str = String.format(
                        BedwarPack.getInstance().messages.get("level_up_shared_gen_success"),
                        info.getPlayer().getName(), xag.getGenerator().getName(), xag.getLevel() + 1
                );
                for (Player p : game.getPlayers().keySet()) {
                    p.sendMessage(str);
                }
            }
            return;
        }
        for(ActiveGenerator ag : BedwarPack.getInstance().activeGenerators.get(game)){
            if(ag.getOwner() == null && instruction.getArgs()[0].equals(ag.getGenerator().getName())){
                if(ag.getLevel() == ag.getGenerator().getTiers().length-1){
                    info.getPlayer().sendMessage(BedwarPack.getInstance().messages.get("level_up_shared_gen_failed"));
                    info.getEvent().setCancelled(true);
                    info.getEvent().setHasEnoughBalance(true);
                } else {
                    info.getWindow().getDataContainer().put("sgu", ag);
                }
                break;
            }
        }
    }
}
