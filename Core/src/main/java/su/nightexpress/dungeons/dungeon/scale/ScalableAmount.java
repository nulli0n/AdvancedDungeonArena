package su.nightexpress.dungeons.dungeon.scale;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScalableAmount implements Writeable {

    //private final UniInt              initialValue;

    private final String initialMin;
    private final String initialMax;
    private final boolean initialInteger;
    private final Map<String, Scaler> scalers;

    public ScalableAmount(/*@NotNull UniInt initialValue, */String initialMin, String initialMax, boolean initialInteger, @NotNull Map<String, Scaler> scalers) {
        //this.initialValue = initialValue;
        this.initialMin = initialMin;
        this.initialMax = initialMax;
        this.initialInteger = initialInteger;
        this.scalers = scalers;
    }

    @NotNull
    public static ScalableAmount read(@NotNull FileConfig config, @NotNull String path) {
        //UniInt intialValue = UniInt.read(config, path + ".Initial");

        String initialMin = ConfigValue.create(path + ".Initial.Min", "0").read(config);
        String initialMax = ConfigValue.create(path + ".Initial.Max", "0").read(config);
        boolean initialInt = ConfigValue.create(path + ".Initial.AsInteger", false).read(config);

        Map<String, Scaler> scalers = new LinkedHashMap<>();
        config.getSection(path + ".Scalers").forEach(sId -> {
            ScaleBase scaleBase = ScaleBaseRegistry.getByName(sId);
            if (scaleBase == null) {
                ErrorHandler.error("Invalid scaler '" + sId + "'!", config, path + ".Scalers." + sId);
                return;
            }

            Scaler value = Scaler.read(config, path + ".Scalers." + sId);
            scalers.put(scaleBase.getName(), value);
        });

        return new ScalableAmount(/*intialValue, */initialMin, initialMax, initialInt, scalers);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        //this.initialValue.write(config, path + ".Initial");

        config.set(path + ".Initial.Min", this.initialMin);
        config.set(path + ".Initial.Max", this.initialMax);
        config.set(path + ".Initial.AsInteger", this.initialInteger);

        config.remove(path + ".Scalers");
        this.scalers.forEach((id, scaler) -> config.set(path + ".Scalers." + id.toUpperCase(), scaler));
    }

    public int getScaledInt(@NotNull DungeonInstance instance) {
        return (int) Math.floor(this.getScaled(instance));
    }

    public double getScaled(@NotNull DungeonInstance instance) {
        double min = NumberUtil.getAnyDouble(instance.replaceVariables().apply(this.initialMin), 0D);
        double max = NumberUtil.getAnyDouble(instance.replaceVariables().apply(this.initialMax), 0D);

        double value = this.initialInteger ? Rnd.get((int) min, (int) max) : Rnd.getDouble(min, max);//this.initialValue.roll();

        for (var entry : this.scalers.entrySet()) {
            ScaleBase scaleBase = ScaleBaseRegistry.getByName(entry.getKey());
            if (scaleBase == null) continue;

            Scaler scaler = entry.getValue();
            value = scaler.scale(instance, scaleBase, value);
        }

        return value;
    }

//    @NotNull
//    public UniInt getInitialValue() {
//        return this.initialValue;
//    }


    @NotNull
    public String getInitialMin() {
        return this.initialMin;
    }

    @NotNull
    public String getInitialMax() {
        return this.initialMax;
    }

    @NotNull
    public Map<String, Scaler> getScalers() {
        return this.scalers;
    }
}
