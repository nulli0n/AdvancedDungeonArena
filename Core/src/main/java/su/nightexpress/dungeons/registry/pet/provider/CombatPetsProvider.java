package su.nightexpress.dungeons.registry.pet.provider;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.registry.pet.PetProvider;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;

public class CombatPetsProvider implements PetProvider {

    @NotNull
    @Override
    public String getName() {
        return "combatpets";
    }

    @Override
    public boolean isPet(@NotNull LivingEntity entity) {
        return PetEntityBridge.isPet(entity);
    }
}
