package su.nightexpress.dungeons.nms.mc_1_21_5.mob;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.MobBrain;

public class WardenMob extends Warden implements DungeonHolder {

    public WardenMob(@NotNull ServerLevel level) {
        super(EntityType.WARDEN, level);
    }

    @Override
    protected Brain.Provider<Warden> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return MobBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("wardenBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        MobBrain.updateActivity(this, this.brain);
    }

    @Override
    protected void doPush(Entity entity) {
        if (entity.getBukkitEntity() instanceof LivingEntity bukkitMob && this.getFaction() == this.getDungeon().getMobFaction(bukkitMob)) {
            entity.push(this);
            return;
        }
        super.doPush(entity);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return MobAI.hurt(this, damageSource, fixed -> super.hurtServer(level, fixed, amount));
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }
}
