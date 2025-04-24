package su.nightexpress.dungeons.dungeon.script.task;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.wrapper.UniInt;

public class TaskParams implements Writeable {

    private final String display;
    private final UniInt amount;
    private final boolean perPlayer;
    private final boolean autoAdd;

    public TaskParams(String display, UniInt amount, boolean perPlayer, boolean autoAdd) {
        this.display = display;
        this.amount = amount;
        this.perPlayer = perPlayer;
        this.autoAdd = autoAdd;
    }

    @NotNull
    public static TaskParams read(@NotNull FileConfig config, @NotNull String path) {
        String display = config.getString(path + ".Display", "null");
        UniInt amount = UniInt.read(config, path + ".Amount");
        boolean perPlayer = config.getBoolean(path + ".PerPlayer");
        boolean autoAdd = ConfigValue.create(path + ".AutoAdd", true).read(config);

        return new TaskParams(display, amount, perPlayer, autoAdd);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Display", this.display);
        this.amount.write(config, path + ".Amount");
        config.set(path + ".PerPlayer", this.perPlayer);
        config.set(path + ".AutoAdd", this.autoAdd);
    }

    @NotNull
    public String getDisplay() {
        return this.display;
    }

    @NotNull
    public UniInt getAmount() {
        return this.amount;
    }

    public boolean isPerPlayer() {
        return this.perPlayer;
    }

    public boolean isAutoAdd() {
        return this.autoAdd;
    }
}
