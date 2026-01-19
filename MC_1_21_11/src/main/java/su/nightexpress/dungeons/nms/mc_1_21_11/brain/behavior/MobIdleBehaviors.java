package su.nightexpress.dungeons.nms.mc_1_21_11.brain.behavior;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.dungeon.DungeonPlayer;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;

import java.util.UUID;

public class MobIdleBehaviors {

    private static final UniformInt FOLLOW_RANGE = UniformInt.of(2, 30);

    @NotNull
    public static BehaviorControl<Mob> followOwner() {
        return BehaviorBuilder.create((builder) -> {
            return builder.group(
                    builder.absent(MemoryModuleType.ATTACK_TARGET),
                    builder.registered(MemoryModuleType.LIKED_PLAYER),
                    builder.absent(MemoryModuleType.WALK_TARGET))
                .apply(builder, (atkTarget, memLikedPlayer, memWalkTarget) -> {
                    return (world, mob, i) -> {
                        if (!(mob instanceof DungeonHolder dungeonHolder)) return false;
                        if (dungeonHolder.getFaction() != MobFaction.ALLY) return false;

                        Dungeon dungeon = dungeonHolder.getDungeon();
                        if (dungeon.getState() != GameState.INGAME) return false;
                        if (!dungeon.hasAlivePlayers()) return false;

                        UUID playerId = mob.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER).orElse(null);
                        Player likedPlayer = playerId == null ? null : world.getPlayerByUUID(playerId);

                        if (likedPlayer == null) {
                            DungeonPlayer dungeonPlayer = dungeon.getRandomAlivePlayer();
                            likedPlayer = ((CraftPlayer)dungeonPlayer.getPlayer()).getHandle();

                            memLikedPlayer.setWithExpiry(likedPlayer.getUUID(), 20L * 20L); // Follow the same player for 20s.
                        }

                        if (likedPlayer.level() != world || !dungeon.hasPlayer(likedPlayer.getUUID())) {
                            memLikedPlayer.erase();
                            return false;
                        }

                        int range = FOLLOW_RANGE.getMinValue();

                        if (!mob.closerThan(likedPlayer, range)) {
                            Vec3 target = likedPlayer.position();
                            WalkTarget walkTarget = new WalkTarget(new BlockPosTracker(target), 1F, range);
                            memWalkTarget.set(walkTarget);
                            return true;
                        }
                        return false;
                    };
                });
        });
    }
}
