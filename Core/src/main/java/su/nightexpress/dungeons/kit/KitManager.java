package su.nightexpress.dungeons.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Keys;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.user.DungeonUser;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.kit.menu.KitPreviewMenu;
import su.nightexpress.dungeons.kit.menu.KitSelectMenu;
import su.nightexpress.dungeons.kit.menu.KitShopMenu;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class KitManager extends AbstractManager<DungeonPlugin> {

    private final Map<String, Kit> kitByIdMap;

    private KitPreviewMenu previewMenu;
    private KitSelectMenu selectMenu;
    private KitShopMenu   shopMenu;

    public KitManager(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.kitByIdMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadKits();
        this.loadUI();

        this.addListener(new KitListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        this.kitByIdMap.clear();

        if (this.previewMenu != null) this.previewMenu.clear();
        if (this.selectMenu != null) this.selectMenu.clear();
        if (this.shopMenu != null) this.shopMenu.clear();
    }

    private void loadKits() {
        File dir = new File(this.getKitsPath());
        if (!dir.exists() && dir.mkdirs()) {
            this.loadDefaultKits();
            return;
        }

        for (File file : FileUtil.getConfigFiles(dir.getAbsolutePath())) {
            Kit kit = new Kit(this.plugin, file);
            this.loadKit(kit);
        }
        this.plugin.info("Loaded " + this.kitByIdMap.size() + " kits.");
    }

    private boolean loadKit(@NotNull Kit kit) {
        if (!kit.load()) {
            this.plugin.error("Kit not loaded: '" + kit.getFile().getPath() + "'.");
            return false;
        }

        this.kitByIdMap.put(kit.getId(), kit);
        return true;
    }

    private void loadDefaultKits() {
        this.createKit("tank", KitUtils::setTankKit);
        this.createKit("warrior", KitUtils::setWarriorKit);
        this.createKit("archer", KitUtils::setArcherKit);
        this.createKit("support", KitUtils::setSupportKit);
        this.createKit("priest", KitUtils::setPriestKit);
        this.createKit("assasin", KitUtils::setAssasinKit);
        this.createKit("bomber", KitUtils::setBomberKit);
        this.createKit("pyro", KitUtils::setPyroKit);
    }

    private void loadUI() {
        this.previewMenu = new KitPreviewMenu(this.plugin);
        this.selectMenu = new KitSelectMenu(this.plugin);
        this.shopMenu = new KitShopMenu(this.plugin);
    }

    public boolean hasAnyAccess(@NotNull Player player) {
        DungeonUser user = plugin.getUserManager().getOrFetch(player);
        if (user.getPurchasedKits().stream().anyMatch(has -> this.getKitById(has) != null)) {
            return true;
        }

        return this.getKits().stream().anyMatch(Predicate.not(Kit::hasCost));
    }

    public boolean createKit(@NotNull Player player, @NotNull String name) {
        String id = StringUtil.transformForID(name);
        if (id.isBlank()) {
            Lang.SETUP_ERROR_INVALID_NAME.getMessage().send(player);
            return false;
        }

        //PlayerInventory inventory = player.getInventory();

        this.createKit(id, kit -> {
            kit.setIcon(NightItem.fromType(Material.GOLDEN_SWORD));
            //KitUtils.setKitContent(kit, inventory);
            Lang.KIT_CREATE_DONE_NEW.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()));
        });
        return true;
    }

    public void updateKit(@NotNull Player player, @NotNull Kit kit) {
        PlayerInventory inventory = player.getInventory();

        KitUtils.setKitContent(kit, inventory);
        kit.save();
        Lang.KIT_CREATE_DONE_UPDATE.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()));
    }

    private void createKit(@NotNull String id, @NotNull Consumer<Kit> consumer) {
        File file = new File(this.getKitsPath(), id + ".yml");
        Kit kit = new Kit(this.plugin, file);
        kit.setName(StringUtil.capitalizeUnderscored(id));
        kit.setDescription(new ArrayList<>());
        consumer.accept(kit);
        kit.save();

        this.loadKit(kit);
    }

    public boolean deleteKit(@NotNull Kit kit) {
        if (!kit.getFile().delete()) return false;

        this.kitByIdMap.remove(kit.getId());
        return true;
    }

    @NotNull
    public String getKitsPath() {
        return this.plugin.getDataFolder() + Config.DIR_KITS;
    }

    @NotNull
    public KitPreviewMenu getPreviewMenu() {
        return this.previewMenu;
    }

    @NotNull
    public KitSelectMenu getSelectMenu() {
        return this.selectMenu;
    }

    @NotNull
    public KitShopMenu getShopMenu() {
        return this.shopMenu;
    }

    public boolean isKitExists(@NotNull String id) {
        return this.getKitById(id) != null;
    }

    @NotNull
    public Map<String, Kit> getKitByIdMap() {
        return this.kitByIdMap;
    }

    @NotNull
    public Set<Kit> getKits() {
        return new HashSet<>(this.kitByIdMap.values());
    }

    @NotNull
    public List<String> getKitIds() {
        return new ArrayList<>(this.kitByIdMap.keySet());
    }

    @Nullable
    public Kit getKitById(@NotNull String id) {
        return this.kitByIdMap.get(id.toLowerCase());
    }

    @Nullable
    public Kit getKitByItem(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.kitItem).orElse(null);
        return id == null ? null : this.getKitById(id);
    }

    public void openSelector(@NotNull Player player, @NotNull DungeonInstance dungeon) {
        this.selectMenu.open(player, dungeon);
    }

    public void openShop(@NotNull Player player, @NotNull DungeonInstance dungeon) {
        this.shopMenu.open(player, dungeon);
    }

    public void openPreview(@NotNull Player player, @NotNull Kit kit, @NotNull DungeonInstance dungeon) {
        this.previewMenu.open(player, kit, dungeon);
    }

    public boolean purchase(@NotNull Player player, @NotNull Kit kit) {
        DungeonUser user = this.plugin.getUserManager().getOrFetch(player);
        if (user.hasKit(kit)) {
            return false;
        }

        if (!kit.canAfford(player)) {
            Lang.KIT_BUY_ERROR_INSUFFICIENT_FUNDS.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()));
            return false;
        }

        kit.takeCosts(player);

        Lang.KIT_BUY_SUCCESS.getMessage().send(player, replacer -> replacer.replace(kit.replacePlaceholders()));
        user.addKit(kit);
        this.plugin.getUserManager().save(user);
        return true;
    }
}
