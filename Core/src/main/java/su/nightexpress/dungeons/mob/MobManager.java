package su.nightexpress.dungeons.mob;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.api.dungeon.Dungeon;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.mob.impl.MobTemplate;
import su.nightexpress.dungeons.util.MobUitls;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class MobManager extends AbstractManager<DungeonPlugin> {

    private final Map<String, MobTemplate> templateByIdMap;

    public MobManager(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.templateByIdMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.createDefaults();
        this.loadMobTemplates();
    }

    private void createDefaults() {
        MobCreator.create(this.plugin);
    }

    private void loadMobTemplates() {
        for (File file : FileUtil.getConfigFiles(plugin.getDataFolder() + Config.DIR_MOBS)) {
            MobTemplate template = new MobTemplate(plugin, file);
            if (template.load()) {
                this.templateByIdMap.put(template.getId(), template);
            }
            else this.plugin.warn("Mob template not loaded: '" + file.getName() + "'!");
        }
        this.plugin.info("Loaded " + this.templateByIdMap.size() + " mob templates.");
    }

    @Override
    protected void onShutdown() {
        this.templateByIdMap.clear();
    }

    @NotNull
    public List<String> getTemplateIds() {
        return new ArrayList<>(this.templateByIdMap.keySet());
    }

    @NotNull
    public Map<String, MobTemplate> getTemplateByIdMap() {
        return this.templateByIdMap;
    }

    @NotNull
    public Set<MobTemplate> getTemplates() {
        return new HashSet<>(this.templateByIdMap.values());
    }

    @Nullable
    public MobTemplate getTemplateById(@NotNull String id) {
        return this.templateByIdMap.get(id.toLowerCase());
    }

    @Nullable
    public MobTemplate getTemplateByEntity(@NotNull LivingEntity entity) {
        String mobId = MobUitls.getTemplateId(entity);
        return mobId == null ? null : this.getTemplateById(mobId);
    }

    @Nullable
    public LivingEntity spawnIternalMob(@NotNull Dungeon dungeon, @NotNull MobFaction faction, @NotNull String mobId, @NotNull Location location, int level, @Nullable Consumer<LivingEntity> prespawn) {
        MobTemplate template = this.getTemplateById(mobId);
        if (template == null) return null;

        EntityType type = template.getEntityType();

        return this.plugin.getInternals().spawnMob(dungeon, type, faction, location, entity -> {
            template.applySettings(entity, level);
            template.applyAttributes(entity, level);
            entity.setHealth(EntityUtil.getAttribute(entity, Attribute.MAX_HEALTH)); // Heal to restore modified max. health.
            if (prespawn != null) {
                prespawn.accept(entity);
            }
            MobUitls.setTemplate(entity, template);
        });
    }
}
