package su.nightexpress.dungeons.config;

import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class Perms {

    public static final String PREFIX         = "dungeonarena.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_DUNGEON = PREFIX + "dungeon.";
    public static final String PREFIX_KIT     = PREFIX + "kit.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission PLUGIN  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission CREATOR     = new UniPermission(PREFIX + "creator");
    public static final UniPermission DUNGEON_ALL = new UniPermission(PREFIX_DUNGEON + Placeholders.WILDCARD);
    public static final UniPermission KIT_ALL     = new UniPermission(PREFIX_KIT + Placeholders.WILDCARD);

    public static final UniPermission BYPASS_DUNGEON_JOIN_STARTED   = new UniPermission(PREFIX_BYPASS + "dungeon.join.started");
    public static final UniPermission BYPASS_DUNGEON_ENTRANCE_COST  = new UniPermission(PREFIX_BYPASS + "dungeon.entrance.cost");
    public static final UniPermission BYPASS_DUNGEON_ENTRANCE_LEVEL = new UniPermission(PREFIX_BYPASS + "dungeon.entrance.level");
    public static final UniPermission BYPASS_DUNGEON_COMMANDS       = new UniPermission(PREFIX_BYPASS + "dungeon.game.commands");
    public static final UniPermission BYPASS_DUNGEON_COOLDOWN       = new UniPermission(PREFIX_BYPASS + "dungeon.cooldown");
    public static final UniPermission BYPASS_KIT_COST               = new UniPermission(PREFIX_BYPASS + "kit.cost");

    public static final UniPermission COMMAND_RELOAD         = new UniPermission(PREFIX + "command.reload");
    public static final UniPermission COMMAND_WAND           = new UniPermission(PREFIX + "command.wand");
    public static final UniPermission COMMAND_CREATE         = new UniPermission(PREFIX + "command.create");
    public static final UniPermission COMMAND_SET_PROTECTION = new UniPermission(PREFIX + "command.setprotection");
    public static final UniPermission COMMAND_SET_LOBBY      = new UniPermission(PREFIX + "command.setlobby");
    public static final UniPermission COMMAND_SET_STAGE      = new UniPermission(PREFIX + "command.setstage");
    public static final UniPermission COMMAND_SET_LEVEL      = new UniPermission(PREFIX + "command.setlevel");
    public static final UniPermission COMMAND_STOP           = new UniPermission(PREFIX + "command.stop");
    public static final UniPermission COMMAND_START          = new UniPermission(PREFIX + "command.start");

    public static final UniPermission COMMAND_JOIN          = new UniPermission(PREFIX + "command.join");
    public static final UniPermission COMMAND_JOIN_OTHERS   = new UniPermission(PREFIX + "command.join.others");
    public static final UniPermission COMMAND_LEAVE         = new UniPermission(PREFIX + "command.leave");
    public static final UniPermission COMMAND_BROWSE        = new UniPermission(PREFIX + "command.browse");
    public static final UniPermission COMMAND_BROWSE_OTHERS = new UniPermission(PREFIX + "command.browse.others");

    public static final UniPermission COMMAND_SPAWNER    = new UniPermission(PREFIX + "command.spawner");
    public static final UniPermission COMMAND_LEVEL      = new UniPermission(PREFIX + "command.level");
    public static final UniPermission COMMAND_STAGE      = new UniPermission(PREFIX + "command.stage");
    public static final UniPermission COMMAND_REWARD     = new UniPermission(PREFIX + "command.reward");
    public static final UniPermission COMMAND_LOOT_CHEST = new UniPermission(PREFIX + "command.lootchest");
    public static final UniPermission COMMAND_SPOT       = new UniPermission(PREFIX + "command.spot");
    public static final UniPermission COMMAND_KIT        = new UniPermission(PREFIX + "command.kit");

    static {
        PLUGIN.addChildren(COMMAND, BYPASS, CREATOR, DUNGEON_ALL, KIT_ALL);

        BYPASS.addChildren(
            BYPASS_DUNGEON_COMMANDS,
            BYPASS_DUNGEON_JOIN_STARTED,
            BYPASS_DUNGEON_ENTRANCE_LEVEL,
            BYPASS_DUNGEON_ENTRANCE_COST,
            BYPASS_DUNGEON_COOLDOWN,
            BYPASS_KIT_COST
        );

        COMMAND.addChildren(
            COMMAND_RELOAD,
            COMMAND_WAND,
            COMMAND_CREATE,
            COMMAND_LEVEL,
            COMMAND_STAGE,
            COMMAND_SET_PROTECTION,
            COMMAND_SET_LOBBY,
            COMMAND_SET_STAGE,
            COMMAND_SET_LEVEL,
            COMMAND_SPAWNER,
            COMMAND_KIT,
            COMMAND_REWARD,
            COMMAND_LOOT_CHEST,
            COMMAND_SPOT,
            COMMAND_STOP,
            COMMAND_START,
            COMMAND_JOIN,
            COMMAND_JOIN_OTHERS,
            COMMAND_LEAVE,
            COMMAND_BROWSE,
            COMMAND_BROWSE_OTHERS
        );
    }
}
