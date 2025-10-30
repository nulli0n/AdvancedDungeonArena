package su.nightexpress.dungeons.nms.mc_1_21_10;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R6.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R6.block.CraftBlock;
import org.bukkit.craftbukkit.v1_21_R6.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_21_R6.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.schema.SchemaBlock;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.nms.DungeonNMS;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.goal.FollowPlayersGoal;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.goal.LastDamagerTargetGoal;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.goal.NearestFactionTargetGoal;
import su.nightexpress.nightcore.util.Reflex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MC_1_21_10 implements DungeonNMS {

    @Override
    public boolean isSupportedMob(@NotNull EntityType type) {
        return EntityCreator.isSupported(type);
    }

    @Override
    @Nullable
    public EntityType getSpawnEggType(@NotNull ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        if (!(nmsStack.getItem() instanceof SpawnEggItem eggItem)) return null;

        net.minecraft.world.entity.EntityType<?> type = eggItem.getType(nmsStack);
        return type == null ? null : CraftEntityType.minecraftToBukkit(type);
    }

    @Override
    public LivingEntity spawnMob(@NotNull Dungeon dungeon, @NotNull EntityType type, @NotNull MobFaction faction, @NotNull Location location, @NotNull Consumer<LivingEntity> function) {
        ServerLevel level = ((CraftWorld) dungeon.getWorld()).getHandle();

        net.minecraft.world.entity.Mob mob = EntityCreator.createEntity(/*dungeon, faction, */type, level);
        if (mob == null) return null;

        LivingEntity bukkitEntity = (LivingEntity) mob.getBukkitEntity();

        this.registerAttribute(mob, Attributes.ARMOR);
        this.registerAttribute(mob, Attributes.ARMOR_TOUGHNESS);
        this.registerAttribute(mob, Attributes.ATTACK_DAMAGE);
        this.registerAttribute(mob, Attributes.ATTACK_KNOCKBACK);
        this.registerAttribute(mob, Attributes.ATTACK_SPEED);
        this.setAttribute(mob, Attributes.FOLLOW_RANGE, 256D);
        this.registerAttribute(mob, Attributes.FLYING_SPEED);
        this.registerAttribute(mob, Attributes.JUMP_STRENGTH);
        this.registerAttribute(mob, Attributes.KNOCKBACK_RESISTANCE);
        this.registerAttribute(mob, Attributes.MAX_HEALTH);
        this.registerAttribute(mob, Attributes.MOVEMENT_SPEED);

        if (mob.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) == 0) {
            this.setAttribute(mob, Attributes.ATTACK_DAMAGE, 1);
        }

        boolean isAlly = faction == MobFaction.ALLY;

        if (mob instanceof PathfinderMob pathfinderMob) {
            if (isAlly && !EntityCreator.isCustom(type)) {
                mob.goalSelector.addGoal(6, new FollowPlayersGoal(mob, dungeon));
            }

            if (bukkitEntity instanceof Animals || bukkitEntity instanceof org.bukkit.entity.IronGolem) {
                mob.goalSelector.getAvailableGoals().clear();
                mob.goalSelector.addGoal(0, new FloatGoal(mob));
                mob.goalSelector.addGoal(2, new su.nightexpress.dungeons.nms.mc_1_21_10.brain.goal.MeleeAttackGoal(pathfinderMob, dungeon, faction));
                mob.goalSelector.addGoal(8, new LookAtPlayerGoal(pathfinderMob, net.minecraft.world.entity.player.Player.class, 8.0F));
            }
            else {
                if (mob instanceof net.minecraft.world.entity.monster.Drowned drowned) {
                    drowned.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof ZombieAttackGoal);
                    drowned.goalSelector.addGoal(3, new ZombieAttackGoal(drowned, 1D, false));
                }
                else if (mob instanceof EnderMan ender) {
                    ender.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getPriority() == 1); // EndermanFreezeWhenLookedAt
                }
                pathfinderMob.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> {
                    var goal = wrappedGoal.getGoal();
                    return goal instanceof FleeSunGoal || goal instanceof AvoidEntityGoal<?> || goal instanceof RestrictSunGoal;
                });
            }
        }

        if (!EntityCreator.isCustom(type)) {
            mob.targetSelector.getAvailableGoals().clear();
            mob.targetSelector.addGoal(1, new LastDamagerTargetGoal(mob, dungeon, faction));
            mob.targetSelector.addGoal(2, new NearestFactionTargetGoal(mob, dungeon, faction));
            mob.setAggressive(true);
        }

        function.accept(bukkitEntity);

        level.addFreshEntity(mob, null);
        mob.snapTo(location.getX(), location.getY(), location.getZ());

        return bukkitEntity;
    }

    private void registerAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Holder<Attribute> att) {
        AttributeInstance instance = handle.getAttribute(att);

        if (instance == null) {
            // Hacks to register missing entity's attributes.
            AttributeSupplier provider = (AttributeSupplier) Reflex.getFieldValue(handle.getAttributes(), "e");
            if (provider == null) return;

            @SuppressWarnings("unchecked")
            Map<Holder<Attribute>, AttributeInstance> aMap = (Map<Holder<Attribute>, AttributeInstance>) Reflex.getFieldValue(provider, "a");
            if (aMap == null) return;

            Map<Holder<Attribute>, AttributeInstance> aMap2 = new HashMap<>(aMap);
            aMap2.put(att, new AttributeInstance(att, var1 -> {

            }));
            Reflex.setFieldValue(provider, "a", aMap2);
        }
    }

    private void setAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Holder<Attribute> attribute, double value) {
        this.registerAttribute(handle, attribute);

        AttributeInstance instance = handle.getAttribute(attribute);
        if (instance == null) return;

        instance.setBaseValue(value);
    }

    @Override
    public void setSchemaBlock(@NotNull World world, @NotNull SchemaBlock schemaBlock) {
        ServerLevel level = ((CraftWorld)world).getHandle();

        CraftBlock craftBlock = (CraftBlock) schemaBlock.getBlockPos().toLocation(world).getBlock();
        craftBlock.setBlockData(schemaBlock.getBlockData());

        if (schemaBlock.getNbt() instanceof CompoundTag tag) {
            BlockPos blockPos = craftBlock.getPosition();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity == null) return;

            //entityState.loadData(tag);
            //entityState.update(true, false);

            // Load NBT data directly to NMS block.
            // CraftBlockEntityState#load wipes out some data, for example CraftSign overrides #load() and wipes out sign text.
            blockEntity.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag));
            blockEntity.setChanged();
        }
    }

    @NotNull
    @Override
    public List<SchemaBlock> loadSchema(@NotNull File file, boolean compressed) {
        List<SchemaBlock> schemaBlocks = new ArrayList<>();
        CompoundTag schemTag;

        try {
            schemTag = compressed ? NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap()) : NbtIo.read(file.toPath());
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return schemaBlocks;
        }
        if (schemTag == null) {
            return schemaBlocks;
        }

        ListTag blocksTag = schemTag.getListOrEmpty("blocks");

        blocksTag.forEach(tag -> {
            CompoundTag blockTag = (CompoundTag) tag;

            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, blockTag.getCompound("state").orElseThrow());

            ListTag posTag = blockTag.getListOrEmpty("pos");
            BlockPos pos = new BlockPos(posTag.getIntOr(0, 0), posTag.getIntOr(1, 0), posTag.getIntOr(2, 0));
            CompoundTag nbt = blockTag.getCompound("nbt").orElse(null);

            CraftBlockData craftBlockData = CraftBlockData.fromData(state);
            su.nightexpress.nightcore.util.geodata.pos.BlockPos blockPos = new su.nightexpress.nightcore.util.geodata.pos.BlockPos(pos.getX(), pos.getY(), pos.getZ());

            schemaBlocks.add(new SchemaBlock(blockPos, craftBlockData, nbt));
        });

        return schemaBlocks;
    }

    @Override
    public void saveSchema(@NotNull World world, @NotNull List<Block> blocks, @NotNull File file) {
        ServerLevel level = ((CraftWorld) world).getHandle();

        CompoundTag root = new CompoundTag();
        ListTag blocksTag = new ListTag();

        for (Block block : blocks) {
            CraftBlockState craftState = (CraftBlockState) block.getState();
            BlockPos blockPos = craftState.getPosition();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);

            CompoundTag blockTag = new CompoundTag();
            blockTag.put("state", NbtUtils.writeBlockState(craftState.getHandle()));
            blockTag.put("pos", this.newIntegerList(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            if (blockEntity != null) {
                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());//TagValueOutput.createWithContext(reporter, level.registryAccess());
                blockEntity.saveWithId(output);
                blockTag.put("nbt", output.buildResult());
            }
            blocksTag.add(blockTag);
        }

        root.put("blocks", blocksTag);

        try {
            NbtIo.writeCompressed(root, file.toPath());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @NotNull
    private ListTag newIntegerList(int... arr) {
        ListTag tag = new ListTag();

        for (int value : arr) {
            tag.add(IntTag.valueOf(value));
        }

        return tag;
    }
}
