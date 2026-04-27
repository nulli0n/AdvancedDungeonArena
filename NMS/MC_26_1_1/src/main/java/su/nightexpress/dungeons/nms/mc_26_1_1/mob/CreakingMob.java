package su.nightexpress.dungeons.nms.mc_26_1_1.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobBrain;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobBrainConfigurable;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.behavior.MobCoreBehaviors;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.behavior.MobFightBehaviors;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.behavior.MobIdleBehaviors;

public class CreakingMob extends Creaking implements DungeonHolder, MobBrainConfigurable<Creaking> {

    public CreakingMob(@NotNull Level level) {
        super(EntityType.CREAKING, level);
    }

    protected Brain.Provider<Creaking> brainProvider() {
        return MobBrain.brainProvider(this, CreakingMob::getActivities);
    }

    @Override
    protected Brain<Creaking> makeBrain(final Brain.Packed packedBrain) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(this, packedBrain));
    }

    @NotNull
    private Brain<Creaking> refreshBrain(@NotNull Creaking mob, @NotNull Brain<Creaking> brain) {
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        setBrain(brain);
        return brain;
    }

    public static ImmutableList<ActivityData<Creaking>> getActivities(Creaking pet) {
        return ImmutableList.of(
                ActivityData.create(Activity.CORE, 0, ImmutableList.of(
                        MobCoreBehaviors.lookAtTarget(),
                        MobCoreBehaviors.moveToTarget(),
                        MobCoreBehaviors.swim(),
                        MobFightBehaviors.stopAngryIfTargetDead())
                ),
                ActivityData.create(Activity.IDLE, 10, ImmutableList.of(
                        MobIdleBehaviors.followOwner(),
                        MobFightBehaviors.autoTargetAndAttack())
                ),
                ActivityData.create(Activity.FIGHT, 10, ImmutableList.of(
                        MobFightBehaviors.stopAttackIfTargetInvalid(pet),
                        MobFightBehaviors.reachTargetWhenOutOfRange(),
                        MobFightBehaviors.meleeAttack())
                )
        );
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("creakingBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        MobBrain.updateActivity(this, this.brain);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return MobAI.hurt(this, damageSource, fixed -> super.hurtServer(level, fixed, amount));
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void setBrain(@NotNull Brain<Creaking> brain) {
        this.brain = brain;
    }
}

