package su.nightexpress.dungeons.dungeon.script.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.scale.ScalableAmount;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.wrapper.UniInt;

public class TaskParams implements Writeable {

    private final String display;
    private final UniInt amount;
    private final ScalableAmount scalableAmount;
    private final boolean perPlayer;
    private final boolean autoAdd;

    public TaskParams(String display, UniInt amount, boolean perPlayer, boolean autoAdd) {
        this(display, amount, null, perPlayer, autoAdd);
    }

    public TaskParams(String display, @NotNull ScalableAmount amount, boolean perPlayer, boolean autoAdd) {
        this(display, null, amount, perPlayer, autoAdd);
    }

    private TaskParams(String display, @Nullable UniInt amount, @Nullable ScalableAmount scalableAmount, boolean perPlayer, boolean autoAdd) {
        this.display = display;
        this.amount = amount;
        this.scalableAmount = scalableAmount;
        this.perPlayer = perPlayer;
        this.autoAdd = autoAdd;
    }

    @NotNull
    public static TaskParams read(@NotNull FileConfig config, @NotNull String path) {
        String display = config.getString(path + ".Display", "null");
        boolean perPlayer = config.getBoolean(path + ".PerPlayer");
        boolean autoAdd = ConfigValue.create(path + ".AutoAdd", true).read(config);

        String amountPath = path + ".Amount";
        if (config.contains(amountPath + ".Initial") || config.contains(amountPath + ".Scalers")) {
            ScalableAmount scalable = ScalableAmount.read(config, amountPath);
            return new TaskParams(display, scalable, perPlayer, autoAdd);
        }

        UniInt amount = UniInt.read(config, amountPath);
        return new TaskParams(display, amount, perPlayer, autoAdd);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Display", this.display);
        if (this.scalableAmount != null) {
            config.set(path + ".Amount", this.scalableAmount);
        }
        else if (this.amount != null) {
            this.amount.write(config, path + ".Amount");
        }
        config.set(path + ".PerPlayer", this.perPlayer);
        config.set(path + ".AutoAdd", this.autoAdd);
    }

    @NotNull
    public String getDisplay() {
        return this.display;
    }

    public int rollAmount(@NotNull DungeonInstance dungeon) {
        if (this.scalableAmount != null) {
            return Math.max(1, this.scalableAmount.getScaledInt(dungeon));
        }
        return this.amount == null ? 0 : this.amount.roll();
    }

    public boolean isPerPlayer() {
        return this.perPlayer;
    }

    public boolean isAutoAdd() {
        return this.autoAdd;
    }
}