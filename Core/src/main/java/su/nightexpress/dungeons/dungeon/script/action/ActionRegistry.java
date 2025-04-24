package su.nightexpress.dungeons.dungeon.script.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.script.action.impl.*;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {

    private static final Map<String, Loader> ACTION_LOADERS = new HashMap<>();

    public static void load() {
        addLoader(ActionId.ADD_TASK, AddTaskAction::load);
        addLoader(ActionId.DUNGEON_END, DungeonEndAction::load);
        addLoader(ActionId.REMOVE_TASK, RemoveTaskAction::load);
        addLoader(ActionId.RESET_SPOT, ResetSpotAction::load);
        addLoader(ActionId.REVIVE_PLAYERS, RevivePlayersAction::load);
        addLoader(ActionId.RUN_COMMAND, RunCommandAction::load);
        addLoader(ActionId.SET_STAGE, SetStageAction::load);
        addLoader(ActionId.SET_LEVEL, SetLevelAction::load);
        addLoader(ActionId.SET_SPOT, SetSpotAction::load);
        addLoader(ActionId.SPAWN_MOB, SpawnMobAction::load);
        addLoader(ActionId.GIVE_REWARD, GiveRewardAction::load);
        addLoader(ActionId.GENERATE_LOOT, GenerateLootAction::load);
        addLoader(ActionId.CREATE_VAR, CreateVarAction::load);
        addLoader(ActionId.MODIFY_VAR, ModifyVarAction::load);
    }

    public static void clear() {
        ACTION_LOADERS.clear();
    }

    @Nullable
    public static Action loadAction(@NotNull String name, @NotNull FileConfig config, @NotNull String path) {
        Loader loader = getLoader(name);
        if (loader == null) return null;

        return loader.load(config, path);
    }

    @Nullable
    public static Loader getLoader(@NotNull String name) {
        return ACTION_LOADERS.get(name.toLowerCase());
    }

    public static void addLoader(@NotNull String name, @NotNull Loader loader) {
        ACTION_LOADERS.put(name.toLowerCase(), loader);
    }

    public interface Loader {

        Action load(@NotNull FileConfig config, @NotNull String path);

    }
}
