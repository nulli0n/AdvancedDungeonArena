package su.nightexpress.dungeons.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.command.CommandArguments;
import su.nightexpress.dungeons.command.CommandFlags;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.spot.SpotState;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.selection.SelectionType;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.impl.ReloadCommand;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.util.CommandUtil;

import java.util.ArrayList;
import java.util.Collections;

public class BaseCommands {

    public static void load(@NotNull DungeonPlugin plugin) {
        var root = plugin.getRootNode();

        root.addChildren(ReloadCommand.builder(plugin, Perms.COMMAND_RELOAD));

        root.addChildren(DirectNode.builder(plugin, Placeholders.ALIAS_WAND)
            .playerOnly()
            .description(Lang.COMMAND_WAND_DESC)
            .permission(Perms.COMMAND_WAND)
            .withArgument(CommandArguments.forSelectionType(plugin).required())
            .executes((context, arguments) -> getWand(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "browse")
            .description(Lang.COMMAND_BROWSE_DESC)
            .permission(Perms.COMMAND_BROWSE)
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER).permission(Perms.COMMAND_BROWSE_OTHERS))
            .executes((context, arguments) -> browseDungeons(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, Placeholders.ALIAS_JOIN)
            .description(Lang.COMMAND_JOIN_DESC)
            .permission(Perms.COMMAND_JOIN)
            .withArgument(CommandArguments.forDungeon(plugin).required())
            .executes((context, arguments) -> joinDungeon(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "send")
            .description(Lang.COMMAND_SEND_DESC)
            .permission(Perms.COMMAND_SEND)
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.forDungeon(plugin).required())
            .withArgument(CommandArguments.forKit(plugin))
            .withFlag(CommandFlags.force())
            .executes((context, arguments) -> sendToDungeon(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "leave")
            .playerOnly()
            .description(Lang.COMMAND_LEAVE_DESC)
            .permission(Perms.COMMAND_LEAVE)
            .executes((context, arguments) -> leaveDungeon(plugin, context))
        );

        root.addChildren(DirectNode.builder(plugin, "setstage")
            .playerOnly()
            .description(Lang.COMMAND_SET_STAGE_DESC)
            .permission(Perms.COMMAND_SET_STAGE)
            .withArgument(ArgumentTypes.string(CommandArguments.STAGE).required().localized(Lang.COMMAND_ARGUMENT_NAME_STAGE)
                .withSamples(context -> {
                    DungeonInstance instance = CommandArguments.getDungeonInstance(plugin, context);
                    return instance == null ? Collections.emptyList() : new ArrayList<>(instance.getConfig().getStageByIdMap().keySet());
                }))
            .executes((context, arguments) -> setStage(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "setlevel")
            .playerOnly()
            .description(Lang.COMMAND_SET_LEVEL_DESC)
            .permission(Perms.COMMAND_SET_LEVEL)
            .withArgument(ArgumentTypes.string(CommandArguments.LEVEL).required().localized(Lang.COMMAND_ARGUMENT_NAME_LEVEL)
                .withSamples(context -> {
                    DungeonInstance instance = CommandArguments.getDungeonInstance(plugin, context);
                    return instance == null ? Collections.emptyList() : new ArrayList<>(instance.getConfig().getLevelByIdMap().keySet());
                }))
            .executes((context, arguments) -> setLevel(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "setspot")
            .playerOnly()
            .description(Lang.COMMAND_SET_SPOT_DESC)
            .permission(Perms.COMMAND_SET_SPOT)
            .withArgument(CommandArguments.forDungeon(plugin).required())
            .withArgument(ArgumentTypes.string(CommandArguments.SPOT).required().localized(Lang.COMMAND_ARGUMENT_NAME_SPOT)
                .withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpotByIdMap().keySet());
                }))
            .withArgument(ArgumentTypes.string(CommandArguments.STATE).required().localized(Lang.COMMAND_ARGUMENT_NAME_STATE)
                .withSamples(context -> {
                    Spot spot = CommandArguments.getSpot(plugin, context);
                    return spot == null ? Collections.emptyList() : new ArrayList<>(spot.getStateByIdMap().keySet());
                }))
            .executes(BaseCommands::setSpotState)
        );

        root.addChildren(DirectNode.builder(plugin, "start")
            .description(Lang.COMMAND_START_DESC)
            .permission(Perms.COMMAND_START)
            .withArgument(CommandArguments.forDungeon(plugin))
            .executes((context, arguments) -> startGame(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, "stop")
            .description(Lang.COMMAND_STOP_DESC)
            .permission(Perms.COMMAND_STOP)
            .withArgument(CommandArguments.forDungeon(plugin))
            .executes((context, arguments) -> stopGame(plugin, context, arguments))
        );
    }

    private static boolean getWand(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        SelectionType type = arguments.getArgument(CommandArguments.TYPE, SelectionType.class);

        plugin.getSelectionManager().startSelection(player, type);
        return true;
    }

    private static boolean setStage(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonInstance instance = plugin.getDungeonManager().getInstance(player);
        if (instance == null) {
            Lang.DUNGEON_ERROR_MUST_BE_IN.getMessage().send(player);
            return false;
        }

        String stageId = arguments.getStringArgument(CommandArguments.STAGE);
        Stage stage = instance.getConfig().getStageById(stageId);
        if (stage == null) {
            Lang.ERROR_COMMAND_INVALID_STAGE_ARGUMENT.getMessage().send(player, replacer -> replacer.replace(Placeholders.GENERIC_VALUE, stageId));
            return false;
        }

        instance.setStage(stage);
        Lang.DUNGEON_ADMIN_SET_STAGE.getMessage().send(player, replacer -> replacer.replace(instance.replacePlaceholders()).replace(stage.replacePlaceholders()));
        return true;
    }

    private static boolean setLevel(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonInstance instance = plugin.getDungeonManager().getInstance(player);
        if (instance == null) {
            Lang.DUNGEON_ERROR_MUST_BE_IN.getMessage().send(player);
            return false;
        }

        String levelId = arguments.getStringArgument(CommandArguments.LEVEL);
        Level level = instance.getConfig().getLevelById(levelId);
        if (level == null) {
            Lang.ERROR_COMMAND_INVALID_LEVEL_ARGUMENT.getMessage().send(player, replacer -> replacer.replace(Placeholders.GENERIC_VALUE, levelId));
            return false;
        }

        instance.setLevel(level);
        Lang.DUNGEON_ADMIN_SET_LEVEL.getMessage().send(player, replacer -> replacer.replace(instance.replacePlaceholders()).replace(level.replacePlaceholders()));
        return true;
    }

    private static boolean setSpotState(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig config = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);

        String spotId = arguments.getStringArgument(CommandArguments.SPOT);
        Spot spot = config.getSpotById(spotId);
        if (spot == null) {
            Lang.ERROR_COMMAND_INVALID_SPOT_ARGUMENT.getMessage().send(player, replacer -> replacer.replace(Placeholders.GENERIC_VALUE, spotId));
            return false;
        }

        String stateId = arguments.getStringArgument(CommandArguments.STATE);
        SpotState state = spot.getState(stateId);
        if (state == null) {
            Lang.ERROR_COMMAND_INVALID_STATE_ARGUMENT.getMessage().send(player, replacer -> replacer.replace(Placeholders.GENERIC_VALUE, stateId));
            return false;
        }

        DungeonInstance dungeon = config.getInstance();

        if (dungeon.getState() == GameState.INGAME) {
            dungeon.setSpotState(spot, state);
        }
        else if (dungeon.isActive()) {
            spot.build(dungeon.getWorld(), state);
        }

        Lang.DUNGEON_ADMIN_SET_SPOT.getMessage().send(player, replacer -> replacer
            .replace(config.replacePlaceholders())
            .replace(spot.replacePlaceholders())
            .replace(state.replacePlaceholders())
        );
        return true;
    }

