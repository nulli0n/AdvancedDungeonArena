package su.nightexpress.dungeons.nms.mc_1_21_5.mob;

import com.mojang.serialization.Dynamic;
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
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.MobAI;
import su.nightexpress.dungeons.nms.mc_1_21_5.brain.MobBrain;

public class ZoglinMob extends Zoglin implements DungeonHolder {

    public ZoglinMob(@NotNull ServerLevel level) {
        super(EntityType.ZOGLIN, level);
    }

    @Override
    protected Brain.Provider<Zoglin> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return MobBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
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
}
