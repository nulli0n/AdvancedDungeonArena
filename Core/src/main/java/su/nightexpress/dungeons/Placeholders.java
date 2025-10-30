package su.nightexpress.dungeons;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.feature.LevelRequirement;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.lootchest.LootChest;
import su.nightexpress.dungeons.dungeon.module.Features;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.dungeon.reward.Reward;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.spot.SpotState;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.util.UIUtils;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderList;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Placeholders extends su.nightexpress.nightcore.util.Placeholders {

    public static final String LINK_WIKI         = "https://nightexpressdev.com/dungeon-arena/";
    public static final String LINK_PLACEHOLDERS = LINK_WIKI + "placeholders";
    public static final String LINK_WIKI_MOBS    = LINK_WIKI + "mobs";
    public static final String LINK_WIKI_SCOREBOARD    = LINK_WIKI + "dungeons/features/scoreboard";

    public static final String ALIAS_BASIC          = "ada";
    public static final String ALIAS_JOIN           = "join";
    public static final String ALIAS_WAND           = "wand";
    public static final String ALIAS_CREATE         = "create";
    public static final String ALIAS_SET_PROTECTION = "setprotection";
    public static final String ALIAS_SET_LOBBY   = "setlobby";
    //public static final String ALIAS_ADD_SPAWNER = "addspawner";

    public static final String GENERIC_COMMAND = "%command%";
    public static final String GENERIC_TIME    = "%time%";
    public static final String GENERIC_NAME    = "%name%";
    public static final String GENERIC_LEVEL = "%level%";
    public static final String GENERIC_VALUE   = "%value%";
    public static final String GENERIC_PRICE   = "%price%";
    public static final String GENERIC_AMOUNT  = "%amount%";
    public static final String GENERIC_MESSAGE = "%message%";
    public static final String GENERIC_TYPE    = "%type%";
    //public static final String GENERIC_STATE   = "%state%";
    public static final String GENERIC_CURRENT = "%current%";
    public static final String GENERIC_MIN     = "%min%";
    public static final String GENERIC_MAX     = "%max%";
    public static final String GENERIC_TASKS   = "%tasks%";
    public static final String GENERIC_PLAYERS = "%players%";

    public static final String CURRENCY_NAME = "%currency_name%";
    public static final String CURRENCY_ID   = "%currency_id%";

    public static final String DUNGEON_ID               = "%dungeon_id%";
    public static final String DUNGEON_ACTIVE           = "%dungeon_active%";
    public static final String DUNGEON_NAME             = "%dungeon_name%";
    public static final String DUNGEON_DESCRIPTION      = "%dungeon_description%";
    public static final String DUNGEON_STATE            = "%dungeon_state%";
    public static final String DUNGEON_PLAYERS          = "%dungeon_players%";
    public static final String DUNGEON_ALIVE_PLAYERS    = "%dungeon_alive_players%";
    public static final String DUNGEON_DEAD_PLAYERS     = "%dungeon_dead_players%";
    public static final String DUNGEON_MAX_PLAYERS      = "%dungeon_max_players%";
    public static final String DUNGEON_ENEMY_MOBS       = "%dungeon_enemy_mobs%";
    public static final String DUNGEON_ALLY_MOBS        = "%dungeon_ally_mobs%";
    public static final String DUNGEON_COUNTDOWN        = "%dungeon_countdown%";
    public static final String DUNGEON_TIMELEFT         = "%dungeon_timeleft%";
    //public static final String DUNGEON_SCORE            = "%dungeon_score%";
    public static final String DUNGEON_STAGE            = "%dungeon_stage%";
    public static final String DUNGEON_LEVEL            = "%dungeon_level%";
    public static final String DUNGEON_ENTRANCE_PAYMENT  = "%dungeon_entrance_payment%";
    public static final String DUNGEON_LEVEL_REQUIREMENT = "%dungeon_level_requirement%";

    public static final Function<String, String> DUNGEON_VAR     = var -> "%var_" + var + "%";
    public static final Function<String, String> DUNGEON_VAR_RAW = var -> "%var_raw_" + var + "%";

    public static final String REWARD_ID                   = "%reward_id%";
    public static final String REWARD_NAME                 = "%reward_name%";
    public static final String REWARD_DESCRIPTION = "%reward_description%";

    public static final String LOOT_CHEST_ID = "%lootchest_id%";

    public static final String SPOT_ID       = "%spot_id%";
    public static final String SPOT_NAME     = "%spot_name%";
    public static final String SPOT_STATE_ID = "%spot_state_id%";

    public static final String KIT_ID          = "%kit_id%";
    public static final String KIT_NAME        = "%kit_name%";
    public static final String KIT_DESCRIPTION = "%kit_description%";
    public static final String KIT_ATTRIBUTES  = "%kit_attributes%";
    public static final String KIT_EFFECTS     = "%kit_effects%";
    public static final String KIT_COMMANDS    = "%kit_commands%";
    public static final String KIT_COST        = "%kit_cost%";

    public static final String PLAYER_NAME              = "%player_name%";
    public static final String PLAYER_LIVES             = "%player_lives%";
    public static final String PLAYER_KILL_STREAK       = "%player_streak%";
    public static final String PLAYER_KILL_STREAK_DECAY = "%player_streak_decay%";
    public static final String PLAYER_KILLS             = "%player_kills%";
    public static final String PLAYER_SCORE             = "%player_score%";
    public static final String PLAYER_IS_READY          = "%player_is_ready%";
    public static final String PLAYER_KIT_NAME          = "%player_kit_name%";

    public static final String STAGE_ID          = "%stage_id%";
    public static final String STAGE_NAME        = "%stage_name%";
    public static final String STAGE_DESCRIPTION = "%stage_description%";

    public static final String LEVEL_ID          = "%level_id%";
    public static final String LEVEL_NAME        = "%level_name%";
    public static final String LEVEL_DESCRIPTION = "%level_description%";

    @NotNull
    public static final PlaceholderList<DungeonConfig> DUNGEON_CONFIG = PlaceholderList.create(list -> list
        .add(DUNGEON_ID, DungeonConfig::getId)
        .add(DUNGEON_NAME, DungeonConfig::getName)
    );

    @NotNull
    public static final PlaceholderList<DungeonInstance> DUNGEON_INSTANCE = PlaceholderList.create(list -> list
            .add(DUNGEON_ID, DungeonInstance::getId)
            .add(DUNGEON_ACTIVE, instance -> CoreLang.STATE_YES_NO.get(instance.isActive()))
            .add(DUNGEON_NAME, instance -> instance.getConfig().getName())
            .add(DUNGEON_DESCRIPTION, instance -> String.join("\n", instance.getConfig().getDescription()))
            .add(DUNGEON_STATE, instance -> Lang.GAME_STATE.getLocalized(instance.getState()))
            .add(DUNGEON_PLAYERS, instance -> String.valueOf(instance.getPlayers().size()))
            .add(DUNGEON_DEAD_PLAYERS, instance -> String.valueOf(instance.countDeadPlayers()))
            .add(DUNGEON_ALIVE_PLAYERS, instance -> String.valueOf(instance.countAlivePlayers()))
            .add(DUNGEON_MAX_PLAYERS, instance -> String.valueOf(instance.getConfig().gameSettings().getMaxPlayers()))
            .add(DUNGEON_ENEMY_MOBS, instance -> String.valueOf(instance.getEnemyMobs().size()))
            .add(DUNGEON_ALLY_MOBS, instance -> String.valueOf(instance.getAllyMobs().size()))
            .add(DUNGEON_COUNTDOWN, instance -> String.valueOf(instance.getCountdown()))
            .add(DUNGEON_TIMELEFT, instance -> {
                long timeLeft = instance.getTimeLeft();
                if (timeLeft < 0) return CoreLang.OTHER_INFINITY.text();

                return TimeFormats.toDigital(timeLeft * 1000L);
            })
            .add(DUNGEON_LEVEL, instance -> instance.getLevel().getDisplayName())
            .add(DUNGEON_STAGE, instance -> instance.getStage().getDisplayName())
            .add(DUNGEON_ENTRANCE_PAYMENT, instance -> {
                Features features = instance.getConfig().features();
                if (!features.hasEntranceCost()) return Lang.OTHER_FREE.text();

                var map = instance.getConfig().features().getEntranceCostMap();
                return map.entrySet().stream().map(entry -> {
                    Currency currency = EconomyBridge.getCurrency(entry.getKey());
                    return currency == null ? null : currency.format(entry.getValue());
                }).filter(Objects::nonNull).collect(Collectors.joining(", "));
            })
            .add(DUNGEON_LEVEL_REQUIREMENT, instance -> {
                LevelRequirement requirement = instance.getConfig().features().getLevelRequirement();
                if (!requirement.isRequired()) return CoreLang.OTHER_ANY.text();

                int min = requirement.getMinLevel();
                int max = requirement.getMaxLevel();
                String result = Lang.UI_LEVEL_RANGE.text();

                if (!requirement.hasMaxValue()) {
                    result = Lang.UI_LEVEL_MIN_ONLY.text();
                }
                if (!requirement.hasMinValue()) {
                    result = Lang.UI_LEVEL_MAX_ONLY.text();
                }

                return result.replace(GENERIC_MIN, String.valueOf(min)).replace(GENERIC_MAX, String.valueOf(max));
            })
    );

    public static final PlaceholderList<Stage> STAGE = PlaceholderList.create(list -> list
        .add(STAGE_ID, Stage::getId)
        .add(STAGE_NAME, Stage::getDisplayName)
        .add(STAGE_DESCRIPTION, Stage::getDescription)
    );

    public static final PlaceholderList<Level> LEVEL = PlaceholderList.create(list -> list
        .add(LEVEL_ID, Level::getId)
        .add(LEVEL_NAME, Level::getDisplayName)
        .add(LEVEL_DESCRIPTION, Level::getDescription)
    );

    public static final PlaceholderList<DungeonGamer> DUNGEON_GAMER = PlaceholderList.create(list -> list
        .add(PLAYER_NAME, gamer -> gamer.getPlayer().getName())
        .add(PLAYER_DISPLAY_NAME, gamer -> gamer.getPlayer().getDisplayName())
        .add(PLAYER_LIVES, gamer -> String.valueOf(gamer.getLives()))
        .add(PLAYER_KILL_STREAK, gamer -> String.valueOf(gamer.getKillStreak()))
        .add(PLAYER_KILL_STREAK_DECAY, gamer -> TimeFormats.formatAmount(gamer.getKillStreakDecay() * 1000L, TimeFormatType.LITERAL))
        .add(PLAYER_SCORE, gamer -> String.valueOf(gamer.getScore()))
        .add(PLAYER_KILLS, gamer -> NumberUtil.format(gamer.getKills()))
        .add(PLAYER_IS_READY, gamer -> CoreLang.STATE_YES_NO.get(gamer.isReady()))
        .add(PLAYER_KIT_NAME, gamer -> gamer.getKit() == null ? "-" : gamer.getKit().getName())
    );

    public static final PlaceholderList<Reward> REWARD = PlaceholderList.create(list -> list
        .add(REWARD_ID, Reward::getId)
        .add(REWARD_NAME, Reward::getName)
        .add(REWARD_DESCRIPTION, reward -> String.join("\n", reward.getDescription()))
    );

    public static final PlaceholderList<LootChest> LOOT_CHEST = PlaceholderList.create(list -> list
        .add(LOOT_CHEST_ID, LootChest::getId)
    );

    public static final PlaceholderList<Spot> SPOT = PlaceholderList.create(list -> list
        .add(SPOT_ID, Spot::getId)
        .add(SPOT_NAME, Spot::getName)
    );

    public static final PlaceholderList<SpotState> SPOT_STATE = PlaceholderList.create(list -> list
        .add(SPOT_STATE_ID, SpotState::getId)
    );

    public static final PlaceholderList<Kit> KIT = PlaceholderList.create(list -> list
        .add(KIT_ID, Kit::getId)
        .add(KIT_NAME, Kit::getName)
        .add(KIT_DESCRIPTION, kit -> String.join("\n", kit.getDescription()))
        .add(KIT_ATTRIBUTES, kit -> {
            if (!kit.hasAttributes()) return Lang.UI_KIT_NO_ATTRIBUTES.text();

            return kit.getAttributeMap().entrySet().stream()
                .map(entry -> UIUtils.formatAttributeEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        })
        .add(KIT_EFFECTS, kit -> {
            if (!kit.hasPotionEffects()) return Lang.UI_KIT_NO_EFFECTS.text();

            return kit.getPotionEffects().stream().map(UIUtils::formatPotionEffectEntry).collect(Collectors.joining("\n"));
        })
        .add(KIT_COST, kit -> {
            if (!kit.hasCost()) return Lang.OTHER_FREE.text();

            return kit.getCostMap().entrySet().stream()
                .map(entry -> UIUtils.formatCostEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));
        })
    );
}
