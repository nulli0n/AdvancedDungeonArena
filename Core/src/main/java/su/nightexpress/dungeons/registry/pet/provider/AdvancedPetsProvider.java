package su.nightexpress.dungeons.registry.pet.provider;

import net.advancedplugins.pets.api.APAPI;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.registry.pet.PetProvider;

public class AdvancedPetsProvider implements PetProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.ADVANCED_PETS.toLowerCase();
    }

    @Override
    public boolean isPet(@NotNull LivingEntity entity) {
        return APAPI.isPlayerPet(entity);
    }
}
