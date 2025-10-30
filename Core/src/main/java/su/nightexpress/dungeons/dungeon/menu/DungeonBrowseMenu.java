package su.nightexpress.dungeons.dungeon.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.module.Features;
import su.nightexpress.dungeons.user.DungeonUser;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.NormalMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static su.nightexpress.dungeons.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class DungeonBrowseMenu extends NormalMenu<DungeonPlugin> implements Filled<DungeonConfig>, ConfigBased {

    public static final String FILE_NAME = "dungeon_browse.yml";

    private static final String COOLDOWN = "%cooldown%";

    private String       dungeonName;
    private List<String> dungeonUnlockedInfo;
    private List<String> dungeonLockedInfo;
    private List<String> dungeonCooldownInfo;

    private boolean                  gridAutoEnabled;
    private int[]                    gridAutoSlots;
    private int                      gridCustomPages;
    private Map<String, DungeonSlot> gridCustomSlots;

    public DungeonBrowseMenu(@NotNull DungeonPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X4, BLACK.wrap("Dungeons"));

        this.load(FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
    }

    @Override
    @NotNull
    public MenuFiller<DungeonConfig> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();

        return MenuFiller.builder(this)
            .setSlots(this.gridAutoSlots)
            .setItems(plugin.getDungeonManager().getDungeons().stream()
                .filter(Predicate.not(DungeonConfig::isBroken))
                .sorted(Comparator.comparing(DungeonConfig::getName))
                .toList())
            .setItemCreator(job -> this.getDungeonIcon(player, job))
            .setItemClick(job -> (viewer1, event) -> this.onDungeonClick(viewer1, job))
            .build();
    }

    @Override
    public void autoFill(@NotNull MenuViewer viewer) {
        if (this.gridAutoEnabled) {
            Filled.super.autoFill(viewer);
            return;
        }

        Player player = viewer.getPlayer();
        int page = viewer.getPage();

        this.plugin.getDungeonManager().getDungeons().forEach(dungeonConfig -> {
            if (dungeonConfig.isBroken()) return;

            DungeonSlot slot = this.gridCustomSlots.get(dungeonConfig.getId());
            if (slot == null || slot.page != page) return;

            NightItem item = this.getDungeonIcon(player, dungeonConfig);
            MenuItem menuItem = item.toMenuItem().setSlots(slot.slots).setPriority(100).setHandler((viewer1, event) -> {
                this.onDungeonClick(viewer1, dungeonConfig);
            }).build();

            viewer.addItem(menuItem);
        });
    }

    private void onDungeonClick(MenuViewer viewer, @NotNull DungeonConfig dungeonConfig) {
        Player player = viewer.getPlayer();
        this.runNextTick(() -> this.plugin.getDungeonManager().prepareForInstance(player, dungeonConfig.getInstance()));
    }

    @NotNull
    private NightItem getDungeonIcon(@NotNull Player player, @NotNull DungeonConfig config) {
        DungeonInstance dungeon = config.getInstance();
        DungeonUser user = plugin.getUserManager().getOrFetch(player);
        boolean onCooldown = user.isOnCooldown(dungeon);

        return config.getIcon()
            .hideAllComponents()
            .setDisplayName(this.dungeonName)
            .setLore(dungeon.canJoin(player, false, false) ? this.dungeonUnlockedInfo : (onCooldown ? this.dungeonCooldownInfo : this.dungeonLockedInfo))
            .replacement(replacer -> replacer
                .replace(dungeon.replacePlaceholders())
                .replace(COOLDOWN, () -> {
                    if (onCooldown) {
                        return TimeFormats.formatDuration(user.getArenaCooldown(dungeon), TimeFormatType.LITERAL);
                    }

                    Features features = config.features();
                    int cooldown = features.getEntranceCooldown().getSmallest(player).intValue();
                    if (cooldown == 0L) return CoreLang.OTHER_NONE.text();

                    return TimeFormats.formatAmount(cooldown * 1000L, TimeFormatType.LITERAL);
                })
            );
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        if (!this.gridAutoEnabled) {
            viewer.setPages(this.gridCustomPages);
        }

        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        int[] defSlots = IntStream.range(10, 17).toArray();

        this.gridAutoEnabled = ConfigValue.create("Dungeon.Grid.Auto.Enabled", true).read(config);
        this.gridAutoSlots = ConfigValue.create("Dungeon.Grid.Auto.Slots", defSlots).read(config);
        this.gridCustomPages = ConfigValue.create("Dungeon.Grid.Custom.Pages", 1).read(config);
        this.gridCustomSlots = ConfigValue.forMapById("Dungeon.Grid.Custom.Slots",
            DungeonSlot::read,
            map -> {
                map.put("your_dungeon", new DungeonSlot(1, new int[]{12,13,21,22}));
                map.put("another_dungeon", new DungeonSlot(1, new int[]{15,16,24,25}));
            }
        ).read(config);

        this.dungeonName = ConfigValue.create("Dungeon.Name",
            DUNGEON_NAME
        ).read(config);

        this.dungeonUnlockedInfo = ConfigValue.create("Dungeon.Info.Unlocked", Lists.newList(
            DUNGEON_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GRAY.wrap(WHITE.wrap("•") + " State: " + WHITE.wrap(DUNGEON_STATE)),
            GRAY.wrap(WHITE.wrap("•") + " Players: " + WHITE.wrap(DUNGEON_PLAYERS) + "/" + WHITE.wrap(DUNGEON_MAX_PLAYERS)),
            GRAY.wrap(WHITE.wrap("•") + " Enter Cost: " + WHITE.wrap(DUNGEON_ENTRANCE_PAYMENT)),
            GRAY.wrap(WHITE.wrap("•") + " Level Required: " + WHITE.wrap(DUNGEON_LEVEL_REQUIREMENT)),
            GRAY.wrap(WHITE.wrap("•") + " Cooldown: " + WHITE.wrap(COOLDOWN)),
            "",
            LIGHT_YELLOW.wrap("[▶] " + LIGHT_GRAY.wrap("Click to") + " enter" + LIGHT_GRAY.wrap("."))
        )).read(config);

        this.dungeonLockedInfo = ConfigValue.create("Dungeon.Info.Locked", Lists.newList(
            DUNGEON_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GRAY.wrap(LIGHT_RED.wrap("✘") + " Upgrade your " + LIGHT_RED.wrap("/rank") + " to unlock this dungeon!")
        )).read(config);

        this.dungeonCooldownInfo = ConfigValue.create("Dungeon.Info.Cooldown", Lists.newList(
            DUNGEON_DESCRIPTION,
            EMPTY_IF_ABOVE,
            GRAY.wrap(LIGHT_RED.wrap("✘") + " Dungeon is on cooldown: " + LIGHT_RED.wrap(COOLDOWN))
        )).read(config);

        loader.addDefaultItem(MenuItem.buildExit(this, 31));
        loader.addDefaultItem(MenuItem.buildNextPage(this, 32).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 30).setPriority(10));
    }

    private static class DungeonSlot implements Writeable {

        private final int page;
        private final int[] slots;

        public DungeonSlot(int page, int[] slots) {
            this.page = page;
            this.slots = slots;
        }

        @NotNull
        public static DungeonSlot read(@NotNull FileConfig config, @NotNull String path) {
            int page = ConfigValue.create(path + ".Page", 1).read(config);
            int[] slots = ConfigValue.create(path + ".Slots", new int[0]).read(config);

            return new DungeonSlot(page, slots);
        }

        @Override
        public void write(@NotNull FileConfig config, @NotNull String path) {
            config.set(path + ".Page", this.page);
            config.setIntArray(path + ".Slots", this.slots);
        }
    }
}
