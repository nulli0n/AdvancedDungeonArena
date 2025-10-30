package su.nightexpress.dungeons.config;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.dungeon.feature.board.BoardLayout;
import su.nightexpress.dungeons.registry.mob.MobProviderId;
import su.nightexpress.dungeons.dungeon.feature.KillStreak;
import su.nightexpress.dungeons.util.DungeonUtils;
import su.nightexpress.dungeons.util.MobUitls;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.Map;
import java.util.Set;

import static su.nightexpress.dungeons.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Config {

    public static final String DIR_DUNGEONS    = "/dungeons/";
    public static final String DIR_LEVELS      = "/levels/";
    public static final String DIR_STAGES      = "/stages/";
    public static final String DIR_REWARDS     = "/rewards/";
    public static final String DIR_LOOT_CHESTS = "/loot_chests/";
    public static final String DIR_SPOTS       = "/spots/";
    public static final String DIR_MENU        = "/menu/";
    public static final String DIR_KITS        = "/kits/";
    public static final String DIR_MOBS        = "/mobs/";

    public static final ConfigValue<Boolean> CHAT_ENABLED = ConfigValue.create("Chat.Enabled",
        true,
        "Controls whether dungeons should have dedicated chat rooms.");

    public static final ConfigValue<String> CHAT_FORMAT = ConfigValue.create("Chat.Format",
        GRAY.wrap("[" + WHITE.wrap(DUNGEON_NAME) + " - Chat]") + " " + CYAN.wrap(PLAYER_NAME) + LIGHT_GRAY.wrap(": " + GENERIC_MESSAGE),
        "Sets the chat format.",
        "Use '" + PLAYER_NAME + "' for player name.",
        "Use '" + GENERIC_MESSAGE + "' for message text.",
        "You can use 'Dungeon' and 'Dungeon Player' placeholders: " + LINK_PLACEHOLDERS,
        Plugins.PLACEHOLDER_API + " available."
    );



    public static final ConfigValue<Boolean> DUNGEON_ALWAYS_RESTORE_INVENTORY = ConfigValue.create("Dungeon.Always_Restore_Inventory",
        false,
        "Controls whether player inventory is restored to pre-dungeon state even if kits are disabled.");

    public static final ConfigValue<Boolean> DUNGEON_FIX_SPLASH_POTIONS = ConfigValue.create("Dungeon.Fix_Splash_Potions",
        true,
        "Controls whether plugin should 'fix' splash and lingering potions so they will not injure allied players and mobs.");

    public static final ConfigValue<Integer> DUNGEON_COUNTDOWN_DEFEAT = ConfigValue.create("Dungeon.Countdown.Defeat",
        10,
        "Sets how soon (in seconds) game on the dungeon will be stopped in case of defeat?");

    public static final ConfigValue<Integer> DUNGEON_COUNTDOWN_VICTORY = ConfigValue.create("Dungeon.Countdown.Victory",
        10,
        "Sets how soon (in seconds) game on the dungeon will be stopped in case of victory?");

    public static final ConfigValue<Integer> DUNGEON_TIME_TO_REVIVE = ConfigValue.create("Dungeon.TimeToRevive",
        10,
        "Sets how much time (in seconds) must pass after the last player dies before the raid fails.",
        "During this time, the plugin will wait for a potential player resurrection so the raid can continue."
    );

    public static final ConfigValue<Integer> DUNGEON_LOBBY_DROP_TIMER = ConfigValue.create("Dungeon.DropTimerWhenReady",
        10,
        "Instantly decreases lobby countdown timer to this value, if all players are ready to play.",
        "Set this to -1 to disable feature."
    );



    public static final ConfigValue<NightItem> ITEMS_WAND_ITEM = ConfigValue.create("Items.WandItem",
        DungeonUtils.getDefaultSelectionItem(),
        "Item used to select dungeon cuboids.",
        URL_WIKI_ITEMS
    );

    public static final ConfigValue<Boolean> ITEMS_TNT_ALLOW_PLACEMENT = ConfigValue.create("Items.TNT.Allow_Placement",
        true,
        "Controls whether players can place auto-ignited TNTs in the dungeon.");

    public static final ConfigValue<Integer> ITEMS_TNT_FUSE_TICKS = ConfigValue.create("Items.TNT.Fuse_Ticks",
        30,
        "Sets how soon (in ticks) placed TNTs will explode.",
        "[20 ticks = 1 second]",
        "[Plugin default is 30]",
        "[Game default is 80]");

    public static final ConfigValue<Boolean> ITEM_FIRE_CHARGE_HAND_LAUNCH = ConfigValue.create("Items.FireCharge.Hand_Launch",
        true,
        "Controls whether players can launch fireballs using Fire Charge item.");



    public static final ConfigValue<Boolean> KITS_PERMANENT_PURCHASES = ConfigValue.create("Kits.Permanent_Purchases",
        true,
        "When enabled, purchased kits will last forever on player accounts.",
        "When disabled, purchased kits will last for a one game/raid only.");

    public static final ConfigValue<Boolean> KITS_PREVENT_ITEM_SHARE = ConfigValue.create("Kits.Prevent_Item_Share",
        true,
        "Controls whether players can share kit items by dropping them or placing in containers."
    );

    public static final ConfigValue<Boolean> KITS_HIDE_DISABLED_KITS = ConfigValue.create("Kits.Hide_Disabled_Kits",
        true,
        "Controls whether kits that are not available in the selected dungeon will be hidden from the kit selection GUI."
    );

    public static final ConfigValue<Boolean> KITS_HIDE_LOCKED_KITS = ConfigValue.create("Kits.Hide_Locked_Kits",
        true,
        "Controls whether kits for which player don't have permission will be hidden from the kit selection GUI."
    );



    public static final ConfigValue<Map<String, BoardLayout>> SCOREBOARD_LAYOUTS = ConfigValue.forMapById("Scoreboard.Layouts",
        BoardLayout::read,
        map -> map.put(Placeholders.DEFAULT, DungeonUtils.getDefaultBoardLayout()),
        "Custom scoreboard layouts to use in dungeons.",
        LINK_WIKI_SCOREBOARD
    );

    public static final ConfigValue<BoardLayout> SCOREBOARD_LOBBY_LAYOUT = ConfigValue.create("Scoreboard.LobbyLayout",
        BoardLayout::read,
        DungeonUtils.getDefaultLobbyBoardLayout(),
        "Scoreboard layout for the dungeon lobby.",
        LINK_WIKI_SCOREBOARD
    );

    public static final ConfigValue<Boolean> MOBS_REMOVE_UNKNOWN_MOBS = ConfigValue.create("Mobs.Remove_Unknown_Mobs",
        false,
        "When enabled, removes mobs spawned in a dungeon that are not natively supported by the plugin.",
        "This means that any mob with unknown/missing Mob Provider and/or Mob Id will be removed.",
        LINK_WIKI_MOBS
    );

    public static final ConfigValue<String> MOBS_NAME_FORMAT = ConfigValue.create("Mobs.NameFormat",
        LIGHT_YELLOW.wrap(GENERIC_NAME) + " " + GRAY.wrap("Lv. ") + RED.wrap(GENERIC_LEVEL),
        "Sets name format for internal ADA's mobs.",
        "Placeholders: " + GENERIC_NAME + ", " + GENERIC_LEVEL
    );

    public static final ConfigValue<Double> MOBS_SPAWN_OFFSET = ConfigValue.create("Mobs.SpawnOffset",
        1.25,
        "Sets randomized offset for mob spawn locations.",
        "This will make spawning large amount of mobs look more natural."
    );

    public static final ConfigValue<Map<EntityType, MobIdentifier>> MOBS_EGG_ALLIES = ConfigValue.forMap("Mobs.EggAllies",
        BukkitThing::getEntityType,
        BukkitThing::toString,
        MobIdentifier::read,
        map -> map.putAll(MobUitls.getDefaultEggAllies()),
        "List of mobs (Mob Identifiers) allowed to be spawned as ally mobs using spawn eggs.",
        "Syntax: Mob Type -> Mob Identifier.",
        "Mob Types: " + "https://minecraft.wiki/w/Java_Edition_data_values#Entities",
        "Mob Identifiers: " + LINK_WIKI_MOBS
    );

    public static final ConfigValue<Set<MobIdentifier>> MOBS_ALLIES_EXTERNAL = ConfigValue.forSet("Mobs.Ally.ExternalSpawn",
        MobIdentifier::deserialize,
        (cfg, path, set) -> cfg.set(path, Lists.modify(set, MobIdentifier::serialize)),
        Lists.newSet(new MobIdentifier(MobProviderId.MYTHIC_MOBS, "ExampleMobName"), new MobIdentifier(MobProviderId.MYTHIC_MOBS, "AnotherMobName")),
        "Here you can specify which mobs should be considered as 'ally' mobs when spawned externally in a dungeon.",
        "Example: MythicMobs summonned by eggs or skills.",
        "Syntax: 'provider:mobId'",
        "Mobs documentation: " + LINK_WIKI_MOBS,
        "=".repeat(5) + " NOTE " + "=".repeat(5),
        "  This setting only marks them as 'ally' so script conditions with mob faction parameters can work properly.",
        "  Make sure to tweak mob's AI in a corresponding plugin config to make them actually ally to players."
    );

    public static final ConfigValue<Boolean> KILL_STREAKS_ENABLED = ConfigValue.create("KillStreaks.Enabled",
        true,
        "Enables the Kill Streaks feature.");

    public static final ConfigValue<Integer> KILL_STREAKS_DECAY_TIME = ConfigValue.create("KillStreaks.DecayTime",
        30,
        "Sets for how long (in seconds) kill streak will last before reset to zero.");

    public static final ConfigValue<Map<String, KillStreak>> KILL_STREAKS_LIST = ConfigValue.forMapById("KillStreaks.List",
        KillStreak::read,
        map -> map.putAll(MobUitls.getDefaultKillStreaks()),
        "Here you can create custom kill streaks with commands and messages.",
        "Message format: " + URL_WIKI_LANG,
        "Placeholders:",
        "- " + GENERIC_AMOUNT + " - Kills amount.",
        "- " + PLAYER_NAME + " - Player name.",
        "- " + Plugins.PLACEHOLDER_API + " - All placeholders."
    );

    @Nullable
    public static BoardLayout getDungeonBoard(@NotNull String id) {
        return SCOREBOARD_LAYOUTS.get().get(id.toLowerCase());
    }
}
