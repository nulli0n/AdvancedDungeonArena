package su.nightexpress.dungeons.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.selection.SelectionType;
import su.nightexpress.nightcore.command.experimental.TabContext;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.ArrayList;

public class CommandArguments {

    public static final String PLAYER     = "player";
    public static final String TYPE       = "type";
    public static final String NAME       = "name";
    public static final String DUNGEON    = "dungeon";
    public static final String KIT        = "kit";
    public static final String STAGE      = "stage";
    public static final String LEVEL      = "level";
    public static final String AMOUNT     = "amount";
    public static final String SPOT       = "spot";
    public static final String REWARD     = "reward";
    public static final String LOOT_CHEST = "lootchest";
    public static final String WEIGHT     = "weight";
    public static final String STATE      = "state";

    @NotNull
    public static ArgumentBuilder<SelectionType> forSelectionType(@NotNull DungeonPlugin plugin) {
        return CommandArgument.builder(TYPE, (string, context) -> StringUtil.getEnum(string, SelectionType.class).orElse(null))
            .localized(Lang.COMMAND_ARGUMENT_NAME_TYPE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_SELECTION_ARGUMENT)
            .withSamples(context -> Lists.getEnums(SelectionType.class));
    }

    @NotNull
    public static ArgumentBuilder<DungeonConfig> forDungeon(@NotNull DungeonPlugin plugin) {
        return CommandArgument.builder(DUNGEON, (string, context) -> plugin.getDungeonManager().getDungeonById(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_DUNGEON)
            .customFailure(Lang.ERROR_COMMAND_INVALID_DUNGEON_ARGUMENT)
            .withSamples(context -> new ArrayList<>(plugin.getDungeonManager().getDungeonIds()));
    }

    @NotNull
    public static ArgumentBuilder<Kit> forKit(@NotNull DungeonPlugin plugin) {
        return CommandArgument.builder(KIT, (string, context) -> plugin.getKitManager().getKitById(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_KIT)
            .customFailure(Lang.ERROR_COMMAND_INVALID_KIT_ARGUMENT)
            .withSamples(context -> new ArrayList<>(plugin.getKitManager().getKitIds()));
    }

    @Nullable
    public static DungeonInstance getDungeonInstance(@NotNull DungeonPlugin plugin, @NotNull TabContext context) {
        Player player = context.getPlayerOrThrow();
        return plugin.getDungeonManager().getInstance(player);
    }

    @Nullable
    public static DungeonConfig getDungeonConfig(@NotNull DungeonPlugin plugin, @NotNull TabContext context) {
        String arg = context.getCachedArgument(DUNGEON);
        return arg == null ? null : plugin.getDungeonManager().getDungeonById(arg);
    }

    @Nullable
    public static Spot getSpot(@NotNull DungeonPlugin plugin, @NotNull TabContext context) {
        DungeonConfig config = getDungeonConfig(plugin, context);
        if (config == null) return null;

        String arg = context.getCachedArgument(SPOT);
        return arg == null ? null : config.getSpotById(arg);
    }
}
