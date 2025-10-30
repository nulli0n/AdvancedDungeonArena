package su.nightexpress.dungeons.config;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.selection.SelectionType;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.*;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.nightcore.util.bridge.RegistryType;

import static su.nightexpress.dungeons.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class Lang implements LangContainer {

    public static final RegistryLocale<Attribute> ATTRIBUTE  = LangEntry.builder("Attribute").registry(RegistryType.ATTRIBUTE);
    public static final EnumLocale<GameState>     GAME_STATE = LangEntry.builder("GameState").enumeration(GameState.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_DUNGEON = LangEntry.builder("Command.Argument.Name.Dungeon").text("dungeon");
    public static final TextLocale COMMAND_ARGUMENT_NAME_STAGE   = LangEntry.builder("Command.Argument.Name.Stage").text("stage");
    public static final TextLocale COMMAND_ARGUMENT_NAME_LEVEL      = LangEntry.builder("Command.Argument.Name.Level").text("level");
    public static final TextLocale COMMAND_ARGUMENT_NAME_SPOT       = LangEntry.builder("Command.Argument.Name.Spot").text("spot");
    public static final TextLocale COMMAND_ARGUMENT_NAME_REWARD     = LangEntry.builder("Command.Argument.Name.Reward").text("reward");
    public static final TextLocale COMMAND_ARGUMENT_NAME_STATE      = LangEntry.builder("Command.Argument.Name.State").text("state");
    public static final TextLocale COMMAND_ARGUMENT_NAME_KIT        = LangEntry.builder("Command.Argument.Name.Kit").text("kit");
    public static final TextLocale COMMAND_ARGUMENT_NAME_LOOT_CHEST = LangEntry.builder("Command.Argument.Name.LootChest").text("lootChest");
    public static final TextLocale COMMAND_ARGUMENT_NAME_WEIGHT     = LangEntry.builder("Command.Argument.Name.Weight").text("weight");

    public static final TextLocale COMMAND_BROWSE_DESC = LangEntry.builder("Command.Browse.Desc").text("Browse the dungeons.");
    public static final TextLocale COMMAND_JOIN_DESC   = LangEntry.builder("Command.Join.Desc").text("Enter the dungeon.");
    public static final TextLocale COMMAND_LEAVE_DESC  = LangEntry.builder("Command.Leave.Desc").text("Leave the dungeon.");
    public static final TextLocale COMMAND_SEND_DESC   = LangEntry.builder("Command.Send.Send").text("Send player to the dungeon.");
    public static final TextLocale COMMAND_WAND_DESC   = LangEntry.builder("Command.Wand.Desc").text("Get selection tool.");
    public static final TextLocale COMMAND_CREATE_DESC = LangEntry.builder("Command.Create.Desc").text("Create new dungeon from selection.");

    public static final TextLocale COMMAND_SET_PROTECTION_DESC  = LangEntry.builder("Command.SetProtection.Desc").text("Update dungeon's protection area.");
    public static final TextLocale COMMAND_SET_LOBBY_DESC       = LangEntry.builder("Command.SetLobby.Desc").text("Set dungeon's lobby position.");

    public static final TextLocale COMMAND_SET_STAGE_DESC       = LangEntry.builder("Command.SetStage.Desc").text("Set dungeon's game stage.");
    public static final TextLocale COMMAND_SET_LEVEL_DESC       = LangEntry.builder("Command.SetLevel.Desc").text("Set dungeon's game level.");
    public static final TextLocale COMMAND_SET_SPOT_DESC       = LangEntry.builder("Command.SetSpot.Desc").text("Set dungeon's spot state.");

    public static final TextLocale COMMAND_SPAWNER_DESC        = LangEntry.builder("Command.Spawner.Desc").text("Spawner commands.");
    public static final TextLocale COMMAND_SPAWNER_CREATE_DESC = LangEntry.builder("Command.Spawner.Create.Desc").text("Create a new spawner.");

    public static final TextLocale COMMAND_LEVEL_DESC           = LangEntry.builder("Command.Level.Desc").text("Level commands.");
    public static final TextLocale COMMAND_LEVEL_CREATE_DESC    = LangEntry.builder("Command.Level.Create.Desc").text("Create empty level config.");
    public static final TextLocale COMMAND_LEVEL_SET_SPAWN_DESC = LangEntry.builder("Command.Level.SetSpawn.Desc").text("Set level's spawn.");

    public static final TextLocale COMMAND_STAGE_DESC        = LangEntry.builder("Command.Stage.Desc").text("Stage commands.");
    public static final TextLocale COMMAND_STAGE_CREATE_DESC = LangEntry.builder("Command.Stage.Create.Desc").text("Create empty stage config.");

    public static final TextLocale COMMAND_SPOT_DESC           = LangEntry.builder("Command.Spot.Desc").text("Spot commands.");
    public static final TextLocale COMMAND_SPOT_CREATE_DESC    = LangEntry.builder("Command.Spot.Create.Desc").text("Create a new spot from selection.");
    public static final TextLocale COMMAND_SPOT_REMOVE_DESC    = LangEntry.builder("Command.Spot.Remove.Desc").text("Delete a spot.");
    public static final TextLocale COMMAND_SPOT_ADD_STATE_DESC = LangEntry.builder("Command.Spot.AddState.Desc").text("Add a spot state from selection.");

    public static final TextLocale COMMAND_KIT_DESC           = LangEntry.builder("Command.Kit.Desc").text("Kit Commands.");
    public static final TextLocale COMMAND_KIT_CREATE_DESC    = LangEntry.builder("Command.Kit.Create.Desc").text("Create a new kit.");
    public static final TextLocale COMMAND_KIT_SET_ITEMS_DESC = LangEntry.builder("Command.Kit.SetItems.Desc").text("Update kit with current inventory.");
    public static final TextLocale COMMAND_KIT_GRANT_DESC     = LangEntry.builder("Command.Kit.Grant.Desc").text("Grant kit access to a player.");
    public static final TextLocale COMMAND_KIT_REVOKE_DESC    = LangEntry.builder("Command.Kit.Revoke.Desc").text("Revoke kit access from a player.");

    public static final TextLocale COMMAND_REWARD_DESC          = LangEntry.builder("Command.Reward.Desc").text("Reward commands.");
    public static final TextLocale COMMAND_REWARD_CREATE_DESC   = LangEntry.builder("Command.Reward.Create.Desc").text("Create a new reward.");
    public static final TextLocale COMMAND_REWARD_REMOVE_DESC   = LangEntry.builder("Command.Reward.Remove.Desc").text("Delete a reward.");
    public static final TextLocale COMMAND_REWARD_ADD_ITEM_DESC = LangEntry.builder("Command.Reward.AddItem.Desc").text("Add item to the reward.");

    public static final TextLocale COMMAND_LOOT_CHEST_DESC          = LangEntry.builder("Command.LootChest.Desc").text("Loot Chest commands.");
    public static final TextLocale COMMAND_LOOT_CHEST_CREATE_DESC   = LangEntry.builder("Command.LootChest.Create.Desc").text("Create a new loot chest.");
    public static final TextLocale COMMAND_LOOT_CHEST_REMOVE_DESC   = LangEntry.builder("Command.LootChest.Remove.Desc").text("Delete a loot chest.");
    public static final TextLocale COMMAND_LOOT_CHEST_ADD_ITEM_DESC = LangEntry.builder("Command.LootChest.AddItem.Desc").text("Add item to the loot chest.");

    public static final TextLocale COMMAND_STOP_DESC  = LangEntry.builder("Command.Stop.Desc").text("Force stop the dungeon.");
    public static final TextLocale COMMAND_START_DESC = LangEntry.builder("Command.Start.Desc").text("Force start the dungeon.");

    public static final MessageLocale ERROR_COMMAND_INVALID_SELECTION_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidSelectionType").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid type!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_DUNGEON_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidDungeon").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid dungeon!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_KIT_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidKit").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid kit!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_STAGE_ARGUMENT = LangEntry.builder("Error.Command.InvalidArgument.Stage").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_VALUE) + " is not a valid dungeon stage!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_LEVEL_ARGUMENT = LangEntry.builder("Error.Command.InvalidArgument.Level").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_VALUE) + " is not a valid dungeon level!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_SPOT_ARGUMENT = LangEntry.builder("Error.Command.InvalidArgument.Spot").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_VALUE) + " is not a valid dungeon spot!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_STATE_ARGUMENT = LangEntry.builder("Error.Command.InvalidArgument.State").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_VALUE) + " is not a valid spot state!"));



    public static final MessageLocale SETUP_GENERIC_NO_ITEM = LangEntry.builder("Setup.Generic.NoItem").chatMessage(
        SOFT_RED.wrap("You must hold an item in main hand."));

    public static final MessageLocale SETUP_GENERIC_BAD_ITEM = LangEntry.builder("Setup.Generic.BadItem").chatMessage(
        SOFT_RED.wrap("Unable to get data of the item you hold."));



    public static final MessageLocale SETUP_DUNGEON_EXISTS = LangEntry.builder("Setup.Dungeon.AlreadyExists").chatMessage(
        SOFT_RED.wrap("Dungeon with such ID already exists."));

    public static final MessageLocale SETUP_DUNGEON_CREATED = LangEntry.builder("Setup.Dungeon.Created").chatMessage(
        GRAY.wrap("Dungeon created: " + GREEN.wrap(DUNGEON_NAME) + "!"));



    public static final MessageLocale SETUP_PROTECTION_SET = LangEntry.builder("Setup.Protection.Set").chatMessage(
        GRAY.wrap("Updated protection area for the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));

    public static final MessageLocale SETUP_LOBBY_SET = LangEntry.builder("Setup.Lobby.Set").chatMessage(
        GRAY.wrap("Set lobby position for the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));



    public static final MessageLocale SETUP_SPAWNER_CREATED = LangEntry.builder("Setup.Spawner.Created").chatMessage(
        GRAY.wrap("Spawner created: " + GREEN.wrap(GENERIC_NAME) + "!"));



    public static final MessageLocale SETUP_LEVEL_EXISTS = LangEntry.builder("Setup.Level.AlreadyExists").chatMessage(
        SOFT_RED.wrap("Level with such ID already exists."));

    public static final MessageLocale SETUP_LEVEL_INVALID = LangEntry.builder("Setup.Level.Invalid").chatMessage(
        SOFT_RED.wrap("Invalid level!"));

    public static final MessageLocale SETUP_LEVEL_CREATED = LangEntry.builder("Setup.Level.Created").chatMessage(
        GRAY.wrap("Level created: " + GREEN.wrap(LEVEL_NAME) + "!"));

    public static final MessageLocale SETUP_LEVEL_SPAWN_SET = LangEntry.builder("Setup.Level.SpawnSet").chatMessage(
        GRAY.wrap("Set level spawn: " + GREEN.wrap(LEVEL_NAME) + "!"));



    public static final MessageLocale SETUP_STAGE_EXISTS = LangEntry.builder("Setup.Stage.AlreadyExists").chatMessage(
        SOFT_RED.wrap("Stage with such ID already exists."));

    public static final MessageLocale SETUP_STAGE_CREATED = LangEntry.builder("Setup.Stage.Created").chatMessage(
        GRAY.wrap("Stage created: " + GREEN.wrap(STAGE_NAME) + "!"));



    public static final MessageLocale SETUP_REWARD_EXISTS = LangEntry.builder("Setup.Reward.AlreadyExists").chatMessage(
        SOFT_RED.wrap("Reward with such ID already exists."));

    public static final MessageLocale SETUP_REWARD_INVALID = LangEntry.builder("Setup.Reward.Invalid").chatMessage(
        SOFT_RED.wrap("Invalid reward!"));

    public static final MessageLocale SETUP_REWARD_CREATED = LangEntry.builder("Setup.Reward.Created").chatMessage(
        GRAY.wrap("Reward created: " + GREEN.wrap(REWARD_NAME) + "!"));

    public static final MessageLocale SETUP_REWARD_REMOVED = LangEntry.builder("Setup.Reward.Removed").chatMessage(
        GRAY.wrap("Reward removed: " + RED.wrap(REWARD_NAME) + "!"));

    public static final MessageLocale SETUP_REWARD_ITEM_ADDED = LangEntry.builder("Setup.Reward.ItemAdded").chatMessage(
        GRAY.wrap("Added " + GREEN.wrap(GENERIC_NAME) + " to the " + GREEN.wrap(REWARD_NAME) + " reward!"));



    public static final MessageLocale SETUP_LOOT_CHEST_EXISTS = LangEntry.builder("Setup.LootChest.AlreadyExists").chatMessage(
        SOFT_RED.wrap("Loot Chest with such ID already exists."));

    public static final MessageLocale SETUP_LOOT_CHEST_INVALID = LangEntry.builder("Setup.LootChest.Invalid").chatMessage(
        SOFT_RED.wrap("Invalid loot chest!"));

    public static final MessageLocale SETUP_LOOT_CHEST_NOT_CONTAINER = LangEntry.builder("Setup.LootChest.NotContainer").chatMessage(
        SOFT_RED.wrap("The loot chest block must be a container!"));

    public static final MessageLocale SETUP_LOOT_CHEST_CREATED = LangEntry.builder("Setup.LootChest.Created").chatMessage(
        GRAY.wrap("Loot Chest created: " + GREEN.wrap(LOOT_CHEST_ID) + "!"));

    public static final MessageLocale SETUP_LOOT_CHEST_REMOVED = LangEntry.builder("Setup.LootChest.Removed").chatMessage(
        GRAY.wrap("Loot Chest removed: " + RED.wrap(LOOT_CHEST_ID) + "!"));

    public static final MessageLocale SETUP_LOOT_CHEST_ITEM_ADDED = LangEntry.builder("Setup.LootChest.ItemAdded").chatMessage(
        GRAY.wrap("Added " + GREEN.wrap(GENERIC_NAME) + " to the " + GREEN.wrap(LOOT_CHEST_ID) + " loot chest!"));



    public static final MessageLocale SETUP_SPOT_EXISTS = LangEntry.builder("Setup.Spot.AlreadyExists").chatMessage(
        SOFT_RED.wrap("Spot with such ID already exists."));

    public static final MessageLocale SETUP_SPOT_INVALID = LangEntry.builder("Setup.Spot.Invalid").chatMessage(
        SOFT_RED.wrap("Invalid spot!"));

    public static final MessageLocale SETUP_SPOT_CREATED = LangEntry.builder("Setup.Spot.Created").chatMessage(
        GRAY.wrap("Spot created: " + GREEN.wrap(SPOT_NAME) + "!"));

    public static final MessageLocale SETUP_SPOT_REMOVED = LangEntry.builder("Setup.Spot.Removed").chatMessage(
        GRAY.wrap("Spot removed: " + RED.wrap(SPOT_NAME) + "!"));

    public static final MessageLocale SETUP_SPOT_STATE_ADDED = LangEntry.builder("Setup.Spot.StateCreated").chatMessage(
        GRAY.wrap("Spot state created: " + SOFT_GREEN.wrap(SPOT_STATE_ID) + "!"));



    public static final MessageLocale SETUP_ERROR_INVALID_NAME = LangEntry.builder("Setup.Error.InvalidName").chatMessage(
        SOFT_RED.wrap("Invalid ID. Only latin letters and numbers are allowed."));



    public static final MessageLocale SETUP_SELECTION_ACTIVATED = LangEntry.builder("Setup.Selection.Activated").chatMessage(
        GRAY.wrap("Selection mode " + GREEN.wrap("activated") + "."));

    public static final MessageLocale SETUP_SELECTION_NO_CUBOID = LangEntry.builder("Setup.Selection.NoCuboid").chatMessage(
        SOFT_RED.wrap("You must select cuboid area first: " + SOFT_YELLOW.wrap("/" + ALIAS_BASIC + " " + ALIAS_WAND + " " + SelectionType.CUBOID.name().toLowerCase())));

    public static final MessageLocale SETUP_SELECTION_NO_POSITIONS = LangEntry.builder("Setup.Selection.NoPositions").chatMessage(
        SOFT_RED.wrap("You must select block positions first: " + SOFT_YELLOW.wrap("/" + ALIAS_BASIC + " " + ALIAS_WAND + " " + SelectionType.POSITION.name().toLowerCase())));

    public static final MessageLocale SETUP_SELECTION_CUBOID_OUT_OF_PROTECTION = LangEntry.builder("Setup.Selection.CuboidOutOfProtection").chatMessage(
        SOFT_RED.wrap("Selected cuboid must be inside the " + SOFT_YELLOW.wrap(DUNGEON_NAME) + "'s protection area."));

    public static final MessageLocale SETUP_SELECTION_POSITION_OUT_OF_PROTECTION = LangEntry.builder("Setup.Selection.PositionOutOfProtection").chatMessage(
        SOFT_RED.wrap("Position must be inside the " + SOFT_YELLOW.wrap(DUNGEON_NAME) + "'s protection area."));

    public static final MessageLocale SETUP_SELECTION_DUNGEON_OVERLAP = LangEntry.builder("Setup.Selection.DungeonOverlap").chatMessage(
        SOFT_RED.wrap("Selected area overlaps with other dungeon(s)!"));



    public static final MessageLocale DUNGEON_ERROR_MUST_BE_IN = LangEntry.builder("Dungeon.Error.MustBeIn").chatMessage(
        SOFT_RED.wrap("You're not in a dungeon!"));

    public static final MessageLocale DUNGEON_ERROR_MUST_BE_OUT = LangEntry.builder("Dungeon.Error.MustBeOut").chatMessage(
        SOFT_RED.wrap("You can't do that in dungeon!"));

    public static final MessageLocale DUNGEON_ERROR_NOT_IN_GAME = LangEntry.builder("Dungeon.ForceEnd.NotInGame").chatMessage(
        GRAY.wrap("The " + RED.wrap(DUNGEON_NAME) + " dungeon is not in active game state."));

    public static final MessageLocale DUNGEON_ERROR_NOT_READY_TO_GAME = LangEntry.builder("Dungeon.Error.NotReadyToGame").chatMessage(
        GRAY.wrap("The " + RED.wrap(DUNGEON_NAME) + " dungeon is not ready to start the game."));



    public static final MessageLocale DUNGEON_START_DONE = LangEntry.builder("Dungeon.Admin.Start").chatMessage(
        GRAY.wrap("Started the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));

    public static final MessageLocale DUNGEON_ADMIN_STOP = LangEntry.builder("Dungeon.Admin.Stop").chatMessage(
        GRAY.wrap("Stopped the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));

    public static final MessageLocale DUNGEON_ADMIN_SET_LEVEL = LangEntry.builder("Dungeon.Admin.SetLevel").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(DUNGEON_NAME) + "'s level to " + SOFT_YELLOW.wrap(STAGE_NAME) + "."));

    public static final MessageLocale DUNGEON_ADMIN_SET_STAGE = LangEntry.builder("Dungeon.Admin.SetStage").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(DUNGEON_NAME) + "'s stage to " + SOFT_YELLOW.wrap(STAGE_NAME) + "."));

    public static final MessageLocale DUNGEON_ADMIN_SET_SPOT = LangEntry.builder("Dungeon.Admin.SetSpot").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(SPOT_NAME) + "'s state to " + SOFT_YELLOW.wrap(SPOT_STATE_ID) + "."));



    public static final MessageLocale DUNGEON_SEND_SENT = LangEntry.builder("Dungeon.Send.Sent").chatMessage(
        GRAY.wrap("Successfully sent " + SOFT_YELLOW.wrap(PLAYER_NAME) + " to the " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " dungeon.")
    );

    public static final MessageLocale DUNGEON_SEND_FAIL = LangEntry.builder("Dungeon.Send.Fail").chatMessage(
        GRAY.wrap("Player " + SOFT_RED.wrap(PLAYER_NAME) + " was unable to join the " + SOFT_RED.wrap(DUNGEON_NAME) + " dungeon.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_INACTIVE = LangEntry.builder("Dungeon.Enter.Error.Inactive").chatMessage(
        SOFT_RED.wrap("Dungeon " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " is not available currently.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_PERMISSION = LangEntry.builder("Dungeon.Enter.Error.Permission").chatMessage(
        GRAY.wrap("You don't have permission to enter the " + SOFT_RED.wrap(DUNGEON_NAME) + " dungeon.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_COST = LangEntry.builder("Dungeon.Enter.Error.Payment").chatMessage(
        GRAY.wrap("You don't have " + SOFT_RED.wrap(DUNGEON_ENTRANCE_PAYMENT) + " to enter the " + SOFT_RED.wrap(DUNGEON_NAME) + " dungeon!")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_LEVEL = LangEntry.builder("Dungeon.Enter.Error.Level").chatMessage(
        GRAY.wrap("Your level must be " + SOFT_RED.wrap(DUNGEON_LEVEL_REQUIREMENT) + " to enter the " + SOFT_RED.wrap(DUNGEON_NAME) + " dungeon!")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_COOLDOWN = LangEntry.builder("Dungeon.Enter.Error.Cooldown").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(DUNGEON_NAME) + " is on cooldown: " + SOFT_RED.wrap(GENERIC_TIME))
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_MAX_PLAYERS = LangEntry.builder("Dungeon.Enter.Error.Maximum").chatMessage(
        GRAY.wrap("Dungeon " + SOFT_RED.wrap(DUNGEON_NAME) + " has maximum players.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_STARTED = LangEntry.builder("Dungeon.Enter.Error.Started").chatMessage(
        GRAY.wrap("Dungeon " + SOFT_RED.wrap(DUNGEON_NAME) + " is already started. You can't join now.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_ENDING = LangEntry.builder("Dungeon.Enter.Error.Ending").chatMessage(
        GRAY.wrap("Dungeon " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " is being reset. Try again in a few moments.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_NO_KIT = LangEntry.builder("Dungeon.Enter.Error.NoKit").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You must select a kit to join the " + SOFT_RED.wrap(DUNGEON_NAME) + " dungeon!")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_NO_KIT_SLOTS = LangEntry.builder("Dungeon.Enter.Error.NoKitSlots").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("There are already maximum players with the " + SOFT_RED.wrap(KIT_NAME) + " kit.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_NO_KIT_PERMISSION = LangEntry.builder("Dungeon.Enter.Error.NoKitPermission").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You're not allowed to use the " + SOFT_RED.wrap(KIT_NAME) + " kit. Upgrade your rank to unlock it.")
    );

    public static final MessageLocale DUNGEON_ENTER_ERROR_KIT_NOT_ALLOWED = LangEntry.builder("Dungeon.Enter.Error.KitNotAllowed").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Kit " + SOFT_RED.wrap(KIT_NAME) + " is not available for the " + SOFT_RED.wrap(DUNGEON_NAME) + " dungeon.")
    );



    public static final MessageLocale DUNGEON_JOIN_LOBBY = LangEntry.builder("Dungeon.Join.Info").titleMessage(
        SOFT_YELLOW.wrap(BOLD.wrap("Dungeon Hub")),
        GRAY.wrap("You entered " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " dungeon hub.")
    );

    public static final MessageLocale DUNGEON_JOIN_NOTIFY = LangEntry.builder("Dungeon.Join.Notify").message(
        MessageData.CHAT_NO_PREFIX,
        GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " joined the dungeon hub.")
    );

    public static final MessageLocale DUNGEON_CONFISACATE_INFO = LangEntry.builder("Dungeon.Confiscate.Info").chatMessage(
        GRAY.wrap("The following items are not allowed to use in this dungeon: " + SOFT_RED.wrap(GENERIC_ITEM))
    );

    public static final MessageLocale DUNGEON_LEAVE_INFO = LangEntry.builder("Dungeon.Leave.Info").chatMessage(
        "You has left the dungeon.");



    public static final MessageLocale DUNGEON_ANNOUNCE_START = LangEntry.builder("Dungeon.Announce.Start").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        "               " + YELLOW.wrap(BOLD.wrap("Dungeon Info")),
        " ",
        "     " + GRAY.wrap("A raid is planned in the " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " dungeon."),
        "        " + GRAY.wrap("Raid will start in " + SOFT_YELLOW.wrap(GENERIC_TIME) + " seconds."),
        " ",
        "           " +
            RUN_COMMAND.with("/" + ALIAS_BASIC + " " + ALIAS_JOIN + " " + DUNGEON_ID).wrap(
                SHOW_TEXT.with(GRAY.wrap("Click to join the " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " dungeon.")).wrap(
                    YELLOW.wrap(BOLD.wrap("CLICK TO JOIN NOW"))
                )
            ),
        " "
    );

    public static final MessageLocale DUNGEON_ANNOUNCE_END = LangEntry.builder("Dungeon.Announce.End").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        "               " + YELLOW.wrap(BOLD.wrap("Dungeon Info")),
        " ",
        "     " + GRAY.wrap("The " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " dungeon raid has been finished."),
        "        " + GRAY.wrap("Dungeon is available for raids now."),
        " ",
        "           " +
            RUN_COMMAND.with("/" + ALIAS_BASIC + " " + ALIAS_JOIN + " " + DUNGEON_ID).wrap(
                SHOW_TEXT.with(GRAY.wrap("Click to join the " + SOFT_YELLOW.wrap(DUNGEON_NAME) + " dungeon.")).wrap(
                    YELLOW.wrap(BOLD.wrap("CLICK TO RAID NOW"))
                )
            ),
        " "
    );

    public static final MessageLocale DUNGEON_GAME_STARTED = LangEntry.builder("Dungeon.Game.Start").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Dungeon Started")),
        GRAY.wrap("Good luck and have fun!"),
        10, 40, Sound.ENTITY_ENDERMAN_TELEPORT
    );

    public static final MessageLocale DUNGEON_GAME_BAD_COMMAND = LangEntry.builder("Dungeon.Game.BadCommand").chatMessage(
        GRAY.wrap("You can't use the " + SOFT_RED.wrap("/" + GENERIC_COMMAND) + " command in the dungeon!")
    );



    public static final MessageLocale DUNGEON_GAME_LEVEL_CHANGED = LangEntry.builder("Dungeon.Game.LevelChanged").titleMessage(
        LEVEL_NAME,
        LEVEL_DESCRIPTION,
        20, 80, Sound.BLOCK_VAULT_ACTIVATE
    );

    public static final MessageLocale DUNGEON_GAME_STAGE_CHANGED = LangEntry.builder("Dungeon.Game.StageChanged").titleMessage(
        STAGE_NAME,
        STAGE_DESCRIPTION,
        20, 80, Sound.BLOCK_VAULT_ACTIVATE
    );

    public static final MessageLocale DUNGEON_GAME_REWARD_RECEIVED = LangEntry.builder("Dungeon.Game.RewardReceived").chatMessage(
        GRAY.wrap("You received reward: " + SHOW_TEXT.with(GRAY.wrap(REWARD_DESCRIPTION)).wrap(SOFT_GREEN.wrap(REWARD_NAME)) + " " + DARK_GRAY.wrap("(hover for details)"))
    );

    public static final MessageLocale DUNGEON_GAME_PLAYER_DIED = LangEntry.builder("Dungeon.Game.PlayerDied").chatMessage(
        GRAY.wrap(RED.wrap(PLAYER_NAME) + " died!")
    );

    public static final MessageLocale DUNGEON_TASK_COMPLETED_INFO = LangEntry.builder("Dungeon.Task.CompletedInfo").chatMessage(
        GRAY.wrap("Task completed: " + SOFT_GREEN.wrap(GENERIC_NAME))
    );

    public static final MessageLocale DUNGEON_TASK_CREATED_GLOBAL = LangEntry.builder("Dungeon.Task.Created.Global").chatMessage(
        GRAY.wrap("Global task received: " + SOFT_GREEN.wrap(GENERIC_NAME + ": " + GENERIC_VALUE))
    );

    public static final MessageLocale DUNGEON_TASK_CREATED_PERSONAL = LangEntry.builder("Dungeon.Task.Created.Personal").chatMessage(
        GRAY.wrap("Personal task received: " + SOFT_GREEN.wrap(GENERIC_NAME + ": " + GENERIC_VALUE))
    );



    public static final MessageLocale DUNGEON_STATUS_LOBBY_WAITING = LangEntry.builder("Dungeon.Status.Lobby.Waiting").actionBarMessage(
        GRAY.wrap("Waiting for more players: " + RED.wrap(GENERIC_CURRENT) + "/" + RED.wrap(GENERIC_MIN))
    );

    public static final MessageLocale DUNGEON_STATUS_LOBBY_READY_FAR = LangEntry.builder("Dungeon.Status.Lobby.ReadyFar").actionBarMessage(
        GRAY.wrap("Start In: " + YELLOW.wrap(GENERIC_TIME))
    );

    public static final MessageLocale DUNGEON_STATUS_LOBBY_READY_CLOSE = LangEntry.builder("Dungeon.Status.Lobby.ReadyClose").actionBarMessage(
        GRAY.wrap("Start In: " + RED.wrap(GENERIC_TIME)),
        Sound.BLOCK_NOTE_BLOCK_BANJO
    );

    public static final MessageLocale DUNGEON_STATUS_ENDING_VICTORY = LangEntry.builder("Dungeon.Status.Ending.Victory").actionBarMessage(
        GREEN.wrap(BOLD.wrap("Dungeon Completed!")) + " " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")")
    );

    public static final MessageLocale DUNGEON_STATUS_ENDING_DEFEAT = LangEntry.builder("Dungeon.Status.Ending.Defeat").actionBarMessage(
        RED.wrap(BOLD.wrap("Dungeon Raid Failed!")) + " " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")")
    );

    public static final MessageLocale DUNGEON_STATUS_DEAD_LIVES = LangEntry.builder("Dungeon.Status.Dead.WithLives").actionBarMessage(
        RED.wrap("You're dead, but you have " + SOFT_YELLOW.wrap(PLAYER_LIVES + "❤") + " lives and may be revived.")
    );

    public static final MessageLocale DUNGEON_STATUS_DEAD_NO_LIVES = LangEntry.builder("Dungeon.Status.Dead.NoLives").actionBarMessage(
        RED.wrap("You're dead, and you have " + SOFT_YELLOW.wrap(PLAYER_LIVES + "❤") + " lives and can't be revived.")
    );



    public static final MessageLocale DUNGEON_DEATH_WITH_LIFES = LangEntry.builder("Dungeon.Death.WithLives").titleMessage(
        ORANGE.wrap(BOLD.wrap("KNOCKED OUT")),
        GRAY.wrap("You have " + ORANGE.wrap(PLAYER_LIVES + "❤") + " extra lives."),
        10, 50, Sound.ENTITY_ZOMBIE_DEATH
    );

    public static final MessageLocale DUNGEON_DEATH_NO_LIFES = LangEntry.builder("Dungeon.Death.NoLives").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("YOU DIED")),
        GRAY.wrap("And you have " + SOFT_RED.wrap("no extra lives") + " left."),
        10, 50, Sound.ENTITY_ZOMBIE_DEATH
    );

    public static final MessageLocale DUNGEON_REVIVE_WITH_LIFES = LangEntry.builder("Dungeon.Revive.WithLives").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("REVIVED")),
        GRAY.wrap("You have " + SOFT_GREEN.wrap(PLAYER_LIVES + "❤") + " extra lives left."),
        10, 50, Sound.ITEM_TOTEM_USE
    );

    public static final MessageLocale DUNGEON_REVIVE_NO_LIFES = LangEntry.builder("Dungeon.Revive.NoLives").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("REVIVED")),
        GRAY.wrap("You have " + SOFT_RED.wrap("no extra lives") + " left."),
        10, 50, Sound.ITEM_TOTEM_USE
    );



    public static final MessageLocale DUNGEON_END_ALL_DEAD = LangEntry.builder("Dungeon.Ending.AllDead").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Defeat")),
        GRAY.wrap("All players died."),
        10, 80, Sound.ENTITY_BLAZE_DEATH
    );

    public static final MessageLocale DUNGEON_END_TIMEOUT = LangEntry.builder("Dungeon.Ending.Timeout").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Time Out")),
        GRAY.wrap("The time has expired."),
        10, 80, Sound.ENTITY_BLAZE_DEATH
    );

    public static final MessageLocale DUNGEON_END_DEFEAT = LangEntry.builder("Dungeon.Ending.Defeat").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Defeat")),
        GRAY.wrap("You failed the " + SOFT_RED.wrap(DUNGEON_NAME) + " dungeon."),
        10, 80, Sound.ENTITY_PLAYER_LEVELUP
    );

    public static final MessageLocale DUNGEON_END_COMPLETED = LangEntry.builder("Dungeon.Ending.Completed").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Completed")),
        GRAY.wrap("You completed the " + SOFT_GREEN.wrap(DUNGEON_NAME) + " dungeon."),
        10, 80, Sound.ENTITY_PLAYER_LEVELUP
    );



    public static final MessageLocale KIT_CREATE_DONE_NEW = LangEntry.builder("Kit.Create.Done.New").chatMessage(
        GRAY.wrap("Created " + SOFT_GREEN.wrap(KIT_NAME) + " kit."));

    public static final MessageLocale KIT_CREATE_DONE_UPDATE = LangEntry.builder("Kit.Create.Done.Update").chatMessage(
        GRAY.wrap("Updated " + SOFT_GREEN.wrap(KIT_NAME) + " kit."));

    public static final MessageLocale KIT_GRANT_DONE = LangEntry.builder("Kit.Grant.Done").chatMessage(
        GRAY.wrap("Granted " + GREEN.wrap(KIT_NAME) + " kit access to " + GREEN.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale KIT_REVOKE_DONE = LangEntry.builder("Kit.Revoke.Done").chatMessage(
        GRAY.wrap("Revoked " + RED.wrap(KIT_NAME) + " kit access from " + RED.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale KIT_BUY_ERROR_INSUFFICIENT_FUNDS = LangEntry.builder("Kit.Buy.Error.InsufficientFunds").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can't afford " + SOFT_RED.wrap(KIT_NAME) + " kit for " + SOFT_RED.wrap(KIT_COST))
    );

    public static final MessageLocale KIT_BUY_SUCCESS = LangEntry.builder("Kit.Buy.Success").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("You purchased " + SOFT_GREEN.wrap(KIT_NAME) + " kit for " + SOFT_GREEN.wrap(KIT_COST))
    );



    public static final MessageLocale SELECTION_INFO_CUBOID = LangEntry.builder("Selection.Info.Cuboid").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap("#" + GENERIC_VALUE) + " position.")
    );

    public static final MessageLocale SELECTION_INFO_POSITION_ADD = LangEntry.builder("Selection.Info.PositionAdd").chatMessage(
        GRAY.wrap("Added block position.")
    );

    public static final MessageLocale SELECTION_INFO_POSITION_REMOVE = LangEntry.builder("Selection.Info.PositionRemove").chatMessage(
        GRAY.wrap("Removed block position.")
    );



    public static final TextLocale UI_TASK_EMPTY_LIST = LangEntry.builder("UI.Task.EmptyList").text(
        SOFT_RED.wrap("✘ No active tasks."));

    public static final TextLocale UI_TASK_COMPLETED = LangEntry.builder("UI.Task.Completed").text(
        GREEN.wrap("✔") + " " + DARK_GRAY.wrap(STRIKETHROUGH.wrap(GENERIC_NAME + ": " + GENERIC_VALUE)));

    public static final TextLocale UI_TASK_INCOMPLETED = LangEntry.builder("UI.Task.Incompleted").text(
        RED.wrap("✘") + " " + GRAY.wrap(GENERIC_NAME + ": " + GENERIC_VALUE));

    public static final TextLocale UI_BOARD_PLAYER_READY = LangEntry.builder("UI.Board.Player.Ready").text(
        GRAY.wrap(PLAYER_DISPLAY_NAME) + " " + GREEN.wrap("✔"));

    public static final TextLocale UI_BOARD_PLAYER_NOT_READY = LangEntry.builder("UI.Board.Player.NotReady").text(
        GRAY.wrap(PLAYER_DISPLAY_NAME) + " " + RED.wrap("✘"));

    public static final IconLocale UI_CONFIRMATION_DUNGEON_ENTER_NO_KITS = LangEntry.iconBuilder("UI.Confirmation.Dungeon.Enter.NoKits")
        .name("Dungeon Info")
        .appendInfo("Enter Cost: " + RED.wrap(DUNGEON_ENTRANCE_PAYMENT))
        .build();

    public static final IconLocale UI_CONFIRMATION_DUNGEON_ENTER_OWN_KIT = LangEntry.iconBuilder("UI.Confirmation.Dungeon.Enter.OwnedKit")
        .name("Dungeon Info")
        .appendInfo("Your Kit: " + WHITE.wrap(KIT_NAME), "Enter Cost: " + RED.wrap(DUNGEON_ENTRANCE_PAYMENT), "", KIT_ATTRIBUTES, KIT_EFFECTS)
        .build();

    public static final IconLocale UI_CONFIRMATION_DUNGEON_ENTER_RENT_KIT = LangEntry.iconBuilder("UI.Confirmation.Dungeon.Enter.RentedKit")
        .name("Dungeon Info")
        .appendInfo(
            "Your Kit: " + WHITE.wrap(KIT_NAME),
            "Kit Cost: " + RED.wrap(KIT_COST),
            "Enter Cost: " + RED.wrap(DUNGEON_ENTRANCE_PAYMENT),
            "",
            KIT_ATTRIBUTES,
            KIT_EFFECTS
        )
        .build();

    public static final IconLocale UI_CONFIRMATION_KIT_PURCHASE = LangEntry.iconBuilder("UI.Confirmation.Kit.Purchase")
        .name("Kit Info")
        .appendInfo("Kit: " + WHITE.wrap(KIT_NAME), "Cost: " + RED.wrap(KIT_COST), "", KIT_ATTRIBUTES, KIT_EFFECTS)
        .build();

    public static final TextLocale UI_KIT_NO_ATTRIBUTES = LangEntry.builder("UI.Kit.NoAttributes").text(SOFT_RED.wrap("✘ No attribute modifiers."));
    public static final TextLocale UI_KIT_NO_EFFECTS    = LangEntry.builder("UI.Kit.NoEffects").text(SOFT_RED.wrap("✘ No potion effects."));

    public static final TextLocale UI_LEVEL_MIN_ONLY = LangEntry.builder("UI.LevelRequirement.MinOnly").text(SOFT_RED.wrap(GENERIC_MIN + "+"));
    public static final TextLocale UI_LEVEL_MAX_ONLY = LangEntry.builder("UI.LevelRequirement.MaxOnly").text(SOFT_RED.wrap("<= " + GENERIC_MAX));
    public static final TextLocale UI_LEVEL_RANGE    = LangEntry.builder("UI.LevelRequirement.Range").text(SOFT_RED.wrap(GENERIC_MIN + " - " + GENERIC_MAX));

    public static final TextLocale UI_POTION_EFFECT_ENTRY = LangEntry.builder("UI.PotionEffect.Entry").text(SOFT_YELLOW.wrap("● " + GRAY.wrap(GENERIC_NAME) + " " + GENERIC_AMOUNT));

    public static final TextLocale UI_ATTRIBUTE_ENTRY           = LangEntry.builder("UI.Attribute.Entry").text(SOFT_YELLOW.wrap("● " + GRAY.wrap(GENERIC_NAME) + " " + GENERIC_AMOUNT));
    public static final TextLocale UI_ATTRIBUTE_POSITIVE_PLAIN  = LangEntry.builder("UI.Attribute.Positive.Plain").text(GREEN.wrap("+" + GENERIC_VALUE));
    public static final TextLocale UI_ATTRIBUTE_POSITIVE_SCALAR = LangEntry.builder("UI.Attribute.Positive.Scalar").text(GREEN.wrap("+" + GENERIC_VALUE + "%"));
    public static final TextLocale UI_ATTRIBUTE_NEGATIVE_PLAIN  = LangEntry.builder("UI.Attribute.Negative.Plain").text(RED.wrap(GENERIC_VALUE));
    public static final TextLocale UI_ATTRIBUTE_NEGATIVE_SCALAR = LangEntry.builder("UI.Attribute.Negative.Scalar").text(RED.wrap(GENERIC_VALUE + "%"));

    public static final TextLocale OTHER_FREE = LangEntry.builder("Other.Free").text("Free");
}
