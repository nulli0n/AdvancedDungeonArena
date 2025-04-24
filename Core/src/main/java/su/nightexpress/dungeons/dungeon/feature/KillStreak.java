package su.nightexpress.dungeons.dungeon.feature;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.DungeonsAPI;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.List;

public class KillStreak implements Writeable {

    private final String       id;
    private final int          kills;
    private final boolean      repeatable;
    private final String       rawMessage;
    private final List<String> commands;

    private final LangMessage message;

    public KillStreak(@NotNull String id, int kills, boolean repeatable, @NotNull String rawMessage, @NotNull List<String> commands) {
        this.id = id.toLowerCase();
        this.kills = kills;
        this.repeatable = repeatable;
        this.rawMessage = rawMessage;
        this.message = LangMessage.parse(DungeonsAPI.getPlugin(), rawMessage);
        this.commands = commands;
    }

    @NotNull
    public static KillStreak read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        int kills = ConfigValue.create(path + ".Kills", 0).read(config);
        boolean repeatable = ConfigValue.create(path + ".Repeatable", false).read(config);
        String rawMessage = config.getString(path + ".Message", "");
        List<String> commands = config.getStringList(path + ".Commands");

        return new KillStreak(id, kills, repeatable, rawMessage, commands);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Kills", this.kills);
        config.set(path + ".Repeatable", this.repeatable);
        config.set(path + ".Message", this.rawMessage);
        config.set(path + ".Commands", this.commands);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public int getKills() {
        return this.kills;
    }

    public boolean isRepeatable() {
        return this.repeatable;
    }

    @NotNull
    public LangMessage getMessage() {
        return this.message;
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public boolean isGoodStreak(int streak) {
        return this.kills == streak || (this.isRepeatable() && streak % this.kills == 0);
    }

    public void run(@NotNull DungeonInstance dungeon, @NotNull DungeonGamer gamer) {
        Player player = gamer.getPlayer();

        this.message.send(player, replacer -> this.replacement(dungeon, gamer, replacer));

        Players.dispatchCommands(player, this.replacement(dungeon, gamer, Replacer.create()).apply(this.commands));
    }

    @NotNull
    private Replacer replacement(@NotNull DungeonInstance dungeon, @NotNull DungeonGamer gamer, @NotNull Replacer replacer) {
        return replacer
            .replace(dungeon.replaceVariables())
            .replace(gamer.replacePlaceholders())
            .replace(Placeholders.forPlayerWithPAPI(gamer.getPlayer()))
            .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(this.kills));
    }
}
