package su.nightexpress.dungeons.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.command.CommandArguments;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;

import java.util.ArrayList;
import java.util.Collections;

public class SetupCommands {

    public static void load(@NotNull DungeonPlugin plugin, @NotNull ChainedNode root) {
        root.addChildren(DirectNode.builder(plugin, Placeholders.ALIAS_CREATE)
            .playerOnly()
            .description(Lang.COMMAND_CREATE_DESC)
            .permission(Perms.COMMAND_CREATE)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
            .executes((context, arguments) -> createDungeon(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, Placeholders.ALIAS_SET_PROTECTION)
            .playerOnly()
            .description(Lang.COMMAND_SET_PROTECTION_DESC)
            .permission(Perms.COMMAND_SET_PROTECTION)
            .withArgument(CommandArguments.forDungeon(plugin).required())
            .executes((context, arguments) -> setProtection(plugin, context, arguments))
        );

        root.addChildren(DirectNode.builder(plugin, Placeholders.ALIAS_SET_LOBBY)
            .playerOnly()
            .description(Lang.COMMAND_SET_LOBBY_DESC)
            .permission(Perms.COMMAND_SET_LOBBY)
            .withArgument(CommandArguments.forDungeon(plugin).required())
            .executes((context, arguments) -> setLobby(plugin, context, arguments))
        );

        root.addChildren(ChainedNode.builder(plugin, "spawner")
            .description(Lang.COMMAND_SPAWNER_DESC)
            .permission(Perms.COMMAND_SPAWNER)
            .addDirect("create", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_SPAWNER_CREATE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpawnerByIdMap().keySet());
                }))
                .executes((context, arguments) -> createSpawner(plugin, context, arguments))
            )
        );

        root.addChildren(ChainedNode.builder(plugin, "level")
            .description(Lang.COMMAND_LEVEL_DESC)
            .permission(Perms.COMMAND_LEVEL)
            .addDirect("create", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_LEVEL_CREATE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
                .executes((context, arguments) -> createLevel(plugin, context, arguments))
            )
            .addDirect("setspawn", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_LEVEL_SET_SPAWN_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.LEVEL).required().localized(Lang.COMMAND_ARGUMENT_NAME_LEVEL).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getLevelByIdMap().keySet());
                }))
                .executes((context, arguments) -> setLevelSpawn(plugin, context, arguments))
            )
        );

        root.addChildren(ChainedNode.builder(plugin, "stage")
            .description(Lang.COMMAND_STAGE_DESC)
            .permission(Perms.COMMAND_STAGE)
            .addDirect("create", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_STAGE_CREATE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
                .executes((context, arguments) -> createStage(plugin, context, arguments))
            )
        );

        root.addChildren(ChainedNode.builder(plugin, "reward")
            .description(Lang.COMMAND_REWARD_DESC)
            .permission(Perms.COMMAND_REWARD)
            .addDirect("create", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_REWARD_CREATE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
                .executes((context, arguments) -> createReward(plugin, context, arguments))
            )
            .addDirect("remove", builder -> builder
                .description(Lang.COMMAND_REWARD_REMOVE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.REWARD).required().localized(Lang.COMMAND_ARGUMENT_NAME_REWARD).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getRewardByIdMap().keySet());
                }))
                .executes((context, arguments) -> removeReward(plugin, context, arguments))
            )
            .addDirect("additem", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_REWARD_ADD_ITEM_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.REWARD).required().localized(Lang.COMMAND_ARGUMENT_NAME_REWARD).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getRewardByIdMap().keySet());
                }))
                .executes((context, arguments) -> addRewardItem(plugin, context, arguments))
            )
        );

        root.addChildren(ChainedNode.builder(plugin, "lootchest")
            .description(Lang.COMMAND_LOOT_CHEST_DESC)
            .permission(Perms.COMMAND_LOOT_CHEST)
            .addDirect("create", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_LOOT_CHEST_CREATE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
                .executes((context, arguments) -> createLootChest(plugin, context, arguments))
            )
            .addDirect("remove", builder -> builder
                .description(Lang.COMMAND_LOOT_CHEST_REMOVE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.LOOT_CHEST).required().localized(Lang.COMMAND_ARGUMENT_NAME_LOOT_CHEST).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getLootChestByIdMap().keySet());
                }))
                .executes((context, arguments) -> removeLootChest(plugin, context, arguments))
            )
            .addDirect("additem", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_LOOT_CHEST_ADD_ITEM_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.LOOT_CHEST).required().localized(Lang.COMMAND_ARGUMENT_NAME_LOOT_CHEST).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getLootChestByIdMap().keySet());
                }))
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).localized(Lang.COMMAND_ARGUMENT_NAME_NAME).required())
                .withArgument(ArgumentTypes.decimalAbs(CommandArguments.WEIGHT).localized(Lang.COMMAND_ARGUMENT_NAME_WEIGHT).required())
                .executes((context, arguments) -> addLootChestItem(plugin, context, arguments))
            )
        );

        root.addChildren(ChainedNode.builder(plugin, "spot")
            .description(Lang.COMMAND_SPOT_DESC)
            .permission(Perms.COMMAND_SPOT)
            .addDirect("create", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_SPOT_CREATE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpotByIdMap().keySet());
                }))
                .executes((context, arguments) -> createSpot(plugin, context, arguments))
            )
            .addDirect("remove", builder -> builder
                .description(Lang.COMMAND_SPOT_REMOVE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.SPOT).required().localized(Lang.COMMAND_ARGUMENT_NAME_SPOT).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpotByIdMap().keySet());
                }))
                .executes((context, arguments) -> removeSpot(plugin, context, arguments))
            )
            .addDirect("addstate", builder -> builder
                .playerOnly()
                .description(Lang.COMMAND_SPOT_ADD_STATE_DESC)
                .withArgument(CommandArguments.forDungeon(plugin).required())
                .withArgument(ArgumentTypes.string(CommandArguments.SPOT).required().localized(Lang.COMMAND_ARGUMENT_NAME_SPOT).withSamples(context -> {
                    DungeonConfig config = CommandArguments.getDungeonConfig(plugin, context);
                    return config == null ? Collections.emptyList() : new ArrayList<>(config.getSpotByIdMap().keySet());
                }))
                .withArgument(ArgumentTypes.string(CommandArguments.STATE).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME).withSamples(context -> {
                    Spot spot = CommandArguments.getSpot(plugin, context);
                    return spot == null ? Collections.emptyList() : new ArrayList<>(spot.getStateByIdMap().keySet());
                }))
                .executes((context, arguments) -> addSpotState(plugin, context, arguments))
            )
        );
    }

    private static boolean createDungeon(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getDungeonSetup().createDungeon(player, name);
        return true;
    }

    private static boolean setProtection(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);

        plugin.getDungeonSetup().setProtectionFromSelection(player, dungeon);
        return true;
    }

    private static boolean setLobby(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);

        plugin.getDungeonSetup().setLobby(player, dungeon);
        return true;
    }

    private static boolean createSpawner(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getDungeonSetup().createSpawner(player, dungeon, name);
        return true;
    }

    private static boolean setLevelSpawn(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.LEVEL);

        plugin.getDungeonSetup().setLevelSpawn(player, dungeon, name);
        return true;
    }

    private static boolean createLevel(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getDungeonSetup().createLevel(player, dungeon, name);
        return true;
    }

    private static boolean createStage(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeon = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getDungeonSetup().createStage(player, dungeon, name);
        return true;
    }

    private static boolean createReward(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getDungeonSetup().createReward(player, dungeonConfig, name);
        return true;
    }

    private static boolean removeReward(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.REWARD);

        plugin.getDungeonSetup().removeReward(player, dungeonConfig, name);
        return true;
    }

    private static boolean addRewardItem(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String rewardId = arguments.getStringArgument(CommandArguments.REWARD);

        plugin.getDungeonSetup().addRewardItem(player, dungeonConfig, rewardId);
        return true;
    }

    private static boolean createLootChest(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getDungeonSetup().createLootChest(player, dungeonConfig, name);
        return true;
    }

    private static boolean removeLootChest(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.LOOT_CHEST);

        plugin.getDungeonSetup().removeLootChest(player, dungeonConfig, name);
        return true;
    }

    private static boolean addLootChestItem(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String lootdId = arguments.getStringArgument(CommandArguments.LOOT_CHEST);
        String itemName = arguments.getStringArgument(CommandArguments.NAME);
        double weight = arguments.getDoubleArgument(CommandArguments.WEIGHT);

        plugin.getDungeonSetup().addLootChestItem(player, dungeonConfig, lootdId, itemName, weight);
        return true;
    }

    private static boolean createSpot(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getDungeonSetup().createSpot(player, dungeonConfig, name);
        return true;
    }

    private static boolean removeSpot(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String name = arguments.getStringArgument(CommandArguments.SPOT);

        plugin.getDungeonSetup().removeSpot(player, dungeonConfig, name);
        return true;
    }

    private static boolean addSpotState(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        DungeonConfig dungeonConfig = arguments.getArgument(CommandArguments.DUNGEON, DungeonConfig.class);
        String spotId = arguments.getStringArgument(CommandArguments.SPOT);
        String stateId = arguments.getStringArgument(CommandArguments.STATE);

        plugin.getDungeonSetup().addSpotState(player, dungeonConfig, spotId, stateId);
        return true;
    }
}
