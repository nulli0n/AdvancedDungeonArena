package su.nightexpress.dungeons;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.api.dungeon.DungeonEntityBridge;
import su.nightexpress.dungeons.command.impl.BaseCommands;
import su.nightexpress.dungeons.command.impl.KitCommands;
import su.nightexpress.dungeons.command.impl.SetupCommands;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Keys;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.dungeons.data.DataHandler;
import su.nightexpress.dungeons.dungeon.DungeonManager;
import su.nightexpress.dungeons.dungeon.DungeonSetup;
import su.nightexpress.dungeons.dungeon.criteria.registry.CriteriaRegistry;
import su.nightexpress.dungeons.dungeon.scale.ScaleBaseRegistry;
import su.nightexpress.dungeons.dungeon.script.action.ActionRegistry;
import su.nightexpress.dungeons.dungeon.script.condition.ConditionRegistry;
import su.nightexpress.dungeons.dungeon.script.number.NumberComparators;
import su.nightexpress.dungeons.dungeon.script.task.TaskRegistry;
import su.nightexpress.dungeons.hook.HookId;
import su.nightexpress.dungeons.hook.impl.McMMOHook;
import su.nightexpress.dungeons.hook.impl.PlaceholderHook;
import su.nightexpress.dungeons.kit.KitManager;
import su.nightexpress.dungeons.mob.MobManager;
import su.nightexpress.dungeons.mob.variant.MobVariantRegistry;
import su.nightexpress.dungeons.nms.DungeonNMS;
import su.nightexpress.dungeons.nms.mc_1_21_3.MC_1_21_3;
import su.nightexpress.dungeons.nms.mc_1_21_5.MC_1_21_5;
import su.nightexpress.dungeons.nms.mc_1_21_8.MC_1_21_8;
import su.nightexpress.dungeons.registry.compat.GodPluginRegistry;
import su.nightexpress.dungeons.registry.compat.BoardPluginRegistry;
import su.nightexpress.dungeons.registry.level.LevelRegistry;
import su.nightexpress.dungeons.registry.mob.MobRegistry;
import su.nightexpress.dungeons.registry.pet.PetRegistry;
import su.nightexpress.dungeons.selection.SelectionManager;
import su.nightexpress.dungeons.user.UserManager;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;

public class DungeonPlugin extends NightPlugin implements ImprovedCommands {

    private DataHandler dataHandler;
    private UserManager userManager;

    private SelectionManager selectionManager;
    private MobManager       mobManager;
    private KitManager       kitManager;
    private DungeonManager   dungeonManager;
    private DungeonSetup dungeonSetup;

    private DungeonNMS internals;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Dungeons", new String[]{"ada", "dungeon", "dungeons", "dungeonarena"})
            .setConfigClass(Config.class)
            .setLangClass(Lang.class)
            .setPermissionsClass(Perms.class);
    }

    @Override
    public void enable() {
        if (!this.loadInternals()) return;

        this.loadEngine();
        this.loadCommands();

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.selectionManager = new SelectionManager(this);
        this.selectionManager.setup();

        this.mobManager = new MobManager(this);
        this.mobManager.setup();

        this.kitManager = new KitManager(this);
        this.kitManager.setup();

        this.dungeonManager = new DungeonManager(this);
        this.dungeonManager.setup();

        this.dungeonSetup = new DungeonSetup(this);
        this.dungeonSetup.setup();

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
        if (Plugins.isInstalled(HookId.MCMMO)) {
            McMMOHook.setup();
        }
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }

        if (this.dungeonSetup != null) this.dungeonSetup.shutdown();
        if (this.dungeonManager != null) this.dungeonManager.shutdown();
        if (this.mobManager != null) this.mobManager.shutdown();
        if (this.kitManager != null) this.kitManager.shutdown();
        if (this.selectionManager != null) this.selectionManager.shutdown();

        if (this.userManager != null) this.userManager.shutdown();
        if (this.dataHandler != null) this.dataHandler.shutdown();

        NumberComparators.clear();
        ConditionRegistry.clear();
        ActionRegistry.clear();
        TaskRegistry.clear();
        ScaleBaseRegistry.clear();
        MobRegistry.clear();
        LevelRegistry.clear();
        PetRegistry.clear();
        DungeonEntityBridge.clear();
        MobVariantRegistry.clear();
        CriteriaRegistry.clear();
        GodPluginRegistry.clear();
        BoardPluginRegistry.clear();
        Keys.clear();
        DungeonsAPI.clear();
    }

    private boolean loadInternals() {
        this.internals = switch (Version.getCurrent()) {
            case MC_1_21_4 -> new MC_1_21_3();
            case MC_1_21_5 -> new MC_1_21_5();
            case MC_1_21_8 -> new MC_1_21_8();
            default -> null;
        };

        if (this.internals == null) {
            this.error("Unsupported server version.");
            this.getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    private void loadEngine() {
        DungeonsAPI.load(this);
        Keys.load(this);
        GodPluginRegistry.load(this);
        BoardPluginRegistry.load(this);
        CriteriaRegistry.load(this);
        MobRegistry.load(this);
        LevelRegistry.load(this);
        PetRegistry.load(this);
        MobVariantRegistry.load();
        NumberComparators.load();
        ConditionRegistry.load();
        ActionRegistry.load();
        TaskRegistry.load();
        ScaleBaseRegistry.load();
    }

    private void loadCommands() {
        BaseCommands.load(this);
        SetupCommands.load(this, this.getRootNode());
        KitCommands.load(this, this.getRootNode());
    }

    @NotNull
    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    @NotNull
    public UserManager getUserManager() {
        return this.userManager;
    }

    @NotNull
    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    @NotNull
    public DungeonManager getDungeonManager() {
        return this.dungeonManager;
    }

    @NotNull
    public DungeonSetup getDungeonSetup() {
        return this.dungeonSetup;
    }

    @NotNull
    public MobManager getMobManager() {
        return this.mobManager;
    }

    @NotNull
    public KitManager getKitManager() {
        return this.kitManager;
    }

    @NotNull
    public DungeonNMS getInternals() {
        return this.internals;
    }
}
