package su.nightexpress.dungeons.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.selection.SelectionType;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Enums;

import java.util.ArrayList;
import java.util.Optional;

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
    public static ArgumentNodeBuilder<SelectionType> forSelectionType(@NotNull DungeonPlugin plugin) {
        return Commands.argument(TYPE, (context, string) -> Enums.parse(string, SelectionType.class)
                .orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_SELECTION_ARGUMENT))
            )
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_TYPE)
            .suggestions((reader, context) -> Enums.getNames(SelectionType.class));
    }

    @NotNull
    public static ArgumentNodeBuilder<DungeonConfig> forDungeon(@NotNull DungeonPlugin plugin) {
        return Commands.argument(DUNGEON, (context, string) -> Optional.ofNullable(plugin.getDungeonManager().getDungeonById(string))
                .orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_DUNGEON_ARGUMENT))
            )
            .localized(Lang.COMMAND_ARGUMENT_NAME_DUNGEON)
            .suggestions((reader, context) -> new ArrayList<>(plugin.getDungeonManager().getDungeonIds()));
    }

    @NotNull
    public static ArgumentNodeBuilder<Kit> forKit(@NotNull DungeonPlugin plugin) {
        return Commands.argument(KIT, (context, string) -> Optional.ofNullable(plugin.getKitManager().getKitById(string))
                .orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_KIT_ARGUMENT))
            )
            .localized(Lang.COMMAND_ARGUMENT_NAME_KIT)
            .suggestions((reader, context) -> new ArrayList<>(plugin.getKitManager().getKitIds()));
    }

    @Nullable
    public static DungeonInstance getDungeonInstance(@NotNull DungeonPlugin plugin, @NotNull CommandContext context) {
        Player player = context.getPlayerOrThrow();
        return plugin.getDungeonManager().getInstance(player);
    }

    @Nullable
    public static DungeonConfig getDungeonConfig(@NotNull DungeonPlugin plugin, @NotNull CommandContext context) {
        return context.getArguments().contains(DUNGEON) ? context.getArguments().get(DUNGEON, DungeonConfig.class) : null;
    }

    @Nullable
    public static Spot getSpot(@NotNull DungeonPlugin plugin, @NotNull CommandContext context) {
        DungeonConfig config = getDungeonConfig(plugin, context);
        if (config == null) return null;

        String arg = context.getArguments().contains(SPOT) ? context.getArguments().getString(SPOT) : null;
        return arg == null ? null : config.getSpotById(arg);
    }
}
