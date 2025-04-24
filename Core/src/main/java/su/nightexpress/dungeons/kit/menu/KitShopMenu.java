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
import su.nightexpress.dungeons.kit.KitUtils;
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
import su.nightexpress.nightcore.ui.menu.item.ItemOptions;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.dungeons.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

@SuppressWarnings("UnstableApiUsage")
public class KitShopMenu extends LinkedMenu<DungeonPlugin, DungeonInstance> implements Filled<Kit>, ConfigBased {

    public static final String FILE_NAME = "kit_shop.yml";

//    private static final String EFFECTS         = "%potions%";
//    private static final String ATTRIBUTES      = "%attributes%";
    private static final String AVAILABLE_INFO  = "%info_available%";
    private static final String PERMISSION_INFO = "%info_permission%";
    private static final String PURCHASE_INFO   = "%info_purchase%";

    protected String       kitName;
    protected List<String> kitLore;
    protected int[]        kitSlots;
//    protected List<String> potionsLore;
//    protected List<String> attributesLore;
    protected List<String> kitInfoAvailable;
    protected List<String> kitInfoPermission;
    protected List<String> kitInfoPurchase;

    public KitShopMenu(@NotNull DungeonPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X4, BLACK.wrap("Kit Shop"));

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
        //GameSettings gameSettings = dungeon.getConfig().gameSettings();

