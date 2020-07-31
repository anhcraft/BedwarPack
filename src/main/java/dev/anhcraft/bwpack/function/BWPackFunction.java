package dev.anhcraft.bwpack.function;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.game.Mode;
import dev.anhcraft.battle.api.arena.game.controllers.BedWarController;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.effect.potion.BattlePotionEffectType;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.bwpack.features.ItemGenerator;
import dev.anhcraft.bwpack.BedwarPack;
import dev.anhcraft.bwpack.features.PotionPool;
import dev.anhcraft.bwpack.utils.TargetPlayer;
import dev.anhcraft.craftkit.cb_common.BoundingBox;
import dev.anhcraft.inst.VM;
import dev.anhcraft.inst.annotations.Function;
import dev.anhcraft.inst.annotations.Namespace;
import dev.anhcraft.inst.lang.Reference;
import dev.anhcraft.inst.values.BoolVal;
import dev.anhcraft.inst.values.IntVal;
import dev.anhcraft.inst.values.StringVal;
import dev.anhcraft.jvmkit.utils.EnumUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

@Namespace("BedwarPack")
public class BWPackFunction extends GuiHandler {
    public BWPackFunction(SlotReport report) {
        super(report);
    }

    @Function("PreGeneratorUpgrade")
    public void preGeneratorUpgrade(VM vm, Reference resultVar, StringVal generator) {
        Player p = report.getPlayer();
        LocalGame game = BattleApi.getInstance().getArenaManager().getGame(p);
        if(game == null || game.getMode() != Mode.BEDWAR) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        BedWarController bw = (BedWarController) Mode.BEDWAR.getController();
        if(bw == null) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        TeamManager<BWTeam> teamManager = bw.getTeamManager(game);
        if(teamManager == null) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        BWTeam team = teamManager.getTeam(p);
        if(team == null) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        String gen = generator.getData();
        if(gen.equalsIgnoreCase("local")) {
            for (ItemGenerator ag : BedwarPack.getInstance().itemGenerators.get(game)) {
                if (ag.getOwner() != null && ag.getOwner() == team) {
                    if (ag.getLevel() == ag.getGenerator().getTiers().length - 1) {
                        p.sendMessage(BedwarPack.getInstance().config.getMessage("level_up_local_gen_failed"));
                        vm.setVariable(resultVar.getTarget(), new BoolVal(false));
                    } else {
                        vm.setVariable(resultVar.getTarget(), new BoolVal(true));
                    }
                    return;
                }
            }
        } else {
            for (ItemGenerator ag : BedwarPack.getInstance().itemGenerators.get(game)) {
                if(ag.getOwner() == null && gen.equals(ag.getGenerator().getName())){
                    if (ag.getLevel() == ag.getGenerator().getTiers().length - 1) {
                        p.sendMessage(BedwarPack.getInstance().config.getMessage("level_up_shared_gen_failed"));
                        vm.setVariable(resultVar.getTarget(), new BoolVal(false));
                    } else {
                        vm.setVariable(resultVar.getTarget(), new BoolVal(true));
                    }
                    return;
                }
            }
        }
        vm.setVariable(resultVar.getTarget(), new BoolVal(false));
    }

    @Function("PostGeneratorUpgrade")
    public void postGeneratorUpgrade(StringVal generator){
        Player p = report.getPlayer();
        LocalGame game = BattleApi.getInstance().getArenaManager().getGame(p);
        if(game == null || game.getMode() != Mode.BEDWAR) return;
        BedWarController bw = (BedWarController) Mode.BEDWAR.getController();
        if(bw == null) return;
        TeamManager<BWTeam> teamManager = bw.getTeamManager(game);
        if(teamManager == null) return;
        BWTeam team = teamManager.getTeam(p);
        if(team == null) return;
        String gen = generator.getData();
        ItemGenerator ig = null;
        if(gen.equalsIgnoreCase("local")) {
            for (ItemGenerator ag : BedwarPack.getInstance().itemGenerators.get(game)) {
                if (ag.getOwner() != null && ag.getOwner() == team && ag.getLevel() < ag.getGenerator().getTiers().length - 1) {
                    ig = ag;
                    break;
                }
            }
        } else {
            for (ItemGenerator ag : BedwarPack.getInstance().itemGenerators.get(game)) {
                if(ag.getOwner() == null && gen.equals(ag.getGenerator().getName()) && ag.getLevel() < ag.getGenerator().getTiers().length - 1) {
                    ig = ag;
                    break;
                }
            }
        }
        if(ig == null) return;
        ig.levelUp();
        String str = ig.getOwner() == null && ig.getGenerator().getName() != null ? new InfoHolder("")
                .inform("name", ig.getGenerator().getName())
                .inform("tier", ig.getLevel() + 1)
                .inform("player", report.getPlayer().getName())
                .compile()
                .replace(Objects.requireNonNull(BedwarPack.getInstance().config.getMessage("level_up_shared_gen_success"))) : new InfoHolder("")
                .inform("tier", ig.getLevel() + 1)
                .inform("player", report.getPlayer().getName())
                .compile()
                .replace(Objects.requireNonNull(BedwarPack.getInstance().config.getMessage("level_up_local_gen_success")));
        for (Player player : teamManager.getPlayers(team)) {
            player.sendMessage(str);
        }
    }

