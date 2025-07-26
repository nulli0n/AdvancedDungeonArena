package su.nightexpress.dungeons.nms.mc_1_21_5.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonHolder;
import su.nightexpress.dungeons.api.type.MobFaction;

public class SlimeMob extends Slime implements DungeonHolder {

    public SlimeMob(@NotNull ServerLevel world) {
        super(EntityType.SLIME, world);
    }

    @Override
    protected int getJumpDelay() {
        return 8;
    }

    @Override
    protected boolean isDealsDamage() {
        return true;
    }

    @Override
    protected void doPush(Entity entity) {
        super.doPush(entity);
    }

    @Override
    public void push(Entity pusher) {
        if (!this.isValidDungeon()) return;
        if (pusher.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity bukkitMob && this.getFaction() != this.getDungeon().getMobFaction(bukkitMob)) {
            this.dealDamage((LivingEntity) pusher);
        }

        //super.push(pusher);
    }

    @Override
    public void playerTouch(Player player) {
        if (this.isValidDungeon() && this.getFaction() == MobFaction.ALLY) return;

        super.playerTouch(player);
    }
}
