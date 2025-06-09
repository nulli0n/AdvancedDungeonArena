package su.nightexpress.dungeons.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.time.TimeFormats;

public class PlaceholderHook {

    public static final String ID = "ada";

    private static Expansion expansion;

    public static void setup(@NotNull DungeonPlugin plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class Expansion extends PlaceholderExpansion {

        private final DungeonPlugin plugin;

        public Expansion(@NotNull DungeonPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        @NotNull
        public String getAuthor() {
            return plugin.getDescription().getAuthors().getFirst();
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return ID;
        }

        @Override
        @NotNull
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, String params) {
            if (params.startsWith("player_")) {
                DungeonGamer gamer = plugin.getDungeonManager().getDungeonPlayer(player);
                if (gamer == null) return "-";

                DungeonInstance arena = gamer.getDungeon();
                String var = params.substring("player_".length());

                if (var.equalsIgnoreCase("score")) {
                    return NumberUtil.format(gamer.getScore());
                }
                if (var.equalsIgnoreCase("streak_length")) {
                    return NumberUtil.format(gamer.getKillStreak());
                }
                if (var.equalsIgnoreCase("streak_decay")) {
                    return TimeFormats.toLiteral(gamer.getKillStreakDecay() * 1000L);
                }
                if (var.equalsIgnoreCase("kills")) {
                    return NumberUtil.format(gamer.getKills());
                }
                if (var.equalsIgnoreCase("lives")) {
                    return NumberUtil.format(gamer.getLives());
                }
                return forDungeon(arena, var);
            }


            if (params.startsWith("dungeon_")) { // ama_arena_tutorial_name
                String raw = params.substring("dungeon_".length()); // tutorial_name_something
                int index = raw.indexOf("_");
                if (index < 0) return null;

                String dungeonId = raw.substring(0, index); // tutorial
                String var = raw.substring(index + 1); // name_something
                DungeonInstance dungeon = plugin.getDungeonManager().getInstanceById(dungeonId);
                if (dungeon == null) return "-";

                return forDungeon(dungeon, var);
            }

            return null;
        }

        @Nullable
        public String forDungeon(@NotNull DungeonInstance dungeon, @NotNull String var) {
            if (var.equalsIgnoreCase("name")) {
                return dungeon.getConfig().getName();
            }
            if (var.equalsIgnoreCase("empty")) {
                return Lang.getYesOrNo(dungeon.getPlayers().isEmpty());
            }
            if (var.equalsIgnoreCase("state")) {
                return Lang.GAME_STATE.getLocalized(dungeon.getState());
            }

            if (var.equalsIgnoreCase("alive_enemy_mobs")) {
                return NumberUtil.format(dungeon.countMobs(MobFaction.ENEMY));
            }
            if (var.equalsIgnoreCase("alive_ally_mobs")) {
                return NumberUtil.format(dungeon.countMobs(MobFaction.ALLY));
            }

            if (var.equalsIgnoreCase("alive_players")) {
                return NumberUtil.format(dungeon.countAlivePlayers());
            }
            if (var.equalsIgnoreCase("dead_players")) {
                return NumberUtil.format(dungeon.countDeadPlayers());
            }
            if (var.equalsIgnoreCase("players")) {
                return NumberUtil.format(dungeon.countPlayers());
            }

            if (var.equalsIgnoreCase("timeleft")) {
                long timeleft = dungeon.getTimeLeft();
                return timeleft < 0 ? Lang.OTHER_INFINITY.getString() : TimeFormats.toDigital(timeleft * 1000L);
            }
            return null;
        }
    }
}