    @Function("HasPotionInPool")
    public void hasPotionInPool(VM vm, Reference resultVar, StringVal potion, StringVal target){
        BattlePotionEffectType potionEffect = (BattlePotionEffectType) EnumUtil.findEnum(BattlePotionEffectType.class, potion.getData().toUpperCase());
        TargetPlayer targetPlayer = (TargetPlayer) EnumUtil.findEnum(TargetPlayer.class, target.getData().toUpperCase());
        Player p = report.getPlayer();
        LocalGame game = BattleApi.getInstance().getArenaManager().getGame(p);
        if(game == null || game.getMode() != Mode.BEDWAR) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        BedWarController bw = (BedWarController) Mode.BEDWAR.getController();
        if(bw == null) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        TeamManager<BWTeam> teamManager = bw.getTeamManager(game);
        if(teamManager == null) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        BWTeam team = teamManager.getTeam(p);
        if(team == null) {
            vm.setVariable(resultVar.getTarget(), new BoolVal(false));
            return;
        }
        for(PotionPool pool : BedwarPack.getInstance().potionPools.get(game)) {
            if(pool.check(team, game)){
                PotionEffectType pet = potionEffect == null ? null : potionEffect.asBukkit();
                for(PotionPool.PotionStack stack : pool.getPotions()){
                    if(pet != null && stack.getType() != pet) continue;
                    if(targetPlayer != null && stack.getTarget() != targetPlayer) continue;
                    vm.setVariable(resultVar.getTarget(), new BoolVal(true));
                    return;
                }
                vm.setVariable(resultVar.getTarget(), new BoolVal(false));
                return;
            }
        }
        vm.setVariable(resultVar.getTarget(), new BoolVal(false));
    }

    @Function("AddPotionToPool")
    public void addPotionToPool(StringVal potion, IntVal amplifier, IntVal radius, StringVal target){
        BattlePotionEffectType potionEffect = (BattlePotionEffectType) EnumUtil.findEnum(BattlePotionEffectType.class, potion.getData().toUpperCase());
        if(potionEffect == null) return;
        TargetPlayer targetPlayer = (TargetPlayer) EnumUtil.findEnum(TargetPlayer.class, target.getData().toUpperCase());
        if(targetPlayer == null) return;
        Player p = report.getPlayer();
        LocalGame game = BattleApi.getInstance().getArenaManager().getGame(p);
        if(game == null || game.getMode() != Mode.BEDWAR) return;
        BedWarController bw = (BedWarController) Mode.BEDWAR.getController();
        if(bw == null) return;
        TeamManager<BWTeam> teamManager = bw.getTeamManager(game);
        if(teamManager == null) return;
        BWTeam team = teamManager.getTeam(p);
        if(team == null) return;
        PotionPool potionPool = null;
        for(PotionPool pool : BedwarPack.getInstance().potionPools.get(game)) {
            if(pool.check(team, game)){
                potionPool = pool;
                break;
            }
        }
        if(potionPool == null) {
            potionPool = new PotionPool(team, game);
            BedwarPack.getInstance().potionPools.put(game, potionPool);
        }
        int r = radius.getData();
        potionPool.addPotion(new PotionPool.PotionStack(
                potionEffect.asBukkit(),
                amplifier.getData(),
                BoundingBox.of(team.getCenterSpawnPoint(), r, r, r),
                targetPlayer,
                team
        ));
    }

    @Function("RemovePotionFromPool")
    public void removePotionFromPool(StringVal potion, StringVal target){
        BattlePotionEffectType potionEffect = (BattlePotionEffectType) EnumUtil.findEnum(BattlePotionEffectType.class, potion.getData().toUpperCase());
        TargetPlayer targetPlayer = (TargetPlayer) EnumUtil.findEnum(TargetPlayer.class, target.getData().toUpperCase());
        Player p = report.getPlayer();
        LocalGame game = BattleApi.getInstance().getArenaManager().getGame(p);
        if(game == null || game.getMode() != Mode.BEDWAR) return;
        BedWarController bw = (BedWarController) Mode.BEDWAR.getController();
        if(bw == null) return;
        TeamManager<BWTeam> teamManager = bw.getTeamManager(game);
        if(teamManager == null) return;
        BWTeam team = teamManager.getTeam(p);
        if(team == null) return;
        for(PotionPool pool : BedwarPack.getInstance().potionPools.get(game)) {
            if(pool.check(team, game)){
                pool.removePotion(potionEffect == null ? null : potionEffect.asBukkit(), targetPlayer);
                break;
            }
        }
    }
}
