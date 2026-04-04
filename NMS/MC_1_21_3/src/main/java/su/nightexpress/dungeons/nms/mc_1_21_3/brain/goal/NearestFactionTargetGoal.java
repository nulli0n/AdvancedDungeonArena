package su.nightexpress.dungeons.nms.mc_1_21_3.brain.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.nms.mc_1_21_3.brain.MobAI;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class NearestFactionTargetGoal extends TargetGoal {

    private final Dungeon    arena;
    private final MobFaction faction;
    private int unseenTicks;

    public NearestFactionTargetGoal(@NotNull Mob mob, @NotNull Dungeon arena, @NotNull MobFaction faction) {
        super(mob, false, true);
        this.setFlags(EnumSet.of(Flag.TARGET));

        this.arena = arena;
        this.faction = faction;
    }

    public boolean canUse() {
        this.findTarget();
        return this.targetMob != null;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            target = this.targetMob;
        }
        if (target == null) return false;
        if (this.arena.getMobFaction((org.bukkit.entity.LivingEntity) target.getBukkitEntity()) == this.faction) return false;
        if (!this.mob.canAttack(target)) return false;

        if (this.mustSee) {
            if (this.mob.getSensing().hasLineOfSight(target)) {
                this.unseenTicks = 0;
            }
            else if (++this.unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) {
                return false;
            }
        }

        LivingEntity nearest = MobAI.getNearestTarget(this.mob, this.faction, this.arena);
        if (target != nearest) return false;

        this.setTarget(nearest);

        // PaperMC removed boolean arg from this method, very thanks :/
        //this.mob.setTarget(target);//, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        return true;
    }

    protected void findTarget() {
        this.targetMob = MobAI.getNearestTarget(this.mob, this.faction, this.arena);
    }

    public void start() {
        this.unseenTicks = 0;

        // PaperMC removed boolean arg from this method, very thanks :/
        //EntityTargetEvent.TargetReason reason = this.faction == MobFaction.ENEMY ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY;
        //this.mob.setTarget(this.target, reason, true);
        this.setTarget(this.targetMob);
        super.start();
    }

    private void setTarget(@Nullable LivingEntity target) {
        if (target == null) return;

        org.bukkit.entity.Mob bukkitMob = (org.bukkit.entity.Mob) this.mob.getBukkitEntity();
        bukkitMob.setTarget((org.bukkit.entity.LivingEntity) target.getBukkitEntity());
    }
}
