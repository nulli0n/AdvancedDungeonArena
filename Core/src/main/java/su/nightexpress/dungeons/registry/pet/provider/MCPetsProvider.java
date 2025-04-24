package su.nightexpress.dungeons.registry.pet.provider;

import fr.nocsy.mcpets.api.MCPetsAPI;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.registry.pet.PetProvider;

public class MCPetsProvider implements PetProvider {

    @NotNull
    @Override
    public String getName() {
        return "mcpets";
    }

    @Override
    public boolean isPet(@NotNull LivingEntity entity) {
        return MCPetsAPI.getObjectPets().stream().anyMatch(pet -> {
            ActiveMob activeMob = pet.getActiveMob();
            return activeMob != null && activeMob.getEntity().getBukkitEntity() == entity;
        });
    }
}
