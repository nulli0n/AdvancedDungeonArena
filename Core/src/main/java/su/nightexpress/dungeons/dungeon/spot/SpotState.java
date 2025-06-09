package su.nightexpress.dungeons.dungeon.spot;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.schema.SchemaBlock;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class SpotState implements Writeable {

    private final String id;

    private List<SchemaBlock> schema;

    public SpotState(@NotNull String id) {
        this.id = id.toLowerCase();
        this.schema = new ArrayList<>();
    }

    @NotNull
    public static SpotState read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        ConfigValue.create(path + ".Enabled", true).read(config);

        return new SpotState(id);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Enabled", true);
    }

    public void loadSchema(@NotNull DungeonPlugin plugin, @NotNull File file, boolean compressed) {
        this.setSchema(plugin.getInternals().loadSchema(file, compressed));
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.SPOT_STATE.replacer(this);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public List<SchemaBlock> getSchema() {
        return this.schema;
    }

    public void setSchema(@NotNull List<SchemaBlock> schema) {
        this.schema = schema;
    }
}
