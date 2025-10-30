package su.nightexpress.dungeons.nms.mc_1_21_10.mob;

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
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_1_21_10.brain.MobBrain;

public class PiglinBruteMob extends PiglinBrute implements DungeonHolder {

    public PiglinBruteMob(@NotNull ServerLevel level) {
        super(EntityType.PIGLIN_BRUTE, level);
        this.setImmuneToZombification(true);
    }

    @Override
    protected Brain.Provider<PiglinBrute> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return MobBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("piglinBruteBrain");
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
    public boolean isConverting() {
        return false;
    }

    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    protected void playAngrySound() {

    }

    @Override
    public boolean startRiding(Entity entity, boolean flag, boolean flag2) {
        return false;
    }
}
