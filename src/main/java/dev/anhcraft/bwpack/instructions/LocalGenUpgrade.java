package dev.anhcraft.bwpack.instructions;

import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.utils.functions.Instruction;
import dev.anhcraft.bwpack.ActiveGenerator;
import dev.anhcraft.bwpack.BedwarPack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LocalGenUpgrade implements InstructionCallback<TransactionInfo> {
    @Override
    public void call(@NotNull Instruction instruction, @NotNull TransactionInfo info) {
        if(info.getEvent() == null) {
            ActiveGenerator xag = (ActiveGenerator) info.getWindow().getDataContainer().remove("lgu1");
            if (xag != null) {
                List<Player> r = (List<Player>) info.getWindow().getDataContainer().remove("lgu2");
                if (r != null) {
                    xag.levelUp();
                    String str = String.format(
                            BedwarPack.getInstance().messages.get("level_up_local_gen_success"),
                            info.getPlayer().getName(), xag.getLevel() + 1
                    );
                    for (Player p : r) {
                        p.sendMessage(str);
                    }
                }
            }
            return;
        }
        LocalGame game = (LocalGame) info.getWindow().getDataContainer().get("bpm1");
        TeamManager<BWTeam> tm = (TeamManager<BWTeam>) info.getWindow().getDataContainer().get("bpm2");
        BWTeam bwt = (BWTeam) info.getWindow().getDataContainer().get("bpm3");
        for(ActiveGenerator ag : BedwarPack.getInstance().activeGenerators.get(game)){
            if(ag.getOwner() != null && ag.getOwner() == bwt){
                if(ag.getLevel() == ag.getGenerator().getTiers().length-1){
                    info.getPlayer().sendMessage(BedwarPack.getInstance().messages.get("level_up_local_gen_failed"));
                    info.getEvent().setCancelled(true);
                    info.getEvent().setHasEnoughBalance(true);
                } else {
                    info.getWindow().getDataContainer().put("lgu1", ag);
                    info.getWindow().getDataContainer().put("lgu2", tm.getPlayers(bwt));
                }
                break;
            }
        }
    }
}
