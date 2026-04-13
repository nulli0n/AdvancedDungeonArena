package su.nightexpress.dungeons.nms.mc_26_1_1.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobBrain;

public class WardenMob extends Warden implements DungeonHolder {

    public WardenMob(@NotNull ServerLevel level) {
        super(EntityType.WARDEN, level);
    }

    protected Brain.Provider<Warden> brainProvider() {
        return MobBrain.brainProvider(this, MobBrain::getActivities);
    }

    @Override
    protected Brain<Warden> makeBrain(final Brain.Packed packedBrain) {
        return MobBrain.refreshBrain(this, this.brainProvider().makeBrain(this, packedBrain));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        this.getBrain().tick(level, this);
        MobBrain.updateActivity(this, this.brain);
    }

    @Override
    protected void doPush(Entity entity) {
        if (entity.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity bukkitMob && this.getFaction() == this.getDungeon().getMobFaction(bukkitMob)) {
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
