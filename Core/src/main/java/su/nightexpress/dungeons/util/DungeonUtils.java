package su.nightexpress.dungeons.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.dungeon.DungeonTarget;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.dungeon.event.DungeonEventHandler;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.dungeons.dungeon.feature.KillStreak;
import su.nightexpress.dungeons.dungeon.feature.board.BoardLayout;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.scale.ScalableAmount;
import su.nightexpress.dungeons.dungeon.script.action.ActionInfo;
import su.nightexpress.dungeons.dungeon.script.action.impl.DungeonEndAction;
import su.nightexpress.dungeons.dungeon.script.action.impl.RunCommandAction;
import su.nightexpress.dungeons.dungeon.script.action.impl.SpawnMobAction;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionInfo;
import su.nightexpress.dungeons.dungeon.script.condition.impl.AliveMobsAmountCondition;
import su.nightexpress.dungeons.dungeon.script.condition.impl.TaskCompletedCondition;
import su.nightexpress.dungeons.dungeon.script.condition.impl.TickIntervalCondition;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparators;
import su.nightexpress.dungeons.dungeon.script.task.TaskParams;
import su.nightexpress.dungeons.dungeon.script.task.impl.KillMobTask;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.mob.MobProviderId;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.util.*;

import static su.nightexpress.dungeons.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class DungeonUtils {

    public static boolean hasPacketLibrary() {
        return hasPacketEvents() || hasProtocolLib();
    }

    public static boolean hasPacketEvents() {
        return Plugins.isInstalled(HookId.PACKET_EVENTS);
    }

    public static boolean hasProtocolLib() {
        return Plugins.isInstalled(HookId.PROTOCOL_LIB);
    }

    public static boolean isKillStreaksEnabled() {
        return Config.KILL_STREAKS_ENABLED.get();
    }

    @Nullable
    public static KillStreak getKillStreak(int kills) {
        return Config.KILL_STREAKS_LIST.get().values().stream()
            .filter(killStreak -> killStreak.isGoodStreak(kills))
            .max(Comparator.comparingInt(KillStreak::getKills))
            .orElse(null);
    }

    public static void setStageDefaults(@NotNull Stage stage) {
        MobIdentifier zombieId = new MobIdentifier(MobProviderId.ADA, BukkitThing.toString(EntityType.ZOMBIE));

        Map<String, ConditionInfo> tickConditionMap = new LinkedHashMap<>();
        tickConditionMap.put("every_5_seconds", new ConditionInfo(false, new TickIntervalCondition(5)));
        tickConditionMap.put("task_incompleted", new ConditionInfo(false, new TaskCompletedCondition("kill_zombies", true)));
        tickConditionMap.put("low_mobs", new ConditionInfo(false, new AliveMobsAmountCondition(NumberComparators.getByOperator("<="), 1, true, MobFaction.ENEMY)));

        Map<String, ActionInfo> tickActionMap = new LinkedHashMap<>();

        ScalableAmount spawnAmount = new ScalableAmount("1", "3", true, Collections.emptyMap());
        ScalableAmount level = new ScalableAmount("1", "1", true, Collections.emptyMap());
        SpawnMobAction spawnMobAction = new SpawnMobAction(zombieId, Placeholders.DEFAULT, spawnAmount, level);
        tickActionMap.put("spawn_zombies", new ActionInfo("every_5_seconds && task_incompleted && low_mobs", 100D, spawnMobAction));

        DungeonEventHandler onTickHandler = new DungeonEventHandler("onDungeonTick", DungeonEventType.DUNGEON_TICK, tickConditionMap, tickActionMap);

        Map<String, ActionInfo> endActionMap = new LinkedHashMap<>();
        endActionMap.put("finish_dungeon", new ActionInfo(null, 100D, new DungeonEndAction(10, true)));
        DungeonEventHandler onFinishHandler = new DungeonEventHandler("onStageFinish", DungeonEventType.STAGE_FINISHED, new HashMap<>(), endActionMap);

        Map<String, StageTask> taskMap = new HashMap<>();
        StageTask stageTask = new StageTask("kill_zombies", new KillMobTask(zombieId), new TaskParams("Kill Zombies", UniInt.of(10, 10), false, true));
        taskMap.put(stageTask.getId(), stageTask);

        stage.addHandler(onTickHandler);
        stage.addHandler(onFinishHandler);
        stage.getTaskMap().putAll(taskMap);
    }

    public static void setLevelDefaults(@NotNull Level level) {
        Map<String, ActionInfo> actions = new LinkedHashMap<>();
        RunCommandAction action = new RunCommandAction(Lists.newList("money give " + Placeholders.PLAYER_NAME + " 1"), DungeonTarget.EVENT_PLAYER);
        actions.put("kill_reward", new ActionInfo(null, 100D, action));

        DungeonEventHandler handler = new DungeonEventHandler("onMobKill", DungeonEventType.MOB_KILLED, new HashMap<>(), actions);

        level.addHandler(handler);
    }

    @NotNull
    public static NightItem getDefaultSelectionItem() {
        return new NightItem(Material.BLAZE_ROD)
            .setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("Selection Wand")))
            .setLore(Lists.newList(
                DARK_GRAY.wrap("(Drop to exit selection mode)"),
                "",
                LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("Left-Click to " + LIGHT_YELLOW.wrap("set 1st") + " point."),
                LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("Right-Click to " + LIGHT_YELLOW.wrap("set 2nd") + " point.")
            ));
    }

    @NotNull
    public static BoardLayout getDefaultBoardLayout() {
        String title = LIGHT_YELLOW.wrap(BOLD.wrap(DUNGEON_NAME));

        List<String> lines = Lists.newList(
            " ",
            GRAY.wrap("Mobs: " + YELLOW.wrap(DUNGEON_ENEMY_MOBS) + " (" + GREEN.wrap(DUNGEON_ALLY_MOBS) + ")"),
            GRAY.wrap("Players: " + YELLOW.wrap(DUNGEON_ALIVE_PLAYERS) + " (" + RED.wrap(DUNGEON_DEAD_PLAYERS) + ")"),
            GRAY.wrap("Timeleft: " + WHITE.wrap(DUNGEON_TIMELEFT)),
            " ",
            YELLOW.wrap(BOLD.wrap("TASKS:")),
            GENERIC_TASKS,
            " ",
            LIGHT_CYAN.wrap(BOLD.wrap("STATS:")),
            GRAY.wrap("Score: " + LIGHT_CYAN.wrap(PLAYER_SCORE)),
            GRAY.wrap("Kills: " + LIGHT_CYAN.wrap(PLAYER_KILLS)),
            GRAY.wrap("Streak: " + LIGHT_CYAN.wrap("x" + PLAYER_KILL_STREAK) + " (" + WHITE.wrap(PLAYER_KILL_STREAK_DECAY) + ")"),
            " "
        );

        return new BoardLayout(/*Placeholders.DEFAULT, */title, lines);
    }
    @NotNull
    public static BoardLayout getDefaultLobbyBoardLayout() {
        String title = LIGHT_YELLOW.wrap(BOLD.wrap(DUNGEON_NAME)) + GRAY.wrap(" - " + WHITE.wrap("Hub"));

        List<String> lines = Lists.newList(
            GRAY.wrap("Status: " + YELLOW.wrap(DUNGEON_STATE)),
            " ",
            YELLOW.wrap(BOLD.wrap("PLAYERS:")),
            GENERIC_PLAYERS,
            " "
        );

        return new BoardLayout(/*Placeholders.DEFAULT, */title, lines);
    }
}
