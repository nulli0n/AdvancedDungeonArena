package su.nightexpress.dungeons.selection;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Keys;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.selection.impl.CuboidSelection;
import su.nightexpress.dungeons.selection.impl.PositionSelection;
import su.nightexpress.dungeons.selection.impl.Selection;
import su.nightexpress.dungeons.selection.listener.SelectionListener;
import su.nightexpress.dungeons.selection.visual.BlockInfo;
import su.nightexpress.dungeons.selection.visual.Tracker;
import su.nightexpress.dungeons.selection.visual.highlight.BlockHighlighter;
import su.nightexpress.dungeons.selection.visual.highlight.BlockPacketsHighlighter;
import su.nightexpress.dungeons.selection.visual.highlight.BlockProtocolHighlighter;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.geodata.Cuboid;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.geodata.pos.ChunkPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SelectionManager extends AbstractManager<DungeonPlugin> {

    public static final float DISPLAY_SIZE = 0.998f;

    private final Map<UUID, Selection> selectionMap;
    private final Map<UUID, Tracker>   chunkTracker;

    private BlockHighlighter highlighter;

    public SelectionManager(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.selectionMap = new HashMap<>();
        this.chunkTracker = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadHighlighter();

        this.addListener(new SelectionListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        if (this.highlighter != null) {
            this.highlighter.clear();
            this.highlighter = null;
        }

        this.plugin.getServer().getOnlinePlayers().forEach(this::removeAll);
    }

    private void loadHighlighter() {
        if (Plugins.isInstalled(HookId.PACKET_EVENTS)) {
            this.highlighter = new BlockPacketsHighlighter(this.plugin);
        }
        else if (Plugins.isInstalled(HookId.PROTOCOL_LIB)) {
            this.highlighter = new BlockProtocolHighlighter(this.plugin);
        }
        else return;

        this.addAsyncTask(this::highlightBounds, 40L);
    }

    /**
     * Reset current player's chunk position to force trigger chunk bounds render on next task execution.
     */
    public void resetTrackedChunks() {
        this.chunkTracker.values().forEach(tracker -> tracker.setPreviousPos(null));
    }

    public void highlightBounds() {
        this.chunkTracker.forEach((uuid, tracker) -> {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player == null) return;

            Location playerLocation = player.getLocation();
            BlockPos oldPlayerPos = tracker.getPreviousPos();

            // Check if we should skip render.
            if (oldPlayerPos != null && !oldPlayerPos.isEmpty()) {
                // Always shift rendering bounds to player's Y position.
                if (Math.abs(oldPlayerPos.getY() - playerLocation.getBlockY()) < 3) {
                    // Otherwise render only if player went to other chunk.
                    ChunkPos currentChunkPos = ChunkPos.from(playerLocation);
                    ChunkPos oldChunkPos = ChunkPos.from(oldPlayerPos);

                    if (currentChunkPos.equals(oldChunkPos)) return;
                }
            }

            if (tracker.isSelection()) {
                this.highlightSelection(player);
            }

            tracker.setPreviousPos(BlockPos.from(playerLocation));
        });
    }

    public void highlightSelection(@NotNull Player player) {
        Selection selection = this.getSelection(player);
        if (selection == null) return;

        this.highlightSelection(player, selection);
    }

    public void highlightSelection(@NotNull Player player, @NotNull Selection selection) {
        if (selection instanceof CuboidSelection cuboidSelection) {
            this.highlightCuboid(player, cuboidSelection.getFirst(), cuboidSelection.getSecond());
        }
        else if (selection instanceof PositionSelection positionSelection) {
            this.removeVisuals(player);

            Set<BlockInfo> dataSet = new HashSet<>();
            positionSelection.getPositions().forEach(blockPos -> {
                dataSet.add(new BlockInfo(blockPos, Material.WHITE_STAINED_GLASS.createBlockData()));
            });

            this.highlightBlocks(player, dataSet);
        }
    }

    private void highlightCuboid(@NotNull Player player, @Nullable BlockPos min, @Nullable BlockPos max) {
        if (min == null) min = BlockPos.empty();
        if (max == null) max = BlockPos.empty();
        if (min.isEmpty() && !max.isEmpty()) min = max;
        if (max.isEmpty() && !min.isEmpty()) max = min;

        this.highlightCuboid(player, new Cuboid(min, max));
    }

    public void highlightCuboid(@NotNull Player player, @NotNull Cuboid cuboid) {
        this.highlightCuboid(player, cuboid, true/*, true*/);
    }

    public void highlightCuboid(@NotNull Player player, @NotNull Cuboid cuboid, boolean reset/*, boolean checkIntersect*/) {
        if (this.highlighter == null) return;

        if (reset) {
            this.removeVisuals(player);
        }

        World world = player.getWorld();
        Material cornerType = Material.WHITE_STAINED_GLASS;
        Material wireType = Material.CHAIN;
        Set<BlockInfo> dataSet = new HashSet<>();

        // Draw corners of the chunk/region all the time.
        this.collectBlockData(cuboid.getCorners(), dataSet, cornerType.createBlockData());
        this.collectBlockData(cuboid.getCornerWiresY(), dataSet, wireType.createBlockData());

        // Draw connections only for regions or when player is inside a chunk.
        BlockData dataX = this.createBlockData(wireType, Axis.X);
        BlockData dataZ = this.createBlockData(wireType, Axis.Z);

        this.collectBlockData(cuboid.getCornerWiresX(), dataSet, dataX);
        this.collectBlockData(cuboid.getCornerWiresZ(), dataSet, dataZ);

        // Draw all visual blocks at prepated positions with prepared block data.
        dataSet.forEach(blockInfo -> {
            BlockPos blockPos = blockInfo.getBlockPos();
            Location location = blockPos.toLocation(world);
            ChatColor color = this.getBlockColor(blockPos, cuboid);

            this.highlighter.addVisualBlock(player, location, blockInfo.getBlockData(), color, DISPLAY_SIZE);
        });
    }

    private void highlightBlocks(@NotNull Player player, @NotNull Set<BlockInfo> dataSet) {
        World world = player.getWorld();
        ChatColor color = ChatColor.GREEN;

        dataSet.forEach(blockInfo -> {
            BlockPos blockPos = blockInfo.getBlockPos();
            Location location = blockPos.toLocation(world);

            this.highlighter.addVisualBlock(player, location, blockInfo.getBlockData(), color, DISPLAY_SIZE);
        });
    }

    @NotNull
    private ChatColor getBlockColor(@NotNull BlockPos blockPos, @NotNull Cuboid cuboid) {
        ChatColor color;

        color = ChatColor.WHITE;
        if (blockPos.equals(cuboid.getMin()) || blockPos.equals(cuboid.getMax())) {
            color = ChatColor.GREEN;
        }

        return color;
    }

    private void collectBlockData(@NotNull Collection<BlockPos> source, @NotNull Set<BlockInfo> target, @NotNull BlockData data) {
        if (data.getMaterial().isAir()) return;

        source.stream().filter(blockPos -> blockPos != null && !blockPos.isEmpty()).map(blockPos -> new BlockInfo(blockPos, data)).forEach(target::add);
    }

    @NotNull
    private BlockData createBlockData(@NotNull Material material, @NotNull Axis axis) {
        BlockData data = material.createBlockData();
        if (data instanceof Orientable orientable) {
            orientable.setAxis(axis);
        }
        return data;
    }



    @NotNull
    public ItemStack getItem() {
        ItemStack itemStack = Config.ITEMS_WAND_ITEM.get().getItemStack();
        PDCUtil.set(itemStack, Keys.dungeonWand, true);
        return itemStack;
    }

    public boolean isItem(@NotNull ItemStack itemStack) {
        return PDCUtil.getBoolean(itemStack, Keys.dungeonWand).isPresent();
    }

    public void onItemUse(@NotNull Player player, @NotNull Block block, @NotNull Action action) {
        this.selectPosition(player, block.getLocation(), action);
    }

    public void onItemDrop(@NotNull Player player) {
        this.stopSelection(player);
    }

    public boolean isInSelection(@NotNull Player player) {
        return this.getSelection(player) != null;
    }

    @Nullable
    public Selection getSelection(@NotNull Player player) {
        return this.selectionMap.get(player.getUniqueId());
    }

    public void removeAll(@NotNull Player player) {
        if (this.isInSelection(player)) {
            this.stopSelection(player);
        }
        this.chunkTracker.remove(player.getUniqueId());
    }

    public void removeVisuals(@NotNull Player player) {
        if (this.highlighter != null) {
            this.highlighter.removeVisuals(player);
        }
    }

    @NotNull
    public Tracker addTracker(@NotNull Player player) {
        return this.chunkTracker.computeIfAbsent(player.getUniqueId(), k -> new Tracker());
    }

    @Nullable
    public Tracker getTracker(@NotNull Player player) {
        return this.chunkTracker.get(player.getUniqueId());
    }

    public void removeTracker(@NotNull Player player, @NotNull Consumer<Tracker> consumer) {
        Tracker tracker = this.getTracker(player);
        if (tracker == null) return;

        consumer.accept(tracker);

        if (!tracker.isSelection()) {
            this.chunkTracker.remove(player.getUniqueId());
        }
    }

    public void removeTracker(@NotNull Player player) {
        this.chunkTracker.remove(player.getUniqueId());
    }



    @NotNull
    public Selection startSelection(@NotNull Player player, @NotNull SelectionType type) {
        this.stopSelection(player);

        Selection selection = Selection.create(type);
        this.selectionMap.put(player.getUniqueId(), selection);
        this.addTracker(player).setSelection(true);

        Players.addItem(player, this.getItem());
        Lang.SETUP_SELECTION_ACTIVATED.getMessage().send(player);
        return selection;
    }

    public void stopSelection(@NotNull Player player) {
        this.removeVisuals(player);
        this.removeTracker(player, tracker -> tracker.setSelection(false));

        Players.takeItem(player, this::isItem);
        this.selectionMap.remove(player.getUniqueId());
    }

    public void selectPosition(@NotNull Player player, @NotNull Location location, @NotNull Action action) {
        Selection selection = this.getSelection(player);
        if (selection == null) return;

        BlockPos blockPos = BlockPos.from(location);

        selection.onSelect(player, blockPos, action);

        this.plugin.runTaskAsync(task -> {
            this.highlightSelection(player);
        });
    }
}
