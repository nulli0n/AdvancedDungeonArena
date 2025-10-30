package su.nightexpress.dungeons.dungeon.feature.board;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.dungeon.Board;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBoard<T> implements Board {

    protected final BoardLayout          layout;
    protected final DungeonGamer         gamer;
    protected final Player               player;
    protected final String               identifier;
    protected final Map<Integer, String> scores;

    public AbstractBoard(@NotNull DungeonGamer gamer, @NotNull BoardLayout layout) {
        this.layout = layout;
        this.gamer = gamer;
        this.player = gamer.getPlayer();
        this.identifier = createIdentifier(this.player).substring(0, 16);
        this.scores = new ConcurrentHashMap<>();
    }

    @NotNull
    public static String createIdentifier(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();

        // Bedrock players have UUIDs leading with zeros.
        if (Players.isBedrock(player)) {
            uuid = new StringBuilder(uuid).reverse().toString();
        }

        return uuid;
    }

    @NotNull
    public final BoardLayout getLayout() {
        return this.gamer.getDungeon().getState() == GameState.INGAME ? this.layout : Config.SCOREBOARD_LOBBY_LAYOUT.get();
    }

    @NotNull
    private String getScoreIdentifier(int score) {
        return "line_" + score;
    }

    protected enum ObjectiveMode {
        CREATE,
        REMOVE,
        UPDATE
    }

    protected abstract void sendPacket(@NotNull Player player, @NotNull T packet);

    @NotNull
    protected abstract T createObjectivePacket(ObjectiveMode mode, @NotNull String displayName);

    @NotNull
    protected abstract T createResetScorePacket(@NotNull String scoreId);

    @NotNull
    protected abstract T createScorePacket(@NotNull String scoreId, int score, @NotNull String text);

    @NotNull
    protected abstract T createDisplayPacket();

    @Override
    public void create() {
        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.CREATE, ""));
        this.sendPacket(this.player, this.createDisplayPacket());
    }

    @Override
    public void remove() {
        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.REMOVE, ""));

        this.scores.forEach((score, text) -> {
            this.sendPacket(this.player, this.createResetScorePacket(this.getScoreIdentifier(score)));
        });

        this.scores.clear();
    }

    @NotNull
    private String replacePlaceholders(@NotNull String string) {
        return Replacer.create()
            .replace(Placeholders.forPlayerWithPAPI(this.player))
            .replace(this.gamer.replacePlaceholders())
            .replace(this.gamer.getDungeon().replacePlaceholders())
            .apply(string);
    }

    @NotNull
    private List<String> getFormattedTasks() {
        DungeonInstance dungeon = this.gamer.getDungeon();
        List<String> list = new ArrayList<>();

        if (!dungeon.hasTasks()) {
            list.add(Lang.UI_TASK_EMPTY_LIST.text());
            return list;
        }

        dungeon.getTaskProgress().forEach((stageTask, progress) -> {
            TextLocale format = progress.isCompleted() ? Lang.UI_TASK_COMPLETED : Lang.UI_TASK_INCOMPLETED;
            list.add(format.text()
                .replace(Placeholders.GENERIC_NAME, stageTask.getParams().getDisplay())
                .replace(Placeholders.GENERIC_VALUE, progress.format(this.gamer.getPlayer()))
            );
        });

        return list;
    }

    @NotNull
    private List<String> getFormattedPlayers() {
        DungeonInstance dungeon = this.gamer.getDungeon();
        List<String> list = new ArrayList<>();

        dungeon.getPlayers().forEach(gamer -> {
            TextLocale format = gamer.isReady() ? Lang.UI_BOARD_PLAYER_READY : Lang.UI_BOARD_PLAYER_NOT_READY;
            list.add(gamer.replacePlaceholders().apply(format.text()));
        });

        return list;
    }

    @Override
    public void update() {
        BoardLayout layout = this.getLayout();
        String title = layout.getTitle();
        List<String> lines = new ArrayList<>();

        for (String line : layout.getLines()) {
            if (line.equalsIgnoreCase(Placeholders.GENERIC_TASKS)) {
                lines.addAll(this.getFormattedTasks());
                continue;
            }
            else if (line.equalsIgnoreCase(Placeholders.GENERIC_PLAYERS)) {
                lines.addAll(this.getFormattedPlayers());
                continue;
            }
            lines.add(line);
        }

        Map<Integer, String> scores = new HashMap<>();
        int index = lines.size();

        for (String line : lines) {
            scores.put(index--, this.replacePlaceholders(line));
        }
        title = this.replacePlaceholders(title);


        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.UPDATE, title));

        scores.forEach((score, text) -> {
            String scoreId = this.getScoreIdentifier(score);

            this.sendPacket(this.player, this.createScorePacket(scoreId, score, text));
        });

        this.scores.entrySet().stream().filter(entry -> !scores.containsKey(entry.getKey())).forEach(entry -> {
            int score = entry.getKey();
            String scoreId = this.getScoreIdentifier(score);

            this.sendPacket(this.player, this.createResetScorePacket(scoreId));
        });

        this.scores.clear();
        this.scores.putAll(scores);
    }
}
