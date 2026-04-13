package su.nightexpress.dungeons.nms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.schema.SchemaBlock;
import su.nightexpress.dungeons.api.type.MobFaction;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public interface DungeonNMS {

    boolean isSupportedMob(@NotNull EntityType type);

    @Nullable LivingEntity spawnMob(@NotNull Dungeon dungeon, @NotNull EntityType type, @NotNull MobFaction faction, @NotNull Location location, @NotNull Consumer<LivingEntity> function);

    @Nullable EntityType getSpawnEggType(@NotNull ItemStack itemStack);

    //void setBlockStateFromTag(@NotNull Block block, @NotNull Object compoundTag);

    void setSchemaBlock(@NotNull World world, @NotNull SchemaBlock schemaBlock);

    @NotNull List<SchemaBlock> loadSchema(@NotNull File file, boolean compressed);

    void saveSchema(@NotNull World world, @NotNull List<Block> blocks, @NotNull File file);
}
