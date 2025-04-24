package su.nightexpress.dungeons.dungeon.spot;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.function.UnaryOperator;

public class Spot extends AbstractFileData<DungeonPlugin> {

    private final Map<String, SpotState> stateByIdMap;

    private String    name;
    //private Cuboid cuboid;
    private String defaultStateId;
    private String lastState;

    public Spot(@NotNull DungeonPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.stateByIdMap = new HashMap<>();
        this.defaultStateId = Placeholders.DEFAULT;
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setName(config.getString("Name", this.getId()));
        this.setDefaultStateId(config.getString("DefaultState", Placeholders.DEFAULT));

//        BlockPos min = BlockPos.read(config, "Bounds.Min");
//        BlockPos max = BlockPos.read(config, "Bounds.Max");
//        this.setCuboid(new Cuboid(min, max));

        for (String stateId : config.getSection("States")) {
            SpotState state = SpotState.read(config, "States." + stateId, stateId);
            this.addState(state);
            this.loadStateSchema(state);
        }

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.name);
        config.set("DefaultState", this.defaultStateId);
//        this.cuboid.getMin().write(config, "Bounds.Min");
//        this.cuboid.getMax().write(config, "Bounds.Max");

        config.set("States", null);
        this.getStates().forEach(state -> {
            config.set("States." + state.getId(), state);
        });
    }

    public void build(@NotNull World world, @NotNull SpotState state) {
        state.getSchema().forEach(schemaBlock -> {
            this.plugin.getInternals().setSchemaBlock(world, schemaBlock);
        });
    }

    public void loadStateSchemas() {
        this.getStates().forEach(this::loadStateSchema);
    }

    public void loadStateSchema(@NotNull SpotState state) {
        File file = this.getStateSchemaFile(state);
        if (!file.exists()) return;

        state.loadSchema(this.plugin, file);
    }

    public void writeStateSchema(@NotNull SpotState state, @NotNull World world, @NotNull List<Block> blocks) {
        File file = this.getStateSchemaFile(state);
        FileUtil.create(file);

        this.plugin.getInternals().saveSchema(world, blocks, file);
    }

    public void addStateOrUpdate(@NotNull SpotState state, @NotNull World world, @NotNull List<Block> blocks) {
        this.removeState(state);
        this.addState(state);
        this.writeStateSchema(state, world, blocks);
        this.loadStateSchema(state);
    }

    public void removeState(@NotNull SpotState state) {
        this.stateByIdMap.remove(state.getId());

        File file = this.getStateSchemaFile(state);
        if (file.exists()) {
            file.delete();
        }
    }

    public void removeStates() {
        this.getStates().forEach(this::removeState);
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.SPOT.replacer(this);
    }

    @NotNull
    public File getStateSchemaFile(@NotNull SpotState state) {
        String name = this.getId() + "_" + state.getId() + ".schema";
        return new File(this.file.getAbsoluteFile().getParent(), name);
    }

    @Nullable
    public SpotState getDefaultState() {
        return this.getState(this.defaultStateId);
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getDefaultStateId() {
        return this.defaultStateId;
    }

    public void setDefaultStateId(@NotNull String defaultStateId) {
        this.defaultStateId = defaultStateId;
    }

//    @NotNull
//    @Deprecated
//    public Cuboid getCuboid() {
//        return this.cuboid;
//    }
//
//    @Deprecated
//    public void setCuboid(@Nullable Cuboid cuboid) {
//        this.cuboid = cuboid;
//    }

    public void addState(@NotNull SpotState state) {
        this.stateByIdMap.put(state.getId(), state);
    }

    @NotNull
    public Map<String, SpotState> getStateByIdMap() {
        return this.stateByIdMap;
    }

    @NotNull
    public Set<SpotState> getStates() {
        return new HashSet<>(this.stateByIdMap.values());
    }

    @Nullable
    public SpotState getState(@NotNull String id) {
        return this.stateByIdMap.get(id.toLowerCase());
    }

    @NotNull
    public String getLastState() {
        return this.lastState == null ? this.defaultStateId : this.lastState;
    }

    public void setLastState(@Nullable SpotState lastState) {
        this.lastState = lastState == null ? null : lastState.getId();
    }
}
