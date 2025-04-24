package su.nightexpress.dungeons.kit.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.user.DungeonUser;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.UIUtils;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.confirmation.Confirmation;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.ItemHandler;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.dungeons.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

@SuppressWarnings("UnstableApiUsage")
public class KitSelectMenu extends LinkedMenu<DungeonPlugin, DungeonInstance> implements Filled<Kit>, ConfigBased {

    public static final String FILE_NAME = "kit_selection.yml";

    private String       kitName;
    private List<String> kitLore;
    private List<String> kitLoreNotAvailable;
    private List<String> kitLoreNoPermission;
    private List<String> kitLoreMaxUses;
    private int[]        kitSlots;

    public KitSelectMenu(@NotNull DungeonPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X4, BLACK.wrap("Kit Selection"));

        this.load(FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    public MenuFiller<Kit> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        DungeonUser user = plugin.getUserManager().getOrFetch(player);
        DungeonInstance dungeon = this.getLink(player);

        return MenuFiller.builder(this)
            .setSlots(this.kitSlots)
            .setItems(plugin.getKitManager().getKits().stream()
                .filter(kit -> user.hasKit(kit) || !kit.hasCost())
                .filter(kit -> {
                    if (!dungeon.isKitAllowed(kit) && Config.KITS_HIDE_DISABLED_KITS.get()) return false;
                    return kit.hasPermission(player) || !Config.KITS_HIDE_LOCKED_KITS.get();
                })
                .sorted(Comparator.comparing(Kit::getId)).toList())
            .setItemCreator(kit -> {
                List<String> lore = new ArrayList<>();
                if (!dungeon.isKitAllowed(kit)) {
                    lore.addAll(this.kitLoreNotAvailable);
                }
                else if (dungeon.isKitLimitReached(kit)) {
                    lore.addAll(this.kitLoreMaxUses);
                }
                else if (!kit.hasPermission(player)) {
                    lore.addAll(this.kitLoreNoPermission);
                }
                else lore.addAll(this.kitLore);

                return kit.getIcon()
                    .setDisplayName(this.kitName)
                    .setLore(lore)
                    .setHideComponents(true)
                    .replacement(replacer -> replacer
                        .replace(dungeon.replacePlaceholders())
                        .replace(kit.replacePlaceholders())
                    );
            })
            .setItemClick(kit -> (viewer1, event) -> {
                if (event.isLeftClick()) {
                    if (!kit.hasPermission(player)) return;
                    if (!dungeon.isKitAllowed(kit)) return;
                    if (dungeon.isKitLimitReached(kit)) return;

                    this.runNextTick(() -> {
                        UIUtils.openConfirmation(player, Confirmation.builder()
                            .setIcon(dungeon.getConfig().getIcon()
                                .localized(Lang.UI_CONFIRMATION_DUNGEON_ENTER_OWN_KIT)
                                .replacement(replacer -> replacer.replace(dungeon.replacePlaceholders()).replace(kit.replacePlaceholders())))
                            .onAccept((viewer2, event1) -> {
                                plugin.getDungeonManager().enterInstance(player, dungeon, kit);
                                plugin.runTask(task -> player.closeInventory());
                            })
                            .onReturn((viewer2, event1) -> {
                                plugin.runTask(task -> plugin.getKitManager().openSelector(player, dungeon));
                            })
                            .returnOnAccept(false)
                            .build());
                    });
                }
                else if (event.isRightClick()) {
                    this.runNextTick(() -> plugin.getKitManager().openPreview(player, kit, dungeon));
                }
            })
            .build();
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.kitName = ConfigValue.create("Kit.Name", KIT_NAME).read(config);

        this.kitLore = ConfigValue.create("Kit.Lore.Unlocked", Lists.newList(
            KIT_DESCRIPTION,
            EMPTY_IF_ABOVE,
            LIGHT_YELLOW.wrap(BOLD.wrap("Stats & Effects:")),
            KIT_ATTRIBUTES,
            KIT_EFFECTS,
            EMPTY_IF_ABOVE,
            LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("L-Click to " + LIGHT_YELLOW.wrap("select") + "."),
            LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("R-Click to " + LIGHT_YELLOW.wrap("preview") + ".")
        )).read(config);

        this.kitLoreNotAvailable = ConfigValue.create("Kit.Lore.NotAvailable", Lists.newList(
            LIGHT_RED.wrap("✘ Not available for " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " dungeon.")
        )).read(config);

        this.kitLoreNoPermission = ConfigValue.create("Kit.Lore.NoPermission", Lists.newList(
            LIGHT_RED.wrap("✘ Upgrade your " + LIGHT_YELLOW.wrap("/rank") + " to unlock this kit.")
        )).read(config);

        this.kitLoreMaxUses = ConfigValue.create("Kit.Lore.MaxUses", Lists.newList(
            LIGHT_RED.wrap("✘ There are already too many players with this kit.")
        )).read(config);

        this.kitSlots = ConfigValue.create("Kit.Slots",
            new int[] {2,3,4,5,6, 11,12,13,14,15, 21,22,23}
        ).read(config);

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE).toMenuItem()
            .setSlots(0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 20, 24));

        loader.addDefaultItem(MenuItem.buildNextPage(this, 17).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 9).setPriority(10));
        loader.addDefaultItem(MenuItem.buildExit(this, 40).setPriority(10));

        loader.addDefaultItem(NightItem.asCustomHead("663029cc8167897e6535a3c5734bbabaff188d0905f9d9353afac62a06dadf86")
            .setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("Kit Shop")))
            .setLore(Lists.newList(
                LIGHT_GRAY.wrap("Click to purchase more kits.")
            ))
            .toMenuItem()
            .setPriority(10)
            .setSlots(33)
            .setHandler(new ItemHandler("kit_shop", (viewer, event) -> {
                this.runNextTick(() -> plugin.getKitManager().openShop(viewer.getPlayer(), this.getLink(viewer)));
            })));

        loader.addDefaultItem(NightItem.asCustomHead("76d126affd03def502bfaa91a34e7c1562421490002a85c2b5815bdd4248e12")
            .setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("To Dungeons")))
            .setLore(Lists.newList(
                LIGHT_GRAY.wrap("Click to go back to dungeons.")
            ))
            .toMenuItem()
            .setPriority(10)
            .setSlots(29)
            .setHandler(new ItemHandler("dungeons", (viewer, event) -> {
                this.runNextTick(() -> plugin.getDungeonManager().browseDungeons(viewer.getPlayer()));
            })));
    }
}
