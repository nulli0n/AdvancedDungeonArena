package su.nightexpress.dungeons.registry.pet;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface PetProvider {

    @NotNull String getName();

    boolean isPet(@NotNull LivingEntity entity);
}
