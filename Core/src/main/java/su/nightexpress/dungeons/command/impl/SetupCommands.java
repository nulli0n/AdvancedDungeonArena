package su.nightexpress.dungeons.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.command.CommandArguments;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;

import java.util.ArrayList;
import java.util.Collections;

public class SetupCommands {

    public static void load(@NotNull DungeonPlugin plugin, @NotNull HubNodeBuilder root) {
        root.branch(Commands.literal(Placeholders.ALIAS_CREATE)
            .playerOnly()
            .description(Lang.COMMAND_CREATE_DESC)
            .permission(Perms.COMMAND_CREATE)
            .withArguments(Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME))
            .executes((context, arguments) -> createDungeon(plugin, context, arguments))
        );

        root.branch(Commands.literal(Placeholders.ALIAS_SET_PROTECTION)
            .playerOnly()
            .description(Lang.COMMAND_SET_PROTECTION_DESC)
            .permission(Perms.COMMAND_SET_PROTECTION)
            .withArguments(CommandArguments.forDungeon(plugin))
            .executes((context, arguments) -> setProtection(plugin, context, arguments))
        );

        root.branch(Commands.literal(Placeholders.ALIAS_SET_LOBBY)
            .playerOnly()
            .description(Lang.COMMAND_SET_LOBBY_DESC)
            .permission(Perms.COMMAND_SET_LOBBY)
            .withArguments(CommandArguments.forDungeon(plugin))
            .executes((context, arguments) -> setLobby(plugin, context, arguments))
        );

