package su.nightexpress.dungeons.config;

import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.selection.SelectionType;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.*;
import su.nightexpress.nightcore.language.message.OutputType;

import static su.nightexpress.dungeons.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.OUTPUT;
import static su.nightexpress.nightcore.language.tag.MessageTags.SOUND;
import static su.nightexpress.nightcore.util.Placeholders.GENERIC_VALUE;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Lang extends CoreLang {

    public static final LangKeyed<Attribute> ATTRIBUTE = LangKeyed.of("Attribute", Registry.ATTRIBUTE);
    public static final LangEnum<GameState> GAME_STATE = LangEnum.of("GameState", GameState.class);

    public static final LangString COMMAND_ARGUMENT_NAME_DUNGEON    = LangString.of("Command.Argument.Name.Dungeon", "dungeon");
    public static final LangString COMMAND_ARGUMENT_NAME_STAGE      = LangString.of("Command.Argument.Name.Dungeon", "stage");
    public static final LangString COMMAND_ARGUMENT_NAME_LEVEL      = LangString.of("Command.Argument.Name.Dungeon", "level");
    public static final LangString COMMAND_ARGUMENT_NAME_SPOT       = LangString.of("Command.Argument.Name.Spot", "spot");
    public static final LangString COMMAND_ARGUMENT_NAME_REWARD     = LangString.of("Command.Argument.Name.Reward", "reward");
    public static final LangString COMMAND_ARGUMENT_NAME_STATE      = LangString.of("Command.Argument.Name.State", "state");
    public static final LangString COMMAND_ARGUMENT_NAME_KIT        = LangString.of("Command.Argument.Name.Kit", "kit");
    public static final LangString COMMAND_ARGUMENT_NAME_LOOT_CHEST = LangString.of("Command.Argument.Name.LootChest", "lootChest");
    public static final LangString COMMAND_ARGUMENT_NAME_WEIGHT     = LangString.of("Command.Argument.Name.Weight", "weight");

    public static final LangString COMMAND_BROWSE_DESC = LangString.of("Command.Browse.Desc", "Browse the dungeons.");
    public static final LangString COMMAND_JOIN_DESC   = LangString.of("Command.Join.Desc", "Enter the dungeon.");
    public static final LangString COMMAND_LEAVE_DESC  = LangString.of("Command.Leave.Desc", "Leave the dungeon.");
    public static final LangString COMMAND_WAND_DESC   = LangString.of("Command.Wand.Desc", "Get selection tool.");
    public static final LangString COMMAND_CREATE_DESC = LangString.of("Command.Create.Desc", "Create new dungeon from selection.");

    public static final LangString COMMAND_SET_PROTECTION_DESC  = LangString.of("Command.SetProtection.Desc", "Update dungeon's protection area.");
    public static final LangString COMMAND_SET_LOBBY_DESC       = LangString.of("Command.SetLobby.Desc", "Set dungeon's lobby position.");

    public static final LangString COMMAND_SET_STAGE_DESC       = LangString.of("Command.SetStage.Desc", "Set dungeon's game stage.");
    public static final LangString COMMAND_SET_LEVEL_DESC       = LangString.of("Command.SetLevel.Desc", "Set dungeon's game level.");

    public static final LangString COMMAND_SPAWNER_DESC        = LangString.of("Command.Spawner.Desc", "Spawner commands.");
    public static final LangString COMMAND_SPAWNER_CREATE_DESC = LangString.of("Command.Spawner.Create.Desc", "Create a new spawner.");

    public static final LangString COMMAND_LEVEL_DESC           = LangString.of("Command.Level.Desc", "Level commands.");
    public static final LangString COMMAND_LEVEL_CREATE_DESC    = LangString.of("Command.Level.Create.Desc", "Create empty level config.");
    public static final LangString COMMAND_LEVEL_SET_SPAWN_DESC = LangString.of("Command.Level.SetSpawn.Desc", "Set level's spawn.");

    public static final LangString COMMAND_STAGE_DESC        = LangString.of("Command.Stage.Desc", "Stage commands.");
    public static final LangString COMMAND_STAGE_CREATE_DESC = LangString.of("Command.Stage.Create.Desc", "Create empty stage config.");

    public static final LangString COMMAND_SPOT_DESC           = LangString.of("Command.Spot.Desc", "Spot commands.");
    public static final LangString COMMAND_SPOT_CREATE_DESC    = LangString.of("Command.Spot.Create.Desc", "Create a new spot from selection.");
    public static final LangString COMMAND_SPOT_REMOVE_DESC    = LangString.of("Command.Spot.Remove.Desc", "Delete a spot.");
    public static final LangString COMMAND_SPOT_ADD_STATE_DESC = LangString.of("Command.Spot.AddState.Desc", "Add a spot state from selection.");

    public static final LangString COMMAND_KIT_DESC           = LangString.of("Command.Kit.Desc", "Kit Commands.");
    public static final LangString COMMAND_KIT_CREATE_DESC    = LangString.of("Command.Kit.Create.Desc", "Create a new kit.");
    public static final LangString COMMAND_KIT_SET_ITEMS_DESC = LangString.of("Command.Kit.SetItems.Desc", "Update kit with current inventory.");
    public static final LangString COMMAND_KIT_GRANT_DESC     = LangString.of("Command.Kit.Grant.Desc", "Grant kit access to a player.");
    public static final LangString COMMAND_KIT_REVOKE_DESC    = LangString.of("Command.Kit.Revoke.Desc", "Revoke kit access from a player.");

    public static final LangString COMMAND_REWARD_DESC          = LangString.of("Command.Reward.Desc", "Reward commands.");
    public static final LangString COMMAND_REWARD_CREATE_DESC   = LangString.of("Command.Reward.Create.Desc", "Create a new reward.");
    public static final LangString COMMAND_REWARD_REMOVE_DESC   = LangString.of("Command.Reward.Remove.Desc", "Delete a reward.");
    public static final LangString COMMAND_REWARD_ADD_ITEM_DESC = LangString.of("Command.Reward.AddItem.Desc", "Add item to the reward.");

    public static final LangString COMMAND_LOOT_CHEST_DESC          = LangString.of("Command.LootChest.Desc", "Loot Chest commands.");
    public static final LangString COMMAND_LOOT_CHEST_CREATE_DESC   = LangString.of("Command.LootChest.Create.Desc", "Create a new loot chest.");
    public static final LangString COMMAND_LOOT_CHEST_REMOVE_DESC   = LangString.of("Command.LootChest.Remove.Desc", "Delete a loot chest.");
    public static final LangString COMMAND_LOOT_CHEST_ADD_ITEM_DESC = LangString.of("Command.LootChest.AddItem.Desc", "Add item to the loot chest.");

    public static final LangString COMMAND_STOP_DESC  = LangString.of("Command.Stop.Desc", "Force stop the dungeon.");
    public static final LangString COMMAND_START_DESC = LangString.of("Command.Start.Desc", "Force start the dungeon.");

    public static final LangText ERROR_COMMAND_INVALID_SELECTION_ARGUMENT = LangText.of("Error.Command.Argument.InvalidSelectionType",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid type!"));

    public static final LangText ERROR_COMMAND_INVALID_DUNGEON_ARGUMENT = LangText.of("Error.Command.InvalidArgument.Dungeon",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid dungeon!"));

    public static final LangText ERROR_COMMAND_INVALID_KIT_ARGUMENT = LangText.of("Error.Command.InvalidArgument.Kit",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid kit!"));

    public static final LangText ERROR_COMMAND_INVALID_STAGE_ARGUMENT = LangText.of("Error.Command.InvalidArgument.Stage",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid dungeon stage!"));

    public static final LangText ERROR_COMMAND_INVALID_LEVEL_ARGUMENT = LangText.of("Error.Command.InvalidArgument.Level",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid dungeon level!"));



    public static final LangText SETUP_GENERIC_NO_ITEM = LangText.of("Setup.Generic.NoItem",
        LIGHT_RED.wrap("You must hold an item in main hand."));

    public static final LangText SETUP_GENERIC_BAD_ITEM = LangText.of("Setup.Generic.BadItem",
        LIGHT_RED.wrap("Unable to get data of the item you hold."));



    public static final LangText SETUP_DUNGEON_EXISTS = LangText.of("Setup.Dungeon.AlreadyExists",
        LIGHT_RED.wrap("Dungeon with such ID already exists."));

    public static final LangText SETUP_DUNGEON_CREATED = LangText.of("Setup.Dungeon.Created",
        GRAY.wrap("Dungeon created: " + GREEN.wrap(DUNGEON_NAME) + "!"));



    public static final LangText SETUP_PROTECTION_SET = LangText.of("Setup.Protection.Set",
        GRAY.wrap("Updated protection area for the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));

    public static final LangText SETUP_LOBBY_SET = LangText.of("Setup.Lobby.Set",
        GRAY.wrap("Set lobby position for the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));



    public static final LangText SETUP_SPAWNER_CREATED = LangText.of("Setup.Spawner.Created",
        GRAY.wrap("Spawner created: " + GREEN.wrap(GENERIC_NAME) + "!"));



    public static final LangText SETUP_LEVEL_EXISTS = LangText.of("Setup.Level.AlreadyExists",
        LIGHT_RED.wrap("Level with such ID already exists."));

    public static final LangText SETUP_LEVEL_INVALID = LangText.of("Setup.Level.Invalid",
        LIGHT_RED.wrap("Invalid level!"));

    public static final LangText SETUP_LEVEL_CREATED = LangText.of("Setup.Level.Created",
        GRAY.wrap("Level created: " + GREEN.wrap(LEVEL_NAME) + "!"));

    public static final LangText SETUP_LEVEL_SPAWN_SET = LangText.of("Setup.Level.SpawnSet",
        GRAY.wrap("Set level spawn: " + GREEN.wrap(LEVEL_NAME) + "!"));



    public static final LangText SETUP_STAGE_EXISTS = LangText.of("Setup.Stage.AlreadyExists",
        LIGHT_RED.wrap("Stage with such ID already exists."));

    public static final LangText SETUP_STAGE_CREATED = LangText.of("Setup.Stage.Created",
        GRAY.wrap("Stage created: " + GREEN.wrap(STAGE_NAME) + "!"));



    public static final LangText SETUP_REWARD_EXISTS = LangText.of("Setup.Reward.AlreadyExists",
        LIGHT_RED.wrap("Reward with such ID already exists."));

    public static final LangText SETUP_REWARD_INVALID = LangText.of("Setup.Reward.Invalid",
        LIGHT_RED.wrap("Invalid reward!"));

    public static final LangText SETUP_REWARD_CREATED = LangText.of("Setup.Reward.Created",
        GRAY.wrap("Reward created: " + GREEN.wrap(REWARD_NAME) + "!"));

    public static final LangText SETUP_REWARD_REMOVED = LangText.of("Setup.Reward.Removed",
        GRAY.wrap("Reward removed: " + RED.wrap(REWARD_NAME) + "!"));

    public static final LangText SETUP_REWARD_ITEM_ADDED = LangText.of("Setup.Reward.ItemAdded",
        GRAY.wrap("Added " + GREEN.wrap(GENERIC_NAME) + " to the " + GREEN.wrap(REWARD_NAME) + " reward!"));



    public static final LangText SETUP_LOOT_CHEST_EXISTS = LangText.of("Setup.LootChest.AlreadyExists",
        LIGHT_RED.wrap("Loot Chest with such ID already exists."));

    public static final LangText SETUP_LOOT_CHEST_INVALID = LangText.of("Setup.LootChest.Invalid",
        LIGHT_RED.wrap("Invalid loot chest!"));

    public static final LangText SETUP_LOOT_CHEST_NOT_CONTAINER = LangText.of("Setup.LootChest.NotContainer",
        LIGHT_RED.wrap("The loot chest block must be a container!"));

    public static final LangText SETUP_LOOT_CHEST_CREATED = LangText.of("Setup.LootChest.Created",
        GRAY.wrap("Loot Chest created: " + GREEN.wrap(LOOT_CHEST_ID) + "!"));

    public static final LangText SETUP_LOOT_CHEST_REMOVED = LangText.of("Setup.LootChest.Removed",
        GRAY.wrap("Loot Chest removed: " + RED.wrap(LOOT_CHEST_ID) + "!"));

    public static final LangText SETUP_LOOT_CHEST_ITEM_ADDED = LangText.of("Setup.LootChest.ItemAdded",
        GRAY.wrap("Added " + GREEN.wrap(GENERIC_NAME) + " to the " + GREEN.wrap(LOOT_CHEST_ID) + " loot chest!"));



    public static final LangText SETUP_SPOT_EXISTS = LangText.of("Setup.Spot.AlreadyExists",
        LIGHT_RED.wrap("Spot with such ID already exists."));

    public static final LangText SETUP_SPOT_INVALID = LangText.of("Setup.Spot.Invalid",
        LIGHT_RED.wrap("Invalid spot!"));

    public static final LangText SETUP_SPOT_CREATED = LangText.of("Setup.Spot.Created",
        GRAY.wrap("Spot created: " + GREEN.wrap(SPOT_NAME) + "!"));

    public static final LangText SETUP_SPOT_REMOVED = LangText.of("Setup.Spot.Removed",
        GRAY.wrap("Spot removed: " + RED.wrap(SPOT_NAME) + "!"));

    public static final LangText SETUP_SPOT_STATE_ADDED = LangText.of("Setup.Spot.StateCreated",
        GRAY.wrap("Spot state created: " + LIGHT_GREEN.wrap(SPOT_STATE_ID) + "!"));



    public static final LangText SETUP_ERROR_INVALID_NAME = LangText.of("Setup.Error.InvalidName",
        LIGHT_RED.wrap("Invalid ID. Only latin letters and numbers are allowed."));



    public static final LangText SETUP_SELECTION_ACTIVATED = LangText.of("Setup.Selection.Activated",
        GRAY.wrap("Selection mode " + GREEN.wrap("activated") + "."));

    public static final LangText SETUP_SELECTION_NO_CUBOID = LangText.of("Setup.Selection.NoCuboid",
        LIGHT_RED.wrap("You must select cuboid area first: " + LIGHT_YELLOW.wrap("/" + ALIAS_BASIC + " " + ALIAS_WAND + " " + SelectionType.CUBOID.name().toLowerCase())));

    public static final LangText SETUP_SELECTION_NO_POSITIONS = LangText.of("Setup.Selection.NoPositions",
        LIGHT_RED.wrap("You must select block positions first: " + LIGHT_YELLOW.wrap("/" + ALIAS_BASIC + " " + ALIAS_WAND + " " + SelectionType.POSITION.name().toLowerCase())));

    public static final LangText SETUP_SELECTION_CUBOID_OUT_OF_PROTECTION = LangText.of("Setup.Selection.CuboidOutOfProtection",
        LIGHT_RED.wrap("Selected cuboid must be inside the " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + "'s protection area."));

    public static final LangText SETUP_SELECTION_POSITION_OUT_OF_PROTECTION = LangText.of("Setup.Selection.PositionOutOfProtection",
        LIGHT_RED.wrap("Position must be inside the " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + "'s protection area."));

    public static final LangText SETUP_SELECTION_DUNGEON_OVERLAP = LangText.of("Setup.Selection.DungeonOverlap",
        LIGHT_RED.wrap("Selected area overlaps with other dungeon(s)!"));



    public static final LangText DUNGEON_ERROR_MUST_BE_IN = LangText.of("Dungeon.Error.MustBeIn",
        LIGHT_RED.wrap("You're not in a dungeon!"));

    public static final LangText DUNGEON_ERROR_MUST_BE_OUT = LangText.of("Dungeon.Error.MustBeOut",
        LIGHT_RED.wrap("You can't do that in dungeon!"));

    public static final LangText DUNGEON_ERROR_NOT_IN_GAME = LangText.of("Dungeon.ForceEnd.NotInGame",
        LIGHT_GRAY.wrap("The " + RED.wrap(DUNGEON_NAME) + " dungeon is not in active game state."));

    public static final LangText DUNGEON_ERROR_NOT_READY_TO_GAME = LangText.of("Dungeon.Error.NotReadyToGame",
        LIGHT_GRAY.wrap("The " + RED.wrap(DUNGEON_NAME) + " dungeon is not ready to start the game."));



    public static final LangText DUNGEON_START_DONE = LangText.of("Dungeon.Admin.Start",
        GRAY.wrap("Started the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));

    public static final LangText DUNGEON_ADMIN_STOP = LangText.of("Dungeon.Admin.Stop",
        GRAY.wrap("Stopped the " + GREEN.wrap(DUNGEON_NAME) + " dungeon."));

    public static final LangText DUNGEON_ADMIN_SET_LEVEL = LangText.of("Dungeon.Admin.SetLevel",
        GRAY.wrap("Set " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + "'s level to " + LIGHT_YELLOW.wrap(STAGE_NAME) + "."));

    public static final LangText DUNGEON_ADMIN_SET_STAGE = LangText.of("Dungeon.Admin.SetStage",
        GRAY.wrap("Set " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + "'s stage to " + LIGHT_YELLOW.wrap(STAGE_NAME) + "."));



    public static final LangText DUNGEON_ENTER_ERROR_INACTIVE = LangText.of("Dungeon.Enter.Error.Inactive",
        LIGHT_RED.wrap("Dungeon " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " is not available currently.")
    );

    public static final LangText DUNGEON_ENTER_ERROR_PERMISSION = LangText.of("Dungeon.Enter.Error.Permission",
        LIGHT_GRAY.wrap("You don't have permission to enter the " + LIGHT_RED.wrap(DUNGEON_NAME) + " dungeon.")
    );

    public static final LangText DUNGEON_ENTER_ERROR_COST = LangText.of("Dungeon.Enter.Error.Payment",
        LIGHT_GRAY.wrap("You don't have " + LIGHT_RED.wrap(DUNGEON_ENTRANCE_PAYMENT) + " to enter the " + LIGHT_RED.wrap(DUNGEON_NAME) + " dungeon!")
    );

    public static final LangText DUNGEON_ENTER_ERROR_LEVEL = LangText.of("Dungeon.Enter.Error.Level",
        LIGHT_GRAY.wrap("Your level must be " + LIGHT_RED.wrap(DUNGEON_LEVEL_REQUIREMENT) + " to enter the " + LIGHT_RED.wrap(DUNGEON_NAME) + " dungeon!")
    );

    public static final LangText DUNGEON_ENTER_ERROR_COOLDOWN = LangText.of("Dungeon.Enter.Error.Cooldown",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(DUNGEON_NAME) + " is on cooldown: " + LIGHT_RED.wrap(GENERIC_TIME))
    );

    public static final LangText DUNGEON_ENTER_ERROR_MAX_PLAYERS = LangText.of("Dungeon.Enter.Error.Maximum",
        LIGHT_GRAY.wrap("Dungeon " + LIGHT_RED.wrap(DUNGEON_NAME) + " has maximum players.")
    );

    public static final LangText DUNGEON_ENTER_ERROR_STARTED = LangText.of("Dungeon.Enter.Error.Started",
        LIGHT_GRAY.wrap("Dungeon " + LIGHT_RED.wrap(DUNGEON_NAME) + " is already started. You can't join now.")
    );

    public static final LangText DUNGEON_ENTER_ERROR_ENDING = LangText.of("Dungeon.Enter.Error.Ending",
        LIGHT_GRAY.wrap("Dungeon " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " is being reset. Try again in a few moments.")
    );

    public static final LangText DUNGEON_ENTER_ERROR_NO_KIT = LangText.of("Dungeon.Enter.Error.NoKit",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You must select a kit to join the " + LIGHT_RED.wrap(DUNGEON_NAME) + " dungeon!")
    );

    public static final LangText DUNGEON_ENTER_ERROR_NO_KIT_SLOTS = LangText.of("Dungeon.Enter.Error.NoKitSlots",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("There are already maximum players with the " + LIGHT_RED.wrap(KIT_NAME) + " kit.")
    );

    public static final LangText DUNGEON_ENTER_ERROR_NO_KIT_PERMISSION = LangText.of("Dungeon.Enter.Error.NoKitPermission",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You're not allowed to use the " + LIGHT_RED.wrap(KIT_NAME) + " kit. Upgrade your rank to unlock it.")
    );

    public static final LangText DUNGEON_ENTER_ERROR_KIT_NOT_ALLOWED = LangText.of("Dungeon.Enter.Error.KitNotAllowed",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("Kit " + LIGHT_RED.wrap(KIT_NAME) + " is not available for the " + LIGHT_RED.wrap(DUNGEON_NAME) + " dungeon.")
    );



    public static final LangText DUNGEON_JOIN_LOBBY = LangText.of("Dungeon.Join.Info",
        OUTPUT.wrap(20, 60),
        LIGHT_YELLOW.wrap(BOLD.wrap("Dungeon Hub")),
        LIGHT_GRAY.wrap("You entered " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " dungeon hub.")
    );

    public static final LangText DUNGEON_JOIN_NOTIFY = LangText.of("Dungeon.Join.Notify",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " joined the dungeon hub.")
    );

    public static final LangText DUNGEON_CONFISACATE_INFO = LangText.of("Dungeon.Confiscate.Info",
        LIGHT_GRAY.wrap("The following items are not allowed to use in this dungeon: " + GENERIC_ITEM)
    );

    public static final LangText DUNGEON_LEAVE_INFO = LangText.of("Dungeon.Leave.Info",
        "You has left the dungeon.");



    public static final LangText DUNGEON_ANNOUNCE_START = LangText.of("Dungeon.Announce.Start",
        TAG_NO_PREFIX,
        " ",
        "               " + YELLOW.wrap(BOLD.wrap("Dungeon Info")),
        " ",
        "     " + LIGHT_GRAY.wrap("A raid is planned in the " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " dungeon."),
        "        " + LIGHT_GRAY.wrap("Raid will start in " + LIGHT_YELLOW.wrap(GENERIC_TIME) + " seconds."),
        " ",
        "           " +
            CLICK.wrapRunCommand(
                HOVER.wrapShowText(
                    YELLOW.wrap(BOLD.wrap("CLICK TO JOIN NOW")), GRAY.wrap("Click to join the " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " dungeon.")
                ), "/" + ALIAS_BASIC + " " + ALIAS_JOIN + " " + DUNGEON_ID
            ),
        " "
    );

    public static final LangText DUNGEON_ANNOUNCE_END = LangText.of("Dungeon.Announce.End",
        TAG_NO_PREFIX,
        " ",
        "               " + YELLOW.wrap(BOLD.wrap("Dungeon Info")),
        " ",
        "     " + LIGHT_GRAY.wrap("The " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " dungeon raid has been finished."),
        "        " + LIGHT_GRAY.wrap("Dungeon is available for raids now."),
        " ",
        "           " +
            CLICK.wrapRunCommand(
                HOVER.wrapShowText(
                    YELLOW.wrap(BOLD.wrap("CLICK TO RAID NOW")), GRAY.wrap("Click to join the " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " dungeon.")
                ), "/" + ALIAS_BASIC + " " + ALIAS_JOIN + " " + DUNGEON_ID
            ),
        " "
    );

    public static final LangText DUNGEON_GAME_STARTED = LangText.of("Dungeon.Game.Start",
        OUTPUT.wrap(10, 40) + SOUND.wrap(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GREEN.wrap(BOLD.wrap("Dungeon Started")),
        LIGHT_GRAY.wrap("Good luck and have fun!")
    );

    public static final LangText DUNGEON_GAME_BAD_COMMAND = LangText.of("Dungeon.Game.BadCommand",
        LIGHT_GRAY.wrap("You can't use the " + LIGHT_RED.wrap("/" + GENERIC_COMMAND) + " command in the dungeon!")
    );



    public static final LangText DUNGEON_GAME_LEVEL_CHANGED = LangText.of("Dungeon.Game.LevelChanged",
        OUTPUT.wrap(20, 80) + SOUND.wrap(Sound.BLOCK_VAULT_ACTIVATE),
        LEVEL_NAME,
        LEVEL_DESCRIPTION
    );

    public static final LangText DUNGEON_GAME_STAGE_CHANGED = LangText.of("Dungeon.Game.StageChanged",
        OUTPUT.wrap(20, 80) + SOUND.wrap(Sound.BLOCK_VAULT_ACTIVATE),
        STAGE_NAME,
        STAGE_DESCRIPTION
    );

    public static final LangText DUNGEON_GAME_REWARD_RECEIVED = LangText.of("Dungeon.Game.RewardReceived",
        GRAY.wrap("You received reward: " + HOVER.wrapShowText(LIGHT_GREEN.wrap(REWARD_NAME), GRAY.wrap(REWARD_DESCRIPTION)) + " " + DARK_GRAY.wrap("(hover for details)"))
    );

    public static final LangText DUNGEON_GAME_PLAYER_DIED = LangText.of("Dungeon.Game.PlayerDied",
        GRAY.wrap(RED.wrap(PLAYER_NAME) + " died!")
    );

    public static final LangText DUNGEON_TASK_COMPLETED_INFO = LangText.of("Dungeon.Task.CompletedInfo",
        GRAY.wrap("Task completed: " + LIGHT_GREEN.wrap(GENERIC_NAME))
    );

    public static final LangText DUNGEON_TASK_CREATED_GLOBAL = LangText.of("Dungeon.Task.Created.Global",
        GRAY.wrap("Global task received: " + LIGHT_GREEN.wrap(GENERIC_NAME + ": " + GENERIC_VALUE))
    );

    public static final LangText DUNGEON_TASK_CREATED_PERSONAL = LangText.of("Dungeon.Task.Created.Personal",
        GRAY.wrap("Personal task received: " + LIGHT_GREEN.wrap(GENERIC_NAME + ": " + GENERIC_VALUE))
    );



    public static final LangText DUNGEON_STATUS_LOBBY_WAITING = LangText.of("Dungeon.Status.Lobby.Waiting",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        GRAY.wrap("Waiting for more players: " + RED.wrap(GENERIC_CURRENT) + "/" + RED.wrap(GENERIC_MIN))
    );

    public static final LangText DUNGEON_STATUS_LOBBY_READY_FAR = LangText.of("Dungeon.Status.Lobby.ReadyFar",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        GRAY.wrap("Start In: " + YELLOW.wrap(GENERIC_TIME))
    );

    public static final LangText DUNGEON_STATUS_LOBBY_READY_CLOSE = LangText.of("Dungeon.Status.Lobby.ReadyClose",
        OUTPUT.wrap(OutputType.ACTION_BAR) + SOUND.wrap(Sound.BLOCK_NOTE_BLOCK_BANJO),
        GRAY.wrap("Start In: " + RED.wrap(GENERIC_TIME))
    );

    public static final LangText DUNGEON_STATUS_ENDING_VICTORY = LangText.of("Dungeon.Status.Ending.Victory",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        GREEN.wrap(BOLD.wrap("Dungeon Completed!")) + " " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")")
    );

    public static final LangText DUNGEON_STATUS_ENDING_DEFEAT = LangText.of("Dungeon.Status.Ending.Defeat",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        RED.wrap(BOLD.wrap("Dungeon Defeated!")) + " " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")")
    );

    public static final LangText DUNGEON_STATUS_DEAD_LIVES = LangText.of("Dungeon.Status.Dead.WithLives",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        RED.wrap("You're dead, but you have " + LIGHT_YELLOW.wrap(PLAYER_LIVES + "❤") + " lives and may be revived.")
    );

    public static final LangText DUNGEON_STATUS_DEAD_NO_LIVES = LangText.of("Dungeon.Status.Dead.NoLives",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        RED.wrap("You're dead, and you have " + LIGHT_YELLOW.wrap(PLAYER_LIVES + "❤") + " lives and can't be revived.")
    );



    public static final LangText DUNGEON_DEATH_WITH_LIFES = LangText.of("Dungeon.Death.WithLifes",
        OUTPUT.wrap(10, 50) + SOUND.wrap(Sound.ENTITY_ZOMBIE_DEATH),
        LIGHT_ORANGE.wrap(BOLD.wrap("KNOCKED OUT")),
        LIGHT_GRAY.wrap("You have " + LIGHT_ORANGE.wrap(PLAYER_LIVES + "❤") + " extra lifes.")
    );

    public static final LangText DUNGEON_DEATH_NO_LIFES = LangText.of("Dungeon.Death.NoLifes",
        OUTPUT.wrap(10, 50) + SOUND.wrap(Sound.ENTITY_ZOMBIE_DEATH),
        LIGHT_RED.wrap(BOLD.wrap("YOU DIED")),
        LIGHT_GRAY.wrap("And you have " + LIGHT_RED.wrap("no extra lifes") + " left.")
    );

    public static final LangText DUNGEON_REVIVE_WITH_LIFES = LangText.of("Dungeon.Revive.WithLifes",
        OUTPUT.wrap(10, 50) + SOUND.wrap(Sound.ITEM_TOTEM_USE),
        LIGHT_GREEN.wrap(BOLD.wrap("REVIVED")),
        LIGHT_GRAY.wrap("You have " + LIGHT_GREEN.wrap(PLAYER_LIVES + "❤") + " extra lifes left.")
    );

    public static final LangText DUNGEON_REVIVE_NO_LIFES = LangText.of("Dungeon.Revive.NoLifes",
        OUTPUT.wrap(10, 50) + SOUND.wrap(Sound.ITEM_TOTEM_USE),
        LIGHT_GREEN.wrap(BOLD.wrap("REVIVED")),
        LIGHT_GRAY.wrap("You have " + LIGHT_RED.wrap("no extra lifes") + " left.")
    );



    public static final LangText DUNGEON_END_ALL_DEAD = LangText.of("Dungeon.Ending.AllDead",
        OUTPUT.wrap(10, 80) + SOUND.wrap(Sound.ENTITY_BLAZE_DEATH),
        LIGHT_RED.wrap(BOLD.wrap("Defeat")),
        LIGHT_GRAY.wrap("All players died.")
    );

    public static final LangText DUNGEON_END_TIMEOUT = LangText.of("Dungeon.Ending.Timeout",
        OUTPUT.wrap(10, 80) + SOUND.wrap(Sound.ENTITY_BLAZE_DEATH),
        LIGHT_RED.wrap(BOLD.wrap("Time Out")),
        LIGHT_GRAY.wrap("The time has expired.")
    );

    public static final LangText DUNGEON_END_DEFEAT = LangText.of("Dungeon.Ending.Defeat",
        OUTPUT.wrap(10, 80) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_RED.wrap(BOLD.wrap("Defeat")),
        LIGHT_GRAY.wrap("You failed the " + LIGHT_RED.wrap(DUNGEON_NAME) + " dungeon.")
    );

    public static final LangText DUNGEON_END_COMPLETED = LangText.of("Dungeon.Ending.Completed",
        OUTPUT.wrap(10, 80) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GREEN.wrap(BOLD.wrap("Completed")),
        LIGHT_GRAY.wrap("You completed the " + LIGHT_GREEN.wrap(DUNGEON_NAME) + " dungeon.")
    );



    public static final LangText KIT_CREATE_DONE_NEW = LangText.of("Kit.Create.Done.New",
        LIGHT_GRAY.wrap("Created " + LIGHT_GREEN.wrap(KIT_NAME) + " kit."));

    public static final LangText KIT_CREATE_DONE_UPDATE = LangText.of("Kit.Create.Done.Update",
        LIGHT_GRAY.wrap("Updated " + LIGHT_GREEN.wrap(KIT_NAME) + " kit."));

    public static final LangText KIT_GRANT_DONE = LangText.of("Kit.Grant.Done",
        GRAY.wrap("Granted " + GREEN.wrap(KIT_NAME) + " kit access to " + GREEN.wrap(PLAYER_NAME) + ".")
    );

    public static final LangText KIT_REVOKE_DONE = LangText.of("Kit.Revoke.Done",
        GRAY.wrap("Revoked " + RED.wrap(KIT_NAME) + " kit access from " + RED.wrap(PLAYER_NAME) + ".")
    );

    public static final LangText KIT_BUY_ERROR_INSUFFICIENT_FUNDS = LangText.of("Kit.Buy.Error.InsufficientFunds",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You can't afford " + LIGHT_RED.wrap(KIT_NAME) + " kit for " + LIGHT_RED.wrap(KIT_COST))
    );

    public static final LangText KIT_BUY_SUCCESS = LangText.of("Kit.Buy.Success",
        SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP),
        LIGHT_GRAY.wrap("You purchased " + LIGHT_GREEN.wrap(KIT_NAME) + " kit for " + LIGHT_GREEN.wrap(KIT_COST))
    );



    public static final LangText SELECTION_INFO_CUBOID = LangText.of("Selection.Info.Cuboid",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap("#" + GENERIC_VALUE) + " position.")
    );

    public static final LangText SELECTION_INFO_POSITION_ADD = LangText.of("Selection.Info.PositionAdd",
        LIGHT_GRAY.wrap("Added block position.")
    );

    public static final LangText SELECTION_INFO_POSITION_REMOVE = LangText.of("Selection.Info.PositionRemove",
        LIGHT_GRAY.wrap("Removed block position.")
    );



    public static final LangString UI_TASK_EMPTY_LIST = LangString.of("UI.Task.EmptyList",
        LIGHT_RED.wrap("✘ No active tasks."));

    public static final LangString UI_TASK_COMPLETED = LangString.of("UI.Task.Completed",
        GREEN.wrap("✔") + " " + DARK_GRAY.wrap(STRIKETHROUGH.wrap(GENERIC_NAME + ": " + GENERIC_VALUE)));

    public static final LangString UI_TASK_INCOMPLETED = LangString.of("UI.Task.Incompleted",
        RED.wrap("✘") + " " + GRAY.wrap(GENERIC_NAME + ": " + GENERIC_VALUE));

    public static final LangString UI_BOARD_PLAYER_READY = LangString.of("UI.Board.Player.Ready",
        GRAY.wrap(PLAYER_DISPLAY_NAME) + " " + GREEN.wrap("✔"));

    public static final LangString UI_BOARD_PLAYER_NOT_READY = LangString.of("UI.Board.Player.NotReady",
        GRAY.wrap(PLAYER_DISPLAY_NAME) + " " + RED.wrap("✘"));

    public static final LangUIButton UI_CONFIRMATION_DUNGEON_ENTER_NO_KITS = LangUIButton.builder("UI.Confirmation.Dungeon.Enter.NoKits", "Dungeon Info")
        .description("Enter Cost: " + RED.wrap(DUNGEON_ENTRANCE_PAYMENT))
        .build();

    public static final LangUIButton UI_CONFIRMATION_DUNGEON_ENTER_OWN_KIT = LangUIButton.builder("UI.Confirmation.Dungeon.Enter.OwnedKit", "Dungeon Info")
        .description("Your Kit: " + WHITE.wrap(KIT_NAME), "Enter Cost: " + RED.wrap(DUNGEON_ENTRANCE_PAYMENT), "", KIT_ATTRIBUTES, KIT_EFFECTS)
        .build();

    public static final LangUIButton UI_CONFIRMATION_DUNGEON_ENTER_RENT_KIT = LangUIButton.builder("UI.Confirmation.Dungeon.Enter.RentedKit", "Dungeon Info")
        .description(
            "Your Kit: " + WHITE.wrap(KIT_NAME),
            "Kit Cost: " + RED.wrap(KIT_COST),
            "Enter Cost: " + RED.wrap(DUNGEON_ENTRANCE_PAYMENT),
            "",
            KIT_ATTRIBUTES,
            KIT_EFFECTS
        )
        .build();

    public static final LangUIButton UI_CONFIRMATION_KIT_PURCHASE = LangUIButton.builder("UI.Confirmation.Kit.Purchase", "Kit Info")
        .description("Kit: " + WHITE.wrap(KIT_NAME), "Cost: " + RED.wrap(KIT_COST), "", KIT_ATTRIBUTES, KIT_EFFECTS)
        .build();

    public static final LangString UI_KIT_NO_ATTRIBUTES = LangString.of("UI.Kit.NoAttributes", LIGHT_RED.wrap("✘ No attribute modifiers."));
    public static final LangString UI_KIT_NO_EFFECTS    = LangString.of("UI.Kit.NoEffects", LIGHT_RED.wrap("✘ No potion effects."));

    public static final LangString UI_LEVEL_MIN_ONLY = LangString.of("UI.LevelRequirement.MinOnly", LIGHT_RED.wrap(GENERIC_MIN + "+"));
    public static final LangString UI_LEVEL_MAX_ONLY = LangString.of("UI.LevelRequirement.MaxOnly", LIGHT_RED.wrap("<= " + GENERIC_MAX));
    public static final LangString UI_LEVEL_RANGE    = LangString.of("UI.LevelRequirement.Range", LIGHT_RED.wrap(GENERIC_MIN + " - " + GENERIC_MAX));

    public static final LangString UI_POTION_EFFECT_ENTRY = LangString.of("UI.PotionEffect.Entry", LIGHT_YELLOW.wrap("● " + LIGHT_GRAY.wrap(GENERIC_NAME) + " " + GENERIC_AMOUNT));

    public static final LangString UI_ATTRIBUTE_ENTRY           = LangString.of("UI.Attribute.Entry", LIGHT_YELLOW.wrap("● " + LIGHT_GRAY.wrap(GENERIC_NAME) + " " + GENERIC_AMOUNT));
    public static final LangString UI_ATTRIBUTE_POSITIVE_PLAIN  = LangString.of("UI.Attribute.Positive.Plain", GREEN.wrap("+" + GENERIC_VALUE));
    public static final LangString UI_ATTRIBUTE_POSITIVE_SCALAR = LangString.of("UI.Attribute.Positive.Scalar", GREEN.wrap("+" + GENERIC_VALUE + "%"));
    public static final LangString UI_ATTRIBUTE_NEGATIVE_PLAIN  = LangString.of("UI.Attribute.Negative.Plain", RED.wrap(GENERIC_VALUE));
    public static final LangString UI_ATTRIBUTE_NEGATIVE_SCALAR = LangString.of("UI.Attribute.Negative.Scalar", RED.wrap(GENERIC_VALUE + "%"));

    public static final LangString OTHER_FREE = LangString.of("Other.Free", "Free");

    public static final LangUIButton OTHER_BROKEN_ITEM = LangUIButton.builder("Other.BrokenItem", LIGHT_RED.wrap(BOLD.wrap("< Invalid Item >")))
        .description(
            LIGHT_GRAY.wrap("This item wasn't parsed properly."),
            LIGHT_GRAY.wrap("Check console logs for details.")
        ).formatted(false).build();
}
