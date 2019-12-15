package dev.anhcraft.bwpack;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.BattleAPI;
import dev.anhcraft.battle.api.game.BWTeam;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.api.game.TeamManager;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.mode.BattleBedWar;
import dev.anhcraft.battle.api.mode.Mode;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.battle.utils.functions.Instruction;
import dev.anhcraft.bwpack.objects.ActiveGenerator;
import dev.anhcraft.bwpack.objects.AutoDyeItem;
import dev.anhcraft.bwpack.objects.ExArena;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class ConfigLoader {
    private BedwarPack bp;

    public ConfigLoader(BedwarPack bedwarPack) {
        bp = bedwarPack;
    }

    public void reloadConf(){
        bp.saveDefaultConfig();
        bp.reloadConfig();

        ConfigurationSection msg = bp.getConfig().getConfigurationSection("messages");
        for(String s : msg.getKeys(false)){
            bp.messages.put(s, ChatUtil.formatColorCodes(msg.getString(s)));
        }

        bp.categories.addAll(bp.getConfig().getStringList("products.category_filter"));
        bp.worlds.addAll(bp.getConfig().getStringList("worlds"));

        try {
            ConfigurationSection ady = bp.getConfig().getConfigurationSection("products.auto_dye_items");
            for (String p : ady.getKeys(false)) {
                ConfigurationSection ics = ady.getConfigurationSection(p);
                for (String i : ics.getKeys(false)) {
                    bp.autoDyeProducts.put(p, ConfigHelper.readConfig(ics.getConfigurationSection(i), ConfigSchema.of(AutoDyeItem.class)));
                }
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }

        ConfigurationSection epe = bp.getConfig().getConfigurationSection("products.extra_executions");
        for(String s : epe.getKeys(false)){
            for (String x : epe.getStringList(s)) {
                Instruction i = Objects.requireNonNull(Instruction.parse(x));
                switch (i.getTarget()){
                    case "upgrade_local_generator": {
                        bp.exInstructions.put(s, new FunctionLinker<>(i, pair -> {
                            Player player = Objects.requireNonNull(pair.getSecond());
                            Window w = Objects.requireNonNull(pair.getFirst());
                            if(pair.getThird() == null) {
                                ActiveGenerator xag = (ActiveGenerator) w.getDataContainer().remove("lgu1");
                                if (xag != null) {
                                    List<Player> r = (List<Player>) w.getDataContainer().remove("lgu2");
                                    if (r != null) {
                                        xag.levelUp();
                                        String str = String.format(bp.messages.get("level_up_local_gen_success"), player.getName(), xag.getLevel() + 1);
                                        for (Player p : r) {
                                            p.sendMessage(str);
                                        }
                                    }
                                }
                                return;
                            }
                            LocalGame game = (LocalGame) w.getDataContainer().get("bpm1");
                            TeamManager<BWTeam> tm = (TeamManager<BWTeam>) w.getDataContainer().get("bpm2");
                            BWTeam bwt = (BWTeam) w.getDataContainer().get("bpm3");
                            for(ActiveGenerator ag : bp.activeGenerators.get(game)){
                                if(ag.getOwner() != null && ag.getOwner() == bwt){
                                    if(ag.getLevel() == ag.getGenerator().getTiers().length-1){
                                        player.sendMessage(bp.messages.get("level_up_local_gen_failed"));
                                        pair.getThird().setCancelled(true);
                                        pair.getThird().setHasEnoughBalance(true);
                                    } else {
                                        w.getDataContainer().put("lgu1", ag);
                                        w.getDataContainer().put("lgu2", tm.getPlayers(bwt));
                                    }
                                    break;
                                }
                            }
                        }));
                        break;
                    }
                    case "upgrade_shared_generator": {
                        bp.exInstructions.put(s, new FunctionLinker<>(i, pair -> {
                            if(i.getArgs().length == 0){
                                bp.getLogger().warning("Missing instructions arguments: upgrade_shared_generator");
                                return;
                            }
                            Player player = Objects.requireNonNull(pair.getSecond());
                            Window w = Objects.requireNonNull(pair.getFirst());
                            LocalGame game = (LocalGame) w.getDataContainer().get("bpm1");

                            if(pair.getThird() == null) {
                                ActiveGenerator xag = (ActiveGenerator) w.getDataContainer().remove("sgu");
                                if (xag != null) {
                                    String str = String.format(bp.messages.get("level_up_shared_gen_success"), player.getName(), xag.getGenerator().getName(), xag.getLevel() + 1);
                                    for (Player p : game.getPlayers().keySet()) {
                                        p.sendMessage(str);
                                    }
                                }
                                return;
                            }
                            for(ActiveGenerator ag : bp.activeGenerators.get(game)){
                                if(ag.getOwner() == null && i.getArgs()[0].equals(ag.getGenerator().getName())){
                                    if(ag.getLevel() == ag.getGenerator().getTiers().length-1){
                                        player.sendMessage(bp.messages.get("level_up_shared_gen_failed"));
                                        pair.getThird().setCancelled(true);
                                        pair.getThird().setHasEnoughBalance(true);
                                    } else {
                                        w.getDataContainer().put("sgu", ag);
                                    }
                                    break;
                                }
                            }
                        }));
                    }
                }
            }
        }

        ConfigurationSection aa = bp.getConfig().getConfigurationSection("arenas");
        try {
            for(String s : aa.getKeys(false)){
                bp.arenas.put(s, ConfigHelper.readConfig(aa.getConfigurationSection(s), ConfigSchema.of(ExArena.class)));
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

}
