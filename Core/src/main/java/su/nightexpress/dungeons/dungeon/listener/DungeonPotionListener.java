package su.nightexpress.dungeons.dungeon.listener;

import com.google.common.collect.Sets;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.dungeon.DungeonManager;
import su.nightexpress.nightcore.manager.AbstractListener;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiFunction;

public class DungeonPotionListener extends AbstractListener<DungeonPlugin> {

    private final DungeonManager manager;

    private final Map<LivingEntity, ThrownPotion>    affectedByPotion;
    private final Map<LivingEntity, AreaEffectCloud> affectedByCloud;

    private static final Set<PotionEffectType> POSITIVE_EFFECTS = Sets.newHashSet(
        PotionEffectType.ABSORPTION, PotionEffectType.RESISTANCE,
        PotionEffectType.HASTE, PotionEffectType.FIRE_RESISTANCE,
        PotionEffectType.INSTANT_HEALTH, PotionEffectType.HEALTH_BOOST,
        PotionEffectType.STRENGTH, PotionEffectType.INVISIBILITY,
        PotionEffectType.JUMP_BOOST, PotionEffectType.LUCK,
        PotionEffectType.NIGHT_VISION, PotionEffectType.REGENERATION,
        PotionEffectType.SATURATION, PotionEffectType.SPEED, PotionEffectType.WATER_BREATHING
    );

    public static boolean isPositiveEffect(@NotNull PotionEffectType type) {
        return POSITIVE_EFFECTS.contains(type);
    }

    public DungeonPotionListener(@NotNull DungeonPlugin plugin, @NotNull DungeonManager manager) {
        super(plugin);
        this.manager = manager;
        this.affectedByPotion = new WeakHashMap<>();
        this.affectedByCloud = new WeakHashMap<>();
    }

    private boolean checkPotionFaction(@NotNull LivingEntity entity, @NotNull BiFunction<MobFaction, MobFaction, Boolean> function) {
        LivingEntity shooter = null;

        ThrownPotion potion = this.affectedByPotion.get(entity);
        if (potion != null && potion.getShooter() instanceof LivingEntity damager) {
            shooter = damager;
        }

        AreaEffectCloud cloud = this.affectedByCloud.get(entity);
        if (cloud != null && cloud.getSource() instanceof LivingEntity damager) {
            shooter = damager;
        }

        if (shooter == null) return false;

        MobFaction shooterFaction = this.manager.getFaction(shooter);
        MobFaction victimFaction = this.manager.getFaction(entity);

        return function.apply(shooterFaction, victimFaction);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaPotionSplashNormal(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        event.getAffectedEntities().forEach(entity -> {
            if (this.manager.getFaction(entity) != null) {
                this.affectedByPotion.put(entity, potion);
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaPotionSplashCloud(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();

        event.getAffectedEntities().forEach(entity -> {
            if (this.manager.getFaction(entity) != null) {
                this.affectedByCloud.put(entity, cloud);
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaPotionEffect(EntityPotionEffectEvent event) {
        if (event.getCause() != EntityPotionEffectEvent.Cause.POTION_SPLASH && event.getCause() != EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        boolean isGoodEffect = isPositiveEffect(event.getModifiedType());

        event.setCancelled(this.checkPotionFaction(entity, (shooterFaction, victimFaction) -> isGoodEffect != (shooterFaction == victimFaction)));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArenaDamagePotion(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.MAGIC) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        event.setCancelled(this.checkPotionFaction(entity, (shooterFaction, victimFaction) -> shooterFaction == victimFaction));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArenaHealPotion(EntityRegainHealthEvent event) {
        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        event.setCancelled(this.checkPotionFaction(entity, (shooterFaction, victimFaction) -> shooterFaction != victimFaction));
    }
}
