package su.nightexpress.dungeons.dungeon.script.task.type;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import su.nightexpress.dungeons.api.dungeon.DungeonPlayer;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.event.game.DungeonGameEvent;
import su.nightexpress.dungeons.dungeon.event.DungeonEventType;
import su.nightexpress.dungeons.dungeon.script.task.Task;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

import java.util.*;

public abstract class AreaTask implements Task {

    protected final Map<String, Set<BlockDisplay>> blockLights;

    protected final int      radius;
    protected final int      height;
    protected final BlockPos targetPos;

    public AreaTask(int radius, int height, @NotNull BlockPos targetPos) {
        this.blockLights = new HashMap<>();

        this.radius = radius;
        this.height = height;
        this.targetPos = targetPos;
    }

    protected interface Creator<T extends AreaTask> {

        @NotNull T create(int radius, int height, @NotNull BlockPos targetPos);
    }

    @NotNull
    protected static <T extends AreaTask> T load(@NotNull FileConfig config, @NotNull String path, @NotNull Creator<T> creator) {
        int radius = ConfigValue.create(path + ".Radius", 5).read(config);
        int height = ConfigValue.create(path + ".Height", 5).read(config);
        BlockPos pos = BlockPos.read(config, path + ".Location");

        return creator.create(radius, height, pos);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Radius", this.radius);
        config.set(path + ".Height", this.height);
        config.set(path + ".Location", this.targetPos.serialize());
    }

    @Override
    public boolean canBePerPlayer() {
        return false;
    }

    @Override
    public void onTaskAdd(@NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        List<Block> blocks = this.getCircleBlocks(dungeon);
        blocks.forEach(block -> {
            dungeon.getWorld().spawn(block.getLocation(), BlockDisplay.class, display -> {
                display.setBlock(block.getBlockData());
                display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.999f, 0.999f, 0.999f), new AxisAngle4f()));
                display.setGlowing(true);
                display.setGlowColorOverride(Color.RED);
                display.setPersistent(false);
                this.blockLights.computeIfAbsent(stageTask.getId(), k -> new HashSet<>()).add(display);
            });
        });
    }

    @Override
    public void onTaskRemove(@NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        Set<BlockDisplay> displays = this.blockLights.remove(stageTask.getId());
        if (displays != null) {
            displays.forEach(Entity::remove);
        }
    }

    protected abstract void onTaskProgress(@NotNull DungeonGameEvent event, @NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress);

    @Override
    public void progress(@NotNull DungeonGameEvent event, @NotNull DungeonInstance dungeon, @NotNull StageTask stageTask, @NotNull TaskProgress progress) {
        if (event.getType() != DungeonEventType.DUNGEON_TICK) return;

        this.onTaskProgress(event, dungeon, stageTask, progress);
    }

    protected void setAreaColor(@NotNull StageTask stageTask, @NotNull Color color) {
        this.blockLights.getOrDefault(stageTask.getId(), Collections.emptySet()).forEach(display -> display.setGlowColorOverride(color));
    }

    @NotNull
    protected List<Block> getCircleBlocks(@NotNull DungeonInstance dungeon) {
        World world = dungeon.getWorld();
        int fixedY = this.targetPos.getY();

        List<Block> blocks = new ArrayList<>();

        int centerX = this.targetPos.getX();
        int centerZ = this.targetPos.getZ();

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                if (this.isInsideCircle(x, z)) {
                    Block block = world.getBlockAt(x, fixedY, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    protected boolean isInside(@NotNull DungeonPlayer gamer) {
        Player player = gamer.getPlayer();
        Location location = player.getLocation();
        int yDiff = Math.abs(location.getBlockY() - this.targetPos.getY());
        if (yDiff > this.height) return false;

        return this.isInsideCircle(location.getBlockX(), location.getBlockZ());
    }

    protected boolean isInsideCircle(int x, int z) {
        int dx = this.targetPos.getX() - x;
        int dz = this.targetPos.getZ() - z;
        return (dx * dx + dz * dz) <= (this.radius * this.radius);
    }
}