        return MenuFiller.builder(this)
            .setSlots(this.kitSlots)
            .setItems(plugin.getKitManager().getKits().stream()
                .filter(kit -> KitUtils.isRentMode() || (!user.hasKit(kit) && kit.hasCost()))
                .sorted(Comparator.comparing(Kit::getId)).toList())
            .setItemCreator(kit -> {

//                List<String> potions;
//                List<String> attributes;
                List<String> availability;
                List<String> permissionInfo;
                List<String> purchaseInfo;

//                if (kit.hasPotionEffects()) {
//                    potions = Replacer.create()
//                        .replace(GENERIC_ENTRY, list -> list.addAll(kit.getPotionEffects().stream().map(UIUtils::formatPotionEffectEntry).toList()))
//                        .apply(this.potionsLore);
//                }
//                else potions = Collections.emptyList();
//
//                if (kit.hasAttributes()) {
//                    attributes = Replacer.create()
//                        .replace(GENERIC_ENTRY, list -> list.addAll(kit.getAttributeMap().entrySet().stream().map(entry -> UIUtils.formatAttributeEntry(entry.getKey(), entry.getValue())).toList()))
//                        .apply(this.attributesLore);
//                }
//                else attributes = Collections.emptyList();

                if (!dungeon.isKitAllowed(kit)) {
                    availability = Replacer.create()
                        .replace(dungeon.replacePlaceholders())
                        .apply(this.kitInfoAvailable);
                }
                else availability = Collections.emptyList();

                if (!kit.hasPermission(player)) {
                    permissionInfo = new ArrayList<>(this.kitInfoPermission);
                    purchaseInfo = Collections.emptyList();
                }
                else {
                    permissionInfo = Collections.emptyList();
                    purchaseInfo = new ArrayList<>(this.kitInfoPurchase);
                }

                return kit.getIcon()
                    .setDisplayName(this.kitName)
                    .setLore(this.kitLore)
                    .setHideComponents(true)
                    .replacement(replacer -> replacer
//                        .replace(EFFECTS, potions)
//                        .replace(ATTRIBUTES, attributes)
                        .replace(AVAILABLE_INFO, availability)
                        .replace(PERMISSION_INFO, permissionInfo)
                        .replace(PURCHASE_INFO, purchaseInfo)
                        .replace(kit.replacePlaceholders())
                    );
            })
            .setItemClick(kit -> (viewer1, event) -> {
                if (event.isLeftClick()) {
                    if (!kit.hasPermission(player)) return;

                    this.runNextTick(() -> {
                        if (KitUtils.isPurchaseMode()) {
                            UIUtils.openConfirmation(player, Confirmation.builder()
                                .setIcon(kit.getIcon().localized(Lang.UI_CONFIRMATION_KIT_PURCHASE).replacement(replacer -> replacer.replace(kit.replacePlaceholders())))
                                .onAccept((viewer2, event1) -> plugin.getKitManager().purchase(player, kit))
                                .onReturn((viewer2, event1) -> this.runNextTick(() -> plugin.getKitManager().openShop(player, dungeon)))
                                .build());
                        }
                        else {
                            UIUtils.openConfirmation(player, Confirmation.builder()
                                .setIcon(dungeon.getConfig().getIcon()
                                    .localized(Lang.UI_CONFIRMATION_DUNGEON_ENTER_RENT_KIT)
                                    .replacement(replacer -> replacer.replace(dungeon.replacePlaceholders()).replace(kit.replacePlaceholders())))
                                .onAccept((viewer2, event1) -> plugin.getDungeonManager().enterInstance(player, dungeon, kit))
                                .onReturn((viewer2, event1) -> this.runNextTick(() -> plugin.getKitManager().openShop(player, dungeon)))
                                .returnOnAccept(false)
                                .build());
                        }
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

        this.kitLore = ConfigValue.create("Kit.Lore", Lists.newList(
            GRAY.wrap("Cost: " + YELLOW.wrap(KIT_COST)),
            EMPTY_IF_BELOW,
            KIT_DESCRIPTION,
            EMPTY_IF_ABOVE,
            LIGHT_YELLOW.wrap(BOLD.wrap("Stats & Effects:")),
            KIT_ATTRIBUTES,
            KIT_EFFECTS,
            EMPTY_IF_BELOW,
            AVAILABLE_INFO,
            PERMISSION_INFO,
            EMPTY_IF_ABOVE,
            PURCHASE_INFO,
            LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("R-Click to " + LIGHT_YELLOW.wrap("preview") + ".")
        )).read(config);

        this.kitSlots = ConfigValue.create("Kit.Slots",
            new int[] {2,3,4,5,6, 11,12,13,14,15, 21,22,23}
        ).read(config);

//        this.potionsLore = ConfigValue.create("Kit.Details.Potions", Lists.newList(
//            LIGHT_YELLOW.wrap(BOLD.wrap("Effects:")),
//            GENERIC_ENTRY
//        )).read(config);
//
//        this.attributesLore = ConfigValue.create("Kit.Details.Attributes", Lists.newList(
//            LIGHT_YELLOW.wrap(BOLD.wrap("Attributes:")),
//            GENERIC_ENTRY
//        )).read(config);

        this.kitInfoAvailable = ConfigValue.create("Kit.Info.Availability", Lists.newList(
            LIGHT_RED.wrap("✘ Not available for " + LIGHT_YELLOW.wrap(DUNGEON_NAME) + " dungeon.")
        )).read(config);

        this.kitInfoPermission = ConfigValue.create("Kit.Info.Permission", Lists.newList(
            LIGHT_RED.wrap("✘ Upgrade your " + LIGHT_YELLOW.wrap("/rank") + " to unlock this kit.")
        )).read(config);

        this.kitInfoPurchase = ConfigValue.create("Kit.Info.Purchase", Lists.newList(
            LIGHT_YELLOW.wrap("[▶] ") + LIGHT_GRAY.wrap("L-Click to " + LIGHT_YELLOW.wrap("purchase") + ".")
        )).read(config);

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE).toMenuItem()
            .setSlots(0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 20, 24));

        loader.addDefaultItem(MenuItem.buildNextPage(this, 17).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 9).setPriority(10));
        loader.addDefaultItem(MenuItem.buildExit(this, 40).setPriority(10));

        loader.addDefaultItem(NightItem.asCustomHead("8373515aa9b60537d0a46fa66912f999865bb95ceb02d7c5e2725d414ddb49d")
            .setDisplayName(LIGHT_YELLOW.wrap(BOLD.wrap("My Kits")))
            .setLore(Lists.newList(
                LIGHT_GRAY.wrap("Click to browse all your kits.")
            ))
            .toMenuItem()
            .setPriority(10)
            .setSlots(33)
            .setHandler(new ItemHandler("kit_selection", (viewer, event) -> {
                this.runNextTick(() -> plugin.getKitManager().openSelector(viewer.getPlayer(), this.getLink(viewer)));
            }, ItemOptions.builder().setVisibilityPolicy(viewer -> KitUtils.isPurchaseMode()).build())));

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
