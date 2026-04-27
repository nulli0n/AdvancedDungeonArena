package su.nightexpress.dungeons.nms.mc_26_1_1.brain;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface MobBrainConfigurable<E extends Mob> {

    void setBrain(@NotNull Brain<E> brain);

    static <E extends Mob> @NotNull Optional<MobBrainConfigurable<E>> optionalCast(@NotNull Mob mob) {
        if (mob instanceof MobBrainConfigurable) {
            //noinspection unchecked
            return Optional.of((MobBrainConfigurable<E>) mob);
        }
        return Optional.empty();
    }
}
