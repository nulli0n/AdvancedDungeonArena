package su.nightexpress.dungeons.dungeon.game;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.dungeon.*;
import su.nightexpress.dungeons.api.mob.MobIdentifier;
import su.nightexpress.dungeons.api.mob.MobProvider;
import su.nightexpress.dungeons.api.type.GameResult;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.api.type.MobFaction;
import su.nightexpress.dungeons.config.Config;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.dungeons.dungeon.config.DungeonConfig;
import su.nightexpress.dungeons.dungeon.event.DungeonEventReceiver;
import su.nightexpress.dungeons.dungeon.event.game.*;
import su.nightexpress.dungeons.dungeon.event.normal.DungeonEndEvent;
import su.nightexpress.dungeons.dungeon.event.normal.DungeonStartedEvent;
import su.nightexpress.dungeons.dungeon.feature.KillStreak;
import su.nightexpress.dungeons.dungeon.feature.LevelRequirement;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.lootchest.LootChest;
import su.nightexpress.dungeons.dungeon.mob.DungeonMob;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.dungeon.player.PlayerSnapshot;
import su.nightexpress.dungeons.dungeon.reward.GameReward;
import su.nightexpress.dungeons.dungeon.reward.Reward;
import su.nightexpress.dungeons.dungeon.spot.Spot;
import su.nightexpress.dungeons.dungeon.spot.SpotState;
import su.nightexpress.dungeons.dungeon.stage.Stage;
import su.nightexpress.dungeons.dungeon.stage.StageTask;
import su.nightexpress.dungeons.dungeon.stage.task.TaskProgress;
import su.nightexpress.dungeons.kit.KitUtils;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.registry.mob.MobRegistry;
import su.nightexpress.dungeons.user.DungeonUser;
import su.nightexpress.dungeons.util.DungeonUtils;
import su.nightexpress.dungeons.util.ErrorHandler;
import su.nightexpress.dungeons.util.MobUitls;
import su.nightexpress.economybridge.EconomyBridge;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.language.message.OutputType;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class DungeonInstance implements Dungeon {

    private final DungeonPlugin   plugin;
    private final DungeonConfig   config;
    private final DungeonCounters counters;
    private final DungeonVariables variables;

    private final List<DungeonEventReceiver>   eventReceivers;
    private final Map<StageTask, TaskProgress> taskProgress;
    private final Map<UUID, DungeonGamer>      players;
    private final Map<UUID, DungeonMob>        mobByIdMap;
    private final Set<Item>                    groundItems;

    private World world;
    private long  tickCount;

    private GameState  state;
    private GameResult gameResult;
    private int  countdown;
    private long timeLeft;

    private Level level;
    private Stage stage;
    private boolean stageCompleted;

    public DungeonInstance(@NotNull DungeonPlugin plugin, @NotNull DungeonConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.counters = new DungeonCounters(this);
        this.variables = new DungeonVariables();
        this.eventReceivers = new ArrayList<>(); // List to keep receivers order.
        this.taskProgress = new LinkedHashMap<>(); // Linked to keep tasks order.
        this.players = new HashMap<>();
        this.mobByIdMap = new HashMap<>();
        this.groundItems = new HashSet<>();

        this.reset();
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> Placeholders.DUNGEON_INSTANCE.replacer(this).apply(this.replaceVariables().apply(str));
    }

    @NotNull
    public UnaryOperator<String> replaceVariables() {
        return this.variables.replacePlaceholders();
    }

    private void reset() {
        this.killGroundItems();
        this.killMobs();
        this.removeTasks();
        this.resetSpotStates();
        this.clearLootChests();

        this.counters.clear();
        this.variables.clear();

        this.taskProgress.clear();
        this.players.clear();
        this.mobByIdMap.clear();
        this.tickCount = 0L;
        this.countdown = this.config.gameSettings().getLobbyTime();

        this.level = null;
        this.stage = null;
        this.stageCompleted = false;
        this.state = GameState.WAITING;
        this.eventReceivers.clear();
    }

    public void stop() {
        if (this.state == GameState.INGAME) {
            DungeonEndEvent event = new DungeonEndEvent(this, this.gameResult);
            this.plugin.getPluginManager().callEvent(event);

            if (this.config.gameSettings().isEndAnnouncement()) {
                Lang.DUNGEON_ANNOUNCE_END.getMessage().broadcast(replacer -> replacer.replace(this.replacePlaceholders()));
            }
        }

        this.getPlayers().forEach(this::leavePlayer);

        this.reset();
    }

    public boolean activate() {
        World world = Bukkit.getWorld(this.config.getWorldName());
        if (world == null) return false;

        this.activate(world);
        return true;
    }

    public void activate(@NotNull World world) {
        if (this.config.getWorldName().equalsIgnoreCase(world.getName())) {
            this.world = world;
            this.plugin.debug("Dungeon " + this.getId() + " activated.");
        }
    }

    public void deactivate(@NotNull World world) {
        if (this.config.getWorldName().equalsIgnoreCase(world.getName())) {
            this.deactivate();
        }
    }

    public void deactivate() {
        this.gameResult = GameResult.NONE;
        this.stop();
        this.world = null;
        this.plugin.debug("Dungeon " + this.getId() + " deactivated.");
    }

    @Override
    @NotNull
    public World getWorld() {
        if (this.world == null) throw new IllegalStateException("Dungeon world is not loaded! You must check Dungeon#isActive before calling this method.");

        return this.world;
    }

    public boolean isActive() {
        return this.world != null && !this.config.isBroken();
    }

    private void updateListeners() {
        this.eventReceivers.clear();
        if (this.level != null) {
            this.eventReceivers.add(this.level);
        }
        if (this.stage != null) {
            this.eventReceivers.add(this.stage);
        }
    }

    public void tick() {
        if (!this.isActive()) return;

        if (this.state == GameState.INGAME) {
            this.tickGame();

            if (this.state != GameState.INGAME) return;

            DungeonGameEvent tickEvent = new DungeonTickEvent(this);
            this.broadcastEvent(tickEvent);

            this.tickCount++;
        }
        else {
            this.tickLobby();
        }

        this.getPlayers().forEach(DungeonGamer::tick);
        this.showStatus();
    }

    public void tickLobby() {
        boolean readyToStart = this.isReadyToStart();

        if (this.state == GameState.WAITING) {
            if (readyToStart) {
                this.state = GameState.READY;

                if (this.config.gameSettings().isStartAnnouncement()) {
                    Players.getOnline().forEach(player -> {
                        if (this.plugin.getDungeonManager().isPlaying(player)) return;
                        if (!this.hasPermission(player)) return;

                        Lang.DUNGEON_ANNOUNCE_START.getMessage().send(player, replacer -> replacer
                            .replace(this.replacePlaceholders())
                            .replace(Placeholders.GENERIC_TIME, this.countdown));
                    });
                }
            }

            return;
        }

        if (!readyToStart) {
            this.state = GameState.WAITING;
            this.setCountdown(this.config.gameSettings().getLobbyTime());
            // this.updateSigns();
            return;
        }

        Set<DungeonGamer> players = this.getPlayers();
        boolean allReady = players.stream().allMatch(DungeonPlayer::isReady);

        // Drop countdown timer to a specific value when all players are ready to fight.
        int dropTo = Config.DUNGEON_LOBBY_DROP_TIMER.get();
        if (dropTo > 0 && this.countdown > dropTo && allReady) {
            this.setCountdown(dropTo);
        }

        if (this.countdown <= 0) {
            this.state = GameState.INGAME;
            this.setLevel(this.config.getStartLevel());
            this.setStage(this.config.getStartStage());
            players.forEach(this::spawnPlayer);
            this.countdown = -1;
            this.setTimeLeft(this.config.gameSettings().hasTimeleft() ? this.config.gameSettings().getTimeleft() * 60L : -1L);

            DungeonStartedEvent event = new DungeonStartedEvent(this);
            this.plugin.getPluginManager().callEvent(event);
            return;
        }

        this.countdown--;
    }

    public void tickGame() {
        if (!this.config.gameSettings().isItemPickupAllowed()) {
            this.burnGroundItems();
        }

        this.eliminateDeadMobs();

        if (this.isAboutToEnd()) {
            if (this.countdown-- <= 0) {
                this.stop();
            }
            return;
        }

        if (!this.hasAlivePlayers()) {
            this.setCountdown(Config.DUNGEON_COUNTDOWN_DEFEAT.get(), GameResult.DEFEAT);
            this.broadcast(Lang.DUNGEON_END_ALL_DEAD, replacer -> replacer.replace(this.replacePlaceholders()));
            return;
        }

        if (this.timeLeft > 0) {
            if (--this.timeLeft == 0) {
                this.setCountdown(Config.DUNGEON_COUNTDOWN_DEFEAT.get(), GameResult.DEFEAT);
                this.broadcast(Lang.DUNGEON_END_TIMEOUT, replacer -> replacer.replace(this.replacePlaceholders()));
                return;
            }
        }

        if (this.isTasksCompleted() && !this.stageCompleted) {
            this.handleStageEnd();
        }
    }

    private void broadcastEvent(@NotNull DungeonGameEvent event) {
        this.plugin.getPluginManager().callEvent(event);
        if (event instanceof Cancellable cancellable && cancellable.isCancelled()) return;

        // Copy to prevent new stage/levels to handle that event if they were changed during it.
        List<DungeonEventReceiver> receivers = new ArrayList<>(this.eventReceivers);

        receivers.forEach(receiver -> receiver.onDungeonEventBroadcastReceive(event, event.getType(), this));

        // Extra set to call finish events for only tasks completed in the first loop
        // and to prevent ConcurrentModificationException since TaskFinishEvent may produce new tasks added by script actions.
        Set<StageTask> completedTasks = new HashSet<>();

        this.taskProgress.forEach((stageTask, progress) -> {
            if (progress.isCompleted()) return;

            stageTask.getTask().progress(event, this, stageTask, progress);

            if (progress.isCompleted()) completedTasks.add(stageTask);
        });

        // Call task finish events in a different loop to prevent event-in-event recursion and associated issues.
        completedTasks.forEach(stageTask -> {
            var progress = this.taskProgress.get(stageTask);
            if (progress == null) return;

            this.broadcastEvent(new DungeonTaskFinishedEvent(this, stageTask, progress));
            this.broadcast(Lang.DUNGEON_TASK_COMPLETED_INFO, replacer -> replacer
                .replace(this.replacePlaceholders())
                .replace(Placeholders.GENERIC_NAME, stageTask.getParams().getDisplay())
            );
        });
    }

    public void broadcast(@NotNull LangText lang, @NotNull Consumer<Replacer> consumer) {
        this.getPlayers().forEach(player -> this.getPrefixed(lang).send(player.getPlayer(), consumer));
    }

    public void broadcast(@NotNull LangText lang, @NotNull BiConsumer<Player, Replacer> consumer) {
        this.getPlayers().forEach(gamer -> this.getPrefixed(lang).send(gamer.getPlayer(), replacer -> consumer.accept(gamer.getPlayer(), replacer)));
    }

    public void broadcast(@NotNull String message) {
        this.getPlayers().forEach(player -> Players.sendModernMessage(player.getPlayer(), message));
    }

    public void sendMessage(@NotNull Player player, @NotNull LangText lang, @NotNull Consumer<Replacer> consumer) {
        this.getPrefixed(lang).send(player, consumer);
    }

    public void runCommand(@NotNull List<String> commands, @NotNull DungeonTarget target, @Nullable DungeonGameEvent event) {
        //String command = this.replacePlaceholders().apply(cmd);

        if (target == DungeonTarget.GLOBAL) {
            commands.forEach(command -> {
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command);
            });
            return;
        }

        this.runForPlayers(target, event, gamer -> Players.dispatchCommands(gamer.getPlayer(), commands));
    }

    public void giveReward(@NotNull GameReward reward, boolean instant, @NotNull DungeonTarget target, @Nullable DungeonGameEvent event) {
        if (target == DungeonTarget.GLOBAL) {
            ErrorHandler.error("Reward must have player-specific target, not " + target.name() + ".", this);
            return;
        }

        this.runForPlayers(target, event, gamer -> this.giveReward(gamer, reward, instant));
    }

    public void giveReward(@NotNull DungeonGamer gamer, @NotNull GameReward gameReward, boolean instant) {
        Reward reward = gameReward.getReward();
        Player player = gamer.getPlayer();

        if (instant) reward.give(this, gamer);
        else gamer.addReward(gameReward);

        this.sendMessage(player, Lang.DUNGEON_GAME_REWARD_RECEIVED, replacer -> replacer.replace(this.replacePlaceholders()).replace(reward.replacePlaceholders()));
    }

    private void runForPlayers(@NotNull DungeonTarget target, @Nullable DungeonGameEvent event, @NotNull Consumer<DungeonGamer> consumer) {
        if (target == DungeonTarget.EVENT_PLAYER) {
            if (!(event instanceof GamerEvent gamerEvent)) return;

            DungeonGamer gamer = gamerEvent.getGamer();
            if (gamer != null) {
                consumer.accept(gamer);
            }
            return;
        }

        this.getPlayers().forEach(gamer -> {
            if (!gamer.isDead() || target == DungeonTarget.ALL_PLAYERS) {
                consumer.accept(gamer);
            }
        });
    }

    @NotNull
    private LangMessage getPrefixed(@NotNull LangText lang) {
        return lang.getMessage().getOptions().getOutputType() == OutputType.CHAT ? lang.getMessage().setPrefix(this.config.getPrefix()) : lang.getMessage();
    }

    private void showStatus() {
        switch (this.state) {
            case WAITING -> this.broadcast(Lang.DUNGEON_STATUS_LOBBY_WAITING, replacer -> replacer
                .replace(Placeholders.GENERIC_CURRENT, String.valueOf(this.countPlayers()))
                .replace(Placeholders.GENERIC_MIN, String.valueOf(this.config.gameSettings().getMinPlayers()))
            );
            case READY -> {
                boolean isClose = this.countdown <= 10;
                this.broadcast((isClose ? Lang.DUNGEON_STATUS_LOBBY_READY_CLOSE : Lang.DUNGEON_STATUS_LOBBY_READY_FAR), replacer -> replacer
                    .replace(Placeholders.GENERIC_TIME, TimeFormats.toDigitalShort(TimeUnit.SECONDS.toMillis(this.countdown))));
            }
            case INGAME -> {
                if (!this.isAboutToEnd()) return;

                this.broadcast((this.gameResult == GameResult.VICTORY ? Lang.DUNGEON_STATUS_ENDING_VICTORY : Lang.DUNGEON_STATUS_ENDING_DEFEAT), replacer -> replacer
                    .replace(Placeholders.GENERIC_TIME, TimeFormats.toDigitalShort(TimeUnit.SECONDS.toMillis(this.countdown))));
            }
        }
    }

    private void spawnPlayer(@NotNull DungeonGamer gamer) {
        Player player = gamer.getPlayer();

        Kit kit = gamer.getKit();
        if (this.isKitsMode() && kit != null) {
            kit.applyPotionEffects(player);
            kit.applyAttributeModifiers(player);
        }

        player.teleport(this.level.getSpawnLocation(this.world)); // Teleport to current level's spawn.
        player.setHealth(EntityUtil.getAttribute(player, Attribute.MAX_HEALTH)); // Restore health.

        gamer.setState(GameState.INGAME);

        this.taskProgress.forEach((stageTask, progress) -> progress.onPlayerJoined(gamer)); // Adjust task progress for new players amount.

        Lang.DUNGEON_GAME_STARTED.getMessage().send(player);
    }

    private void leavePlayer(@NotNull DungeonPlayer gamer) {
        this.plugin.getDungeonManager().leaveInstance((DungeonGamer) gamer);
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.config.features().isPermissionRequired()) return true;

        return player.hasPermission(Perms.PREFIX_DUNGEON + this.getId()) || player.hasPermission(Perms.DUNGEON_ALL);
    }

    public boolean canAffordEntrance(@NotNull Player player) {
        if (!DungeonUtils.hasEconomyBridge() || !this.config.features().hasEntranceCost()) return true;

        return this.config.features().getEntranceCostMap().entrySet().stream().allMatch(entry -> EconomyBridge.hasEnough(player, entry.getKey(), entry.getValue()));
    }

    public boolean hasGoodLevel(@NotNull Player player) {
        LevelRequirement requirement = this.config.features().getLevelRequirement();
        if (!requirement.isRequired()) return true;

        return requirement.isGoodLevel(player);
    }

    public void payEntrance(@NotNull Player player) {
        if (!DungeonUtils.hasEconomyBridge()) return;

        this.config.features().getEntranceCostMap().forEach((id, price) -> {
            EconomyBridge.withdraw(player, id, price);
        });
    }

    public void refundEntrance(@NotNull Player player) {
        if (!DungeonUtils.hasEconomyBridge()) return;

        this.config.features().getEntranceCostMap().forEach((id, price) -> {
            EconomyBridge.deposit(player, id, price);
        });
    }

    public boolean canJoin(@NotNull Player player, boolean notify) {
        if (!this.isActive()) {
            if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_INACTIVE, replacer -> replacer.replace(this.replacePlaceholders()));
            return false;
        }

        if (this.config.features().isPermissionRequired() && !this.hasPermission(player)) {
            if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_PERMISSION, replacer -> replacer.replace(this.replacePlaceholders()));
            return false;
        }

        if (!player.hasPermission(Perms.BYPASS_DUNGEON_COOLDOWN)) {
            DungeonUser user = this.plugin.getUserManager().getOrFetch(player);
            if (user.isOnCooldown(this)) {
                if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_COOLDOWN, replacer -> replacer
                    .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(user.getArenaCooldown(this), TimeFormatType.LITERAL))
                    .replace(this.replacePlaceholders()));
                return false;
            }
        }

        if (this.isAboutToEnd()) {
            if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_ENDING, replacer -> replacer.replace(this.replacePlaceholders()));
            return false;
        }

        if (this.state == GameState.INGAME && !player.hasPermission(Perms.BYPASS_DUNGEON_JOIN_STARTED)) {
            if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_STARTED, replacer -> replacer.replace(this.replacePlaceholders()));
            return false;
        }

        int playerMax = this.config.gameSettings().getMaxPlayers();
        if (playerMax > 0 && this.countPlayers() >= playerMax) {
            if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_MAX_PLAYERS, replacer -> replacer.replace(this.replacePlaceholders()));
            return false;
        }

        if (!player.hasPermission(Perms.BYPASS_DUNGEON_ENTRANCE_COST)) {
            if (!this.canAffordEntrance(player)) {
                if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_COST, replacer -> replacer.replace(this.replacePlaceholders()));
                return false;
            }
        }

        if (!player.hasPermission(Perms.BYPASS_DUNGEON_ENTRANCE_LEVEL)) {
            if (!this.hasGoodLevel(player)) {
                if (notify) this.sendMessage(player, Lang.DUNGEON_ENTER_ERROR_LEVEL, replacer -> replacer.replace(this.replacePlaceholders()));
                return false;
            }
        }

        return true;
    }

    @Override
    public void handlePlayerJoin(@NotNull DungeonPlayer dungeonPlayer) {
        Player player = dungeonPlayer.getPlayer();
        DungeonGamer gamer = (DungeonGamer) dungeonPlayer;
        Kit kit = gamer.getKit();

        // Save the player inventory, effects, game modes, etc. before teleporting to the arena.
        PlayerSnapshot snapshot = PlayerSnapshot.doSnapshot(player);

        // Pay for the entrance (and kit) before cleaning.
        if (!player.hasPermission(Perms.BYPASS_DUNGEON_ENTRANCE_COST)) {
            this.payEntrance(player);
        }
        if (kit != null && KitUtils.isRentMode() && kit.hasCost() && !player.hasPermission(Perms.BYPASS_KIT_COST)) {
            kit.takeCosts(player);
        }

        // Now clear all player's active effects, god modes, etc.
        player.teleport(this.getLobbyLocation());
        player.setGameMode(this.getGameMode());
        PlayerSnapshot.clear(player);
        UniParticle.of(Particle.CLOUD).play(player.getLocation(), 0.25, 0.15, 30);

        if (this.isKitsMode() && kit != null) {
            player.getInventory().clear();
            kit.give(player);
        }
        else {
            List<ItemStack> confiscate = snapshot.getConfiscate();

            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType().isAir()) continue;
                if (this.config.gameSettings().isBannedItem(item)) {
                    confiscate.add(new ItemStack(item));
                    item.setAmount(0);
                }
            }
            if (!confiscate.isEmpty()) {
                Lang.DUNGEON_CONFISACATE_INFO.getMessage().send(player, replacer -> replacer
                    .replace(Placeholders.GENERIC_ITEM, confiscate.stream().map(ItemUtil::getSerializedName).collect(Collectors.joining(", ")))
                );
            }
        }

        this.sendMessage(player, Lang.DUNGEON_JOIN_LOBBY, replacer -> replacer.replace(this.replacePlaceholders()));
        this.broadcast(Lang.DUNGEON_JOIN_NOTIFY, replacer -> replacer.replace(this.replacePlaceholders()).replace(gamer.replacePlaceholders()));

        this.players.put(player.getUniqueId(), gamer);

        if (this.config.gameSettings().isScoreboardEnabled() && DungeonUtils.hasPacketLibrary()) {
            gamer.addBoard();
        }

        // this.updateSigns();

        if (this.state == GameState.INGAME) {
            gamer.setDead(true);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(this.level.getSpawnLocation(this.world));
        }
    }

    @Override
    public void handlePlayerLeave(@NotNull DungeonPlayer dungeonPlayer) {
        DungeonGamer gamer = (DungeonGamer) dungeonPlayer;
        Player player = gamer.getPlayer();

        player.closeInventory();
        gamer.removeBoard();
        this.players.remove(player.getUniqueId());

        if (this.state == GameState.INGAME && this.gameResult == null && gamer.isInGame()) {
            this.taskProgress.forEach((stageTask, progress) -> progress.onPlayerLeft(gamer));
        }

        if (!this.isAboutToEnd() || this.gameResult == GameResult.DEFEAT) {
            gamer.takeDefeatRewards();
        }

        // Remove kit effects.
        Kit kit = gamer.getKit();
        if (kit != null) {
            kit.resetPotionEffects(player);
            kit.resetAttributeModifiers(player);
        }

        // Restore player data.
        PlayerSnapshot.restore(gamer);

        // Refund payments.
        if (this.state != GameState.INGAME) {
            if (!player.hasPermission(Perms.BYPASS_DUNGEON_ENTRANCE_COST)) {
                this.refundEntrance(player);
            }
            if (kit != null && KitUtils.isRentMode() && kit.hasCost() && !player.hasPermission(Perms.BYPASS_KIT_COST)) {
                kit.refundCosts(player);
            }
        }
        else {
            gamer.getRewards().forEach(reward -> reward.getReward().give(this, gamer));

            // Set cooldown only if dungeon have been started.
            if (!player.hasPermission(Perms.BYPASS_DUNGEON_COOLDOWN)) {
                this.plugin.getDungeonManager().setJoinCooldown(player, this);
            }
        }
    }

    public void handlePlayerDeath(@NotNull DungeonGamer gamer) {
        boolean hasExtraLives = gamer.hasExtraLives();

        gamer.handleDeath();

        if (!hasExtraLives && this.config.gameSettings().isLeaveOnDeath()) {
            this.plugin.runTask(task -> this.leavePlayer(gamer));
        }

        DungeonPlayerDeathEvent event = new DungeonPlayerDeathEvent(this, gamer);
        this.broadcastEvent(event);
    }

    public void handleMobDeath(@NotNull DungeonEntity mob, @NotNull EntityDeathEvent event) {
        if (!this.config.gameSettings().isMobsDropLoot()) {
            event.getDrops().clear();
        }
        if (!this.config.gameSettings().isMobsDropXP()) {
            event.setDroppedExp(0);
        }

        DungeonMobKilledEvent mobDeathEvent = new DungeonMobKilledEvent(this, mob);

        LivingEntity entity = mob.getBukkitEntity();
        Player killer = entity.getKiller();
        DungeonGamer gamer = killer == null ? null : this.getPlayer(killer.getUniqueId());
        if (killer != null && gamer != null) {
            mobDeathEvent.setGamer(gamer);

            int streak = gamer.getKillStreak() + 1;
            gamer.addKill();
            gamer.setKillStreak(streak);
            gamer.setKillStreakDecay(Config.KILL_STREAKS_DECAY_TIME.get());

            if (DungeonUtils.isKillStreaksEnabled()) {
                KillStreak killStreak = DungeonUtils.getKillStreak(gamer.getKillStreak());
                if (killStreak != null) {
                    killStreak.run(this, gamer);
                }
            }
        }

        this.broadcastEvent(mobDeathEvent);
        this.eliminateMob(mob);
    }

    public void handleMobSpawn(@NotNull LivingEntity entity) {
        if (MobUitls.isPet(entity)) {
            if (!this.config.gameSettings().isPetsAllowed()) {
                entity.remove();
            }
            return;
        }

        if (this.hasMob(entity.getUniqueId())) return;

        MobProvider provider = MobRegistry.getProvider(entity);
        String mobId = provider == null ? null : provider.getMobId(entity);
        if (provider == null || mobId == null) {
            if (Config.MOBS_REMOVE_UNKNOWN_MOBS.get()) {
                entity.remove();
            }
            return;
        }

        MobIdentifier identifier = MobIdentifier.from(provider, mobId);
        MobFaction faction = MobUitls.isExternalAlly(identifier) ? MobFaction.ALLY : MobFaction.ENEMY;

        DungeonMob dungeonMob = new DungeonMob(this, entity, faction, provider, mobId);
        this.addMob(dungeonMob);
    }

    public void handleStageEnd() {
        this.stageCompleted = true;
        this.broadcastEvent(new DungeonStageFinishEvent(this, this.stage));
    }

    public boolean isReadyToStart() {
        return this.hasMinimumPlayers();
    }

    public boolean hasMinimumPlayers() {
        return this.countPlayers() >= this.config.gameSettings().getMinPlayers();
    }

    public void setLevel(@NotNull Level level) {
        if (this.isLevel(level)) return;

        this.level = level;
        this.updateListeners();
        this.broadcastEvent(new DungeonLevelStartEvent(this, level));
        this.broadcast(Lang.DUNGEON_GAME_LEVEL_CHANGED, replacer -> replacer.replace(this.replacePlaceholders()).replace(level.replacePlaceholders()));
    }

    public boolean isLevel(@NotNull Level level) {
        return this.level == level;
    }

    public void setStage(@NotNull Stage stage) {
        if (this.isStage(stage)) return;

        this.removeTasks();
        this.stage = stage;
        this.stageCompleted = false;
        this.updateListeners();
        this.addTasks(stage);
        this.broadcastEvent(new DungeonStageStartEvent(this, stage));
        this.broadcast(Lang.DUNGEON_GAME_STAGE_CHANGED, replacer -> replacer.replace(this.replacePlaceholders()).replace(stage.replacePlaceholders()));
    }

    public boolean isStage(@NotNull Stage stage) {
        return this.stage == stage;
    }



    public boolean isAboutToEnd() {
        return this.state == GameState.INGAME && this.countdown >= 0 && this.gameResult != null;
    }



    private void eliminateDeadMobs() {
        this.getMobs().forEach(mob -> {
            if (mob.isDead()) {
                this.eliminateMob(mob);
            }
        });
    }

    @Override
    public void killMobs() {
        this.getMobs().forEach(this::eliminateMob);
        this.mobByIdMap.clear();
    }

    @Override
    public boolean isAllyMob(@NotNull LivingEntity entity) {
        return this.getMobFaction(entity) == MobFaction.ALLY;
    }

    @Override
    public boolean isEnemyMob(@NotNull LivingEntity entity) {
        return this.getMobFaction(entity) == MobFaction.ENEMY;
    }

    @Override
    public boolean hasAllyMobs() {
        return this.hasMobsOfFaction(MobFaction.ALLY);
    }

    @Override
    public boolean hasEnemyMobs() {
        return this.hasMobsOfFaction(MobFaction.ENEMY);
    }

    @Override
    public boolean hasMobsOfFaction(@NotNull MobFaction faction) {
        return this.getMobs().stream().anyMatch(mob -> mob.isFaction(faction));
    }

    @Override
    @Nullable
    public MobFaction getMobFaction(@NotNull LivingEntity entity) {
        DungeonMob mob = this.getMob(entity);
        return mob == null ? null : mob.getFaction();
    }

    @Override
    public void eliminateMob(@NotNull DungeonEntity mob) {
        mob.getBukkitEntity().getLocation().getChunk(); // Load chunk to remove entity lol.
        mob.getBukkitEntity().setPersistent(false);
        if (mob.isAlive()) {
            mob.getBukkitEntity().remove();
        }
        this.counters.addMobKill(mob);
        this.removeMob(mob);
        this.broadcastEvent(new DungeonMobEliminatedEvent(this, mob));
        DungeonEntityBridge.removeHolder(mob);
    }

    public boolean spawnAllyMob(@NotNull EntityType entityType, @NotNull Location location, int level) {
        MobIdentifier identifier = MobUitls.getEggAllyIdentifier(entityType);
        if (identifier == null) return false;

        MobProvider provider = MobRegistry.getProviderByName(identifier.getProviderId());
        if (provider == null) return false;

        String mobId = identifier.getMobId();
        MobFaction faction = MobFaction.ALLY;

        this.spawnMob(provider, mobId, faction, location, level);
        return true;
    }

    @Override
    public void spawnMob(@NotNull MobProvider provider, @NotNull String mobId, @NotNull MobFaction faction, @NotNull DungeonSpawner spawner, int level, int amount) {
        if (spawner.isEmpty()) {
            ErrorHandler.error("Could not spawn mob '" + provider.getName() + ":" + mobId + "' at empty spawner '" + spawner.getId() + "'!", this);
            return;
        }

        for (int i = 0; i < amount; i++) {
            Location location = spawner.getRandomPosition().toLocation(this.world)
                .add(MobUitls.getRandomSpawnOffset(), 0, MobUitls.getRandomSpawnOffset());

            this.spawnMob(provider, mobId, faction, location, level);
        }
    }

    @Override
    public void spawnMob(@NotNull MobProvider provider, @NotNull String mobId, @NotNull MobFaction faction, @NotNull Location location, int level) {
        LivingEntity mob = provider.spawn(this, mobId, faction, location, level, entity -> {
            // Probably don't need to store providerId and mobId, since these values are already stored in a DungeonMob.
        });
        if (mob == null) {
            ErrorHandler.error("Could not spawn mob '" + provider.getName() + ":" + mobId + "', spawned entity is null!", this);
            return;
        }

        DungeonMob dungeonMob = new DungeonMob(this, mob, faction, provider, mobId);

        this.addMob(dungeonMob);
        this.broadcastEvent(new DungeonMobSpawnedEvent(this, dungeonMob));
    }

    @Override
    public void addMob(@NotNull DungeonEntity mob) {
        this.mobByIdMap.put(mob.getUniqueId(), (DungeonMob) mob);
        this.counters.addMobSpawn(mob);

        LivingEntity entity = mob.getBukkitEntity();

        MobUitls.setDungeonId(entity, this);
        entity.setPersistent(true);
        entity.setRemoveWhenFarAway(false);
        DungeonEntityBridge.addHolder(mob);
    }

    @Override
    public void removeMob(@NotNull DungeonEntity mob) {
        this.removeMob(mob.getUniqueId());
    }

    @Override
    public void removeMob(@NotNull UUID mobId) {
        this.mobByIdMap.remove(mobId);
    }

    @Override
    public boolean hasMob(@NotNull UUID mobId) {
        return this.mobByIdMap.containsKey(mobId);
    }

    @Override
    @Nullable
    public DungeonMob getMob(@NotNull LivingEntity entity) {
        return this.getMobById(entity.getUniqueId());
    }

    @Override
    @Nullable
    public DungeonMob getMobById(@NotNull UUID mobId) {
        return this.mobByIdMap.get(mobId);
    }

    @Override
    @NotNull
    public Set<DungeonMob> getAllyMobs() {
        return this.getMobs(MobFaction.ALLY);
    }

    @Override
    @NotNull
    public Set<DungeonMob> getEnemyMobs() {
        return this.getMobs(MobFaction.ENEMY);
    }

    @Override
    @NotNull
    public Set<DungeonMob> getMobs() {
        return new HashSet<>(this.mobByIdMap.values());
    }

    @Override
    @NotNull
    public Set<DungeonMob> getMobs(@NotNull MobFaction faction) {
        return this.getMobs().stream().filter(mob -> mob.getFaction() == faction).collect(Collectors.toSet());
    }



    public void addTasks(@NotNull Stage stage) {
        stage.getTasks().forEach(stageTask -> {
            if (stageTask.getParams().isAutoAdd()) {
                this.addTask(stageTask);
            }
        });
    }

    public void addTask(@NotNull StageTask stageTask) {
        TaskProgress progress = stageTask.createProgress(this);
        if (progress.isEmpty()) return;

        stageTask.getTask().onTaskAdd(this, stageTask, progress);

        this.taskProgress.put(stageTask, progress);
        this.stageCompleted = false;

        boolean isPersonal = stageTask.getParams().isPerPlayer();

        this.broadcast((isPersonal ? Lang.DUNGEON_TASK_CREATED_PERSONAL : Lang.DUNGEON_TASK_CREATED_GLOBAL), (player, replacer) -> replacer
            .replace(this.replacePlaceholders())
            .replace(Placeholders.GENERIC_NAME, stageTask.getParams().getDisplay())
            .replace(Placeholders.GENERIC_VALUE, progress.format(isPersonal ? player : null)));

        this.broadcastEvent(new DungeonTaskCreatedEvent(this, stageTask, progress));
    }

    public void removeTask(@NotNull StageTask stageTask) {
        TaskProgress progress = this.taskProgress.remove(stageTask);
        if (progress == null) return;

        stageTask.getTask().onTaskRemove(this, stageTask, progress);
    }

    public void removeTasks() {
        this.getTasks().forEach(this::removeTask);
    }

    @NotNull
    public Set<StageTask> getTasks() {
        return new HashSet<>(this.taskProgress.keySet());
    }

    public boolean hasTask(@NotNull StageTask stageTask) {
        return this.taskProgress.containsKey(stageTask);
    }

    public boolean hasTasks() {
        return !this.taskProgress.isEmpty();
    }

    public boolean isTaskCompleted(@NotNull StageTask stageTask) {
        TaskProgress progress = this.taskProgress.get(stageTask);
        return progress != null && progress.isCompleted();
    }

    public boolean isTasksCompleted() {
        return !this.taskProgress.isEmpty() && this.taskProgress.values().stream().allMatch(TaskProgress::isCompleted);
    }





    public void refillLootChests() {
        this.config.getLootChests().forEach(this::refillLootChest);
    }

    public void refillLootChest(@NotNull LootChest lootChest) {
        lootChest.generateLoot(this);
    }

    public void clearLootChests() {
        this.config.getLootChests().forEach(this::clearLootChest);
    }

    public void clearLootChest(@NotNull LootChest lootChest) {
        lootChest.clearLoot(this);
    }





    public boolean isKitsMode() {
        return this.config.gameSettings().isKitsEnabled();
    }

    public boolean isKitAllowed(@NotNull Kit kit) {
        return this.config.gameSettings().isKitAllowed(kit.getId());
    }

    public boolean isKitAvailable(@NotNull Kit kit) {
        if (!this.isKitAllowed(kit)) {
            return false;
        }

        return !this.isKitLimitReached(kit);
    }

    public boolean isKitLimitReached(@NotNull Kit kit) {
        return this.countKitFreeSlots(kit) == 0;
    }

    public int getKitLimit(@NotNull Kit kit) {
        return this.config.gameSettings().getKitLimit(kit.getId());
    }

    public int countKitInUse(@NotNull Kit kit) {
        return (int) this.getPlayers().stream().filter(gamer -> gamer.isKit(kit)).count();
    }

    public int countKitFreeSlots(@NotNull Kit kit) {
        int limit = this.getKitLimit(kit);
        if (limit < 0) return -1;

        return limit - this.countKitInUse(kit);
    }


    @Override
    public boolean hasPlayer(@NotNull UUID playerId) {
        return this.players.containsKey(playerId);
    }

    @Override
    public boolean hasAlivePlayers() {
        return this.players.values().stream().anyMatch(DungeonPlayer::isAlive);
    }

    @Override
    public int countPlayers() {
        return this.players.size();
    }

    @Override
    public int countAlivePlayers() {
        return this.getAlivePlayers().size();
    }

    @Override
    public int countDeadPlayers() {
        return this.getDeadPlayers().size();
    }

    @Override
    @NotNull
    public DungeonGamer getRandomAlivePlayer() {
        return Rnd.get(this.getPlayers());
    }

    @Override
    @Nullable
    public DungeonGamer getPlayer(@NotNull UUID playerId) {
        return this.players.get(playerId);
    }

    @Override
    @NotNull
    public Set<DungeonGamer> getPlayers() {
        return new HashSet<>(this.players.values());
    }

    @NotNull
    public Set<DungeonGamer> getAlivePlayers() {
        return this.players.values().stream().filter(DungeonPlayer::isAlive).collect(Collectors.toSet());
    }

    @NotNull
    public Set<DungeonGamer> getDeadPlayers() {
        return this.players.values().stream().filter(DungeonPlayer::isDead).collect(Collectors.toSet());
    }




    public void addGroundItem(@NotNull Item item) {
        this.groundItems.removeIf(other -> !other.isValid());
        this.groundItems.add(item);

        if (this.state == GameState.INGAME && !this.config.gameSettings().isItemPickupAllowed()) {
            item.setPickupDelay(Short.MAX_VALUE);
            item.setOwner(UUID.randomUUID());
        }
    }

    public void burnGroundItems() {
        UniParticle particle = UniParticle.of(Particle.SMOKE);
        this.groundItems.removeIf(item -> {
            if (!item.isValid()) return true;
            if (!item.isOnGround()) return false;

            particle.play(item.getLocation(), 0.1, 0.05, 15);
            item.remove();
            return true;
        });
    }

    public void killGroundItems() {
        this.groundItems.forEach(Entity::remove);
        this.groundItems.clear();
    }

    @NotNull
    public Set<Item> getGroundItems() {
        return this.groundItems;
    }




    public void resetSpotStates() {
        this.config.getSpots().forEach(this::resetSpotState);
    }

    public void resetSpotState(@NotNull Spot spot) {
        SpotState state = spot.getDefaultState();
        if (state == null) return;

        this.setSpotState(spot, state);
    }

    public void setSpotState(@NotNull Spot spot, @NotNull SpotState state) {
        if (!this.isActive()) return;

        spot.build(this.world, state);
        spot.setLastState(state);

        this.broadcastEvent(new DungeonSpotChangeEvent(this, spot, state));
    }




    @Override
    public boolean contains(@NotNull BlockPos blockPos) {
        return this.config.isInProtection(blockPos);
    }

    @Override
    public boolean contains(@NotNull Block block) {
        return this.config.isInProtection(block);
    }

    @Override
    public boolean contains(@NotNull Location location) {
        return this.config.isInProtection(location);
    }

    @Override
    public boolean contains(@NotNull Entity entity) {
        return this.config.isInProtection(entity);
    }



    @Override
    public long getTickCount() {
        return this.tickCount;
    }

    @Override
    @NotNull
    public String getId() {
        return this.config.getId();
    }

    @NotNull
    public DungeonConfig getConfig() {
        return this.config;
    }

    @NotNull
    public DungeonCounters getCounters() {
        return this.counters;
    }

    @NotNull
    public DungeonVariables getVariables() {
        return this.variables;
    }

    @Override
    @NotNull
    public GameState getState() {
        return this.state;
    }

    @NotNull
    public Location getLobbyLocation() {
        return this.config.getLobbyPos().toLocation(this.world);
    }

    public int getCountdown() {
        return this.countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setCountdown(int countdown, @NotNull GameResult gameResult) {
        this.countdown = countdown;
        if (this.state == GameState.INGAME) {
            this.gameResult = gameResult;
        }
    }

    public long getTimeLeft() {
        return this.timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    @NotNull
    public Level getLevel() {
        return this.level;
    }

    @NotNull
    public Stage getStage() {
        return this.stage;
    }

    @NotNull
    public GameMode getGameMode() {
        return this.config.gameSettings().isAdventureMode() ? GameMode.ADVENTURE : GameMode.SURVIVAL;
    }

    @NotNull
    public List<DungeonEventReceiver> getEventReceivers() {
        return this.eventReceivers;
    }

    @NotNull
    public Map<StageTask, TaskProgress> getTaskProgress() {
        return this.taskProgress;
    }
}