    private static boolean browseDungeons(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) return false;

        plugin.getDungeonManager().browseDungeons(player);
        return true;
    }

    private static boolean joinDungeon(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = CommandUtil.getPlayerOrSender(context, arguments, CommandArguments.PLAYER);
        if (player == null) return false;

        DungeonConfig config = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        plugin.getDungeonManager().prepareForInstance(player, config.getInstance());
        return true;
    }

    private static boolean sendToDungeon(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = arguments.getPlayerArgument(CommandArguments.PLAYER);
        DungeonConfig config = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        Kit kit = arguments.hasArgument(CommandArguments.KIT) ? arguments.getArgument(CommandArguments.KIT, Kit.class) : null;
        boolean force = arguments.hasFlag(CommandFlags.FORCE);
        DungeonInstance instance = config.getInstance();

        boolean result = plugin.getDungeonManager().enterInstance(player, instance, kit, force);
        (result ? Lang.DUNGEON_SEND_SENT : Lang.DUNGEON_SEND_FAIL).getMessage().send(context.getSender(), replacer -> replacer
            .replace(instance.replacePlaceholders())
            .replace(Placeholders.forPlayer(player))
        );
        return true;
    }

    private static boolean leaveDungeon(@NotNull DungeonPlugin plugin, @NotNull CommandContext context) {
        Player player = context.getPlayerOrThrow();

        plugin.getDungeonManager().leaveInstance(player);
        return true;
    }

    private static boolean startGame(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        DungeonInstance dungeon;

        if (arguments.hasArgument(CommandArguments.DUNGEON)) {
            DungeonConfig config = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
            dungeon = config.getInstance();
        }
        else {
            if (!context.isPlayer()) {
                context.errorPlayerOnly();
                return false;
            }

            Player player = context.getPlayerOrThrow();
            dungeon = plugin.getDungeonManager().getInstance(player);
            if (dungeon == null) {
                context.send(Lang.DUNGEON_ERROR_MUST_BE_IN);
                return false;
            }
        }

        if (!dungeon.isReadyToStart()) {
            context.send(Lang.DUNGEON_ERROR_NOT_READY_TO_GAME, replacer -> replacer.replace(dungeon.replacePlaceholders()));
            return false;
        }

        dungeon.setCountdown(0);
        context.send(Lang.DUNGEON_START_DONE, replacer -> replacer.replace(dungeon.replacePlaceholders()));
        return true;
    }

    private static boolean stopGame(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        DungeonInstance dungeon;

        if (arguments.hasArgument(CommandArguments.DUNGEON)) {
            DungeonConfig config = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
            dungeon = config.getInstance();
        }
        else {
            if (!context.isPlayer()) {
                context.errorPlayerOnly();
                return false;
            }

            Player player = context.getPlayerOrThrow();
            dungeon = plugin.getDungeonManager().getInstance(player);
            if (dungeon == null) {
                context.send(Lang.DUNGEON_ERROR_MUST_BE_IN);
                return false;
            }
        }

        if (dungeon.getState() != GameState.INGAME || dungeon.isAboutToEnd()) {
            context.send(Lang.DUNGEON_ERROR_NOT_IN_GAME, replacer -> replacer.replace(dungeon.replacePlaceholders()));
            return false;
        }

        dungeon.stop();
        context.send(Lang.DUNGEON_ADMIN_STOP, replacer -> replacer.replace(dungeon.replacePlaceholders()));
        return true;
    }
}