        root.branch(Commands.hub("spawner")
            .description(Lang.COMMAND_SPAWNER_DESC)
            .permission(Perms.COMMAND_SPAWNER)
            .branch(Commands.literal("create")
                .playerOnly()
                .description(Lang.COMMAND_SPAWNER_CREATE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                        .suggestions((reader, context) -> {
                            DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                            return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpawnerByIdMap().keySet());
                        })
                )
                .executes((context, arguments) -> createSpawner(plugin, context, arguments))
            )
        );

        root.branch(Commands.hub("level")
            .description(Lang.COMMAND_LEVEL_DESC)
            .permission(Perms.COMMAND_LEVEL)
            .branch(Commands.literal("create")
                .playerOnly()
                .description(Lang.COMMAND_LEVEL_CREATE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                )
                .executes((context, arguments) -> createLevel(plugin, context, arguments))
            )
            .branch(Commands.literal("setspawn")
                .playerOnly()
                .description(Lang.COMMAND_LEVEL_SET_SPAWN_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.LEVEL).localized(Lang.COMMAND_ARGUMENT_NAME_LEVEL)
                        .suggestions((reader, context) -> {
                            DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                            return config == null ? Collections.emptyList() : new ArrayList<>(config.getLevelByIdMap().keySet());
                        })
                )
                .executes((context, arguments) -> setLevelSpawn(plugin, context, arguments))
            )
        );

        root.branch(Commands.hub("stage")
            .description(Lang.COMMAND_STAGE_DESC)
            .permission(Perms.COMMAND_STAGE)
            .branch(Commands.literal("create")
                .playerOnly()
                .description(Lang.COMMAND_STAGE_CREATE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                )
                .executes((context, arguments) -> createStage(plugin, context, arguments))
            )
        );

        root.branch(Commands.hub("reward")
            .description(Lang.COMMAND_REWARD_DESC)
            .permission(Perms.COMMAND_REWARD)
            .branch(Commands.literal("create")
                .playerOnly()
                .description(Lang.COMMAND_REWARD_CREATE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                )
                .executes((context, arguments) -> createReward(plugin, context, arguments))
            )
            .branch(Commands.literal("remove")
                .description(Lang.COMMAND_REWARD_REMOVE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.REWARD).localized(Lang.COMMAND_ARGUMENT_NAME_REWARD)
                        .suggestions((reader, context) -> {
                            DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                            return config == null ? Collections.emptyList() : new ArrayList<>(config.getRewardByIdMap().keySet());
                        })
                )
                .executes((context, arguments) -> removeReward(plugin, context, arguments))
            )
            .branch(Commands.literal("additem")
                .playerOnly()
                .description(Lang.COMMAND_REWARD_ADD_ITEM_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.REWARD).localized(Lang.COMMAND_ARGUMENT_NAME_REWARD)
                        .suggestions((reader, context) -> {
                            DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                            return config == null ? Collections.emptyList() : new ArrayList<>(config.getRewardByIdMap().keySet());
                        })
                )
                .executes((context, arguments) -> addRewardItem(plugin, context, arguments))
            )
        );

        root.branch(Commands.hub("lootchest")
            .description(Lang.COMMAND_LOOT_CHEST_DESC)
            .permission(Perms.COMMAND_LOOT_CHEST)
            .branch(Commands.literal("create")
                .playerOnly()
                .description(Lang.COMMAND_LOOT_CHEST_CREATE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                )
                .executes((context, arguments) -> createLootChest(plugin, context, arguments))
            )
            .branch(Commands.literal("remove")
                .description(Lang.COMMAND_LOOT_CHEST_REMOVE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.LOOT_CHEST).localized(Lang.COMMAND_ARGUMENT_NAME_LOOT_CHEST)
                        .suggestions((reader, context) -> {
                            DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                            return config == null ? Collections.emptyList() : new ArrayList<>(config.getLootChestByIdMap().keySet());
                        })
                )
                .executes((context, arguments) -> removeLootChest(plugin, context, arguments))
            )
            .branch(Commands.literal("additem")
                .playerOnly()
                .description(Lang.COMMAND_LOOT_CHEST_ADD_ITEM_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.LOOT_CHEST).localized(Lang.COMMAND_ARGUMENT_NAME_LOOT_CHEST)
                        .suggestions((reader, context) -> {
                            DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                            return config == null ? Collections.emptyList() : new ArrayList<>(config.getLootChestByIdMap().keySet());
                        }),
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME),
                    Arguments.decimal(CommandArguments.WEIGHT).localized(Lang.COMMAND_ARGUMENT_NAME_WEIGHT)
                )
                .executes((context, arguments) -> addLootChestItem(plugin, context, arguments))
            )
        );

        root.branch(Commands.hub("spot")
            .description(Lang.COMMAND_SPOT_DESC)
            .permission(Perms.COMMAND_SPOT)
            .branch(Commands.literal("create")
                .playerOnly()
                .description(Lang.COMMAND_SPOT_CREATE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                        .suggestions((reader, context) -> {
                            DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                            return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpotByIdMap().keySet());
                        })
                )
                .executes((context, arguments) -> createSpot(plugin, context, arguments))
            )
            .branch(Commands.literal("remove")
                .description(Lang.COMMAND_SPOT_REMOVE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.SPOT).localized(Lang.COMMAND_ARGUMENT_NAME_SPOT).suggestions((reader, context)  -> {
                        DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                        return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpotByIdMap().keySet());
                    })
                )
                .executes((context, arguments) -> removeSpot(plugin, context, arguments))
            )
            .branch(Commands.literal("addstate")
                .playerOnly()
                .description(Lang.COMMAND_SPOT_ADD_STATE_DESC)
                .withArguments(
                    CommandArguments.forDungeon(plugin),
                    Arguments.string(CommandArguments.SPOT).localized(Lang.COMMAND_ARGUMENT_NAME_SPOT).suggestions((reader, context)  -> {
                        DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                        return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpotByIdMap().keySet());
                    }),
                    Arguments.string(CommandArguments.STATE).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME).suggestions((reader, context)  -> {
                        Spot spot = CommandArguments.getSpot(plugin, context);
                        return spot == null ? Collections.emptyList() : new ArrayList<>(spot.getStateByIdMap().keySet());
                    })
                )
                .executes((context, arguments) -> addSpotState(plugin, context, arguments))
            )
        );
    }

    private static boolean createDungeon(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String name = arguments.getString(CommandArguments.NAME);

        plugin.getDungeonSetup().createDungeon(player, name);
        return true;
    }

    private static boolean setProtection(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);

        plugin.getDungeonSetup().setProtectionFromSelection(player, dungeon);
        return true;
    }

    private static boolean setLobby(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);

        plugin.getDungeonSetup().setLobby(player, dungeon);
        return true;
    }

    private static boolean createSpawner(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.NAME);

        plugin.getDungeonSetup().createSpawner(player, dungeon, name);
        return true;
    }

    private static boolean setLevelSpawn(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.LEVEL);

        plugin.getDungeonSetup().setLevelSpawn(player, dungeon, name);
        return true;
    }

    private static boolean createLevel(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.NAME);

        plugin.getDungeonSetup().createLevel(player, dungeon, name);
        return true;
    }

    private static boolean createStage(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.NAME);

        plugin.getDungeonSetup().createStage(player, dungeon, name);
        return true;
    }

    private static boolean createReward(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.NAME);

        plugin.getDungeonSetup().createReward(player, dungeonConfig, name);
        return true;
    }

    private static boolean removeReward(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.REWARD);

        plugin.getDungeonSetup().removeReward(player, dungeonConfig, name);
        return true;
    }

    private static boolean addRewardItem(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String rewardId = arguments.getString(CommandArguments.REWARD);

        plugin.getDungeonSetup().addRewardItem(player, dungeonConfig, rewardId);
        return true;
    }

    private static boolean createLootChest(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.NAME);

        plugin.getDungeonSetup().createLootChest(player, dungeonConfig, name);
        return true;
    }

    private static boolean removeLootChest(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.LOOT_CHEST);

        plugin.getDungeonSetup().removeLootChest(player, dungeonConfig, name);
        return true;
    }

    private static boolean addLootChestItem(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String lootdId = arguments.getString(CommandArguments.LOOT_CHEST);
        String itemName = arguments.getString(CommandArguments.NAME);
        double weight = arguments.getDouble(CommandArguments.WEIGHT);

        plugin.getDungeonSetup().addLootChestItem(player, dungeonConfig, lootdId, itemName, weight);
        return true;
    }

    private static boolean createSpot(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.NAME);

        plugin.getDungeonSetup().createSpot(player, dungeonConfig, name);
        return true;
    }

    private static boolean removeSpot(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getString(CommandArguments.SPOT);

        plugin.getDungeonSetup().removeSpot(player, dungeonConfig, name);
        return true;
    }

    private static boolean addSpotState(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.get(CommandArguments.DUNGEON, DungeonConfig.class);
        String spotId = arguments.getString(CommandArguments.SPOT);
        String stateId = arguments.getString(CommandArguments.STATE);

        plugin.getDungeonSetup().addSpotState(player, dungeonConfig, spotId, stateId);
        return true;
    }
}
