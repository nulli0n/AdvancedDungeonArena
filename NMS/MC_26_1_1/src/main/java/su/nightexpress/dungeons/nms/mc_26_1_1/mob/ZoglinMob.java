package su.nightexpress.dungeons.nms.mc_26_1_1.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobBrain;
import su.nightexpress.dungeons.nms.mc_26_1_1.brain.MobBrainConfigurable;

public class ZoglinMob extends Zoglin implements DungeonHolder, MobBrainConfigurable<Zoglin> {

    public ZoglinMob(@NotNull ServerLevel level) {
        super(EntityType.ZOGLIN, level);
    }

    protected Brain.Provider<Zoglin> brainProvider() {
        return MobBrain.brainProvider(this, MobBrain::getActivities);
    }

    @Override
    protected Brain<Zoglin> makeBrain(final Brain.Packed packedBrain) {
        return MobBrain.refreshBrain(this, this.brainProvider().makeBrain(this, packedBrain));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("zoglinBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        MobBrain.updateActivity(this, this.brain);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return MobAI.hurt(this, source, fixed -> super.hurtServer(level, source, amount));
    }

    @Override
    protected void playAngrySound() {

    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void setBrain(@NotNull Brain<Zoglin> brain) {
        this.brain = brain;
    }
}
