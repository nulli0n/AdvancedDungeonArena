package su.nightexpress.dungeons.dungeon.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.api.compat.BoardPlugin;
import su.nightexpress.dungeons.api.compat.GodPlugin;
import su.nightexpress.dungeons.api.dungeon.Board;
import su.nightexpress.dungeons.api.dungeon.DungeonPlayer;
import su.nightexpress.dungeons.api.type.GameState;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.dungeon.feature.board.BoardLayout;
import su.nightexpress.dungeons.dungeon.feature.board.impl.PacketsBoard;
import su.nightexpress.dungeons.dungeon.feature.board.impl.ProtocolBoard;
import su.nightexpress.dungeons.dungeon.game.DungeonInstance;
import su.nightexpress.dungeons.dungeon.level.Level;
import su.nightexpress.dungeons.dungeon.reward.GameReward;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.dungeons.registry.compat.BoardPluginRegistry;
import su.nightexpress.dungeons.registry.compat.GodPluginRegistry;
import su.nightexpress.dungeons.util.DungeonUtils;
import su.nightexpress.nightcore.locale.entry.MessageLocale;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class DungeonGamer implements DungeonPlayer {

    //private final DungeonPlugin plugin;
    private final Player           player;
    private final DungeonInstance  dungeon;
    private final List<GameReward> rewards;

    private final GodPlugin   godPlugin;
    private final BoardPlugin boardPlugin;

    private GameState state;
    private Board board;
    private Kit   kit;

    private Location deathLocation;

    private boolean dead;
    private long    deathTime;
    private int     lives;
    private int     killStreak;
    private long    killStreakDecay;
    private int     kills;
    private int     score;

    private boolean teleporting;

    public DungeonGamer(@NotNull Player player, @NotNull DungeonInstance dungeon) {
        this.player = player;
        this.dungeon = dungeon;
        this.state = GameState.WAITING;
        this.rewards = new ArrayList<>();
        this.setDead(false);
        this.deathTime = -1L;
        this.setLives(dungeon.getConfig().gameSettings().getPlayerLives());

        this.godPlugin = GodPluginRegistry.getGodProvider(player);
        this.boardPlugin = BoardPluginRegistry.getBoardProvider(player);
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.DUNGEON_GAMER.replacer(this);
    }

    @Override
    public boolean isReady() {
        return this.state == GameState.READY && !this.isDead();
    }

    @Override
    public boolean isInLobby() {
        return this.state != GameState.INGAME;
    }

    @Override
    public boolean isInGame() {
        return this.state == GameState.INGAME;
    }

    @Override
    public void tick() {
        if (this.isDead() && !this.dungeon.isAboutToEnd()) {
            (this.hasExtraLives() ? Lang.DUNGEON_STATUS_DEAD_LIVES : Lang.DUNGEON_STATUS_DEAD_NO_LIVES).message().send(this.player, replacer -> replacer
                .replace(this.dungeon.replacePlaceholders())
                .replace(this.replacePlaceholders())
            );
        }

        if (this.killStreakDecay-- <= 0) {
            this.setKillStreak(0);
            this.setKillStreakDecay(0);
        }

        if (this.kit != null) {
            this.kit.applyPotionEffects(this.player);
        }

        this.updateBoard();
    }

    @Override
    public void teleport(@NotNull Location location) {
        this.teleporting = true;
        this.player.teleport(location);
        this.teleporting = false;
    }

    @Override
    public void revive() {
        if (!this.isDead() || !this.hasExtraLives()) return;

        Level level = this.dungeon.getLevel();
        this.teleport(level.getSpawnLocation(this.dungeon.getWorld()));

        this.setDead(false);
        this.player.setGameMode(this.dungeon.getGameMode());

        if (this.kit != null) {
            this.kit.applyPotionEffects(this.player);
            this.kit.applyAttributeModifiers(this.player);
        }
        //this.player.playEffect(EntityEffect.TOTEM_RESURRECT);

        MessageLocale locale = this.hasExtraLives() ? Lang.DUNGEON_REVIVE_WITH_LIFES : Lang.DUNGEON_REVIVE_NO_LIFES;
        this.dungeon.sendMessage(this.player, locale, replacer -> replacer.replace(this.replacePlaceholders()));
    }

    @Override
    public void handleDeath() {
        Location location = this.player.getLocation();
        if (!this.dungeon.contains(location)) {
            location = this.dungeon.getSpawnLocation();
        }

        this.setDeathLocation(location);
        this.setDead(true);
        this.deathTime = System.currentTimeMillis();
        this.takeExtraLive();
        this.takeDeathRewards();

        if (!this.hasExtraLives()) {
            if (this.dungeon.hasAlivePlayers()) {
                this.dungeon.sendMessage(this.player, Lang.DUNGEON_DEATH_NO_LIFES, replacer -> replacer.replace(this.replacePlaceholders()));
            }
        }
        else {
            if (this.kit != null) {
                this.kit.resetPotionEffects(this.player);
                this.kit.resetAttributeModifiers(this.player);
            }

            if (this.dungeon.hasAlivePlayers()) {
                this.dungeon.sendMessage(this.player, Lang.DUNGEON_DEATH_WITH_LIFES, replacer -> replacer.replace(this.replacePlaceholders()));
            }
        }

        this.setKillStreak(0);
        this.setKillStreakDecay(0);

        this.dungeon.broadcast(Lang.DUNGEON_GAME_PLAYER_DIED, replacer -> replacer
            .replace(this.dungeon.replacePlaceholders())
            .replace(this.replacePlaceholders())
        );
    }

    public void handleRespawn() {
        this.player.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void manageExternalGod(@NotNull Consumer<GodPlugin> consumer) {
        if (this.godPlugin != null) {
            consumer.accept(this.godPlugin);
        }
    }

    @Override
    public void manageExternalBoard(@NotNull Consumer<BoardPlugin> consumer) {
        if (this.boardPlugin != null) {
            consumer.accept(this.boardPlugin);
        }
    }

    @Override
    public void addBoard() {
        BoardLayout layout = this.dungeon.getConfig().gameSettings().getBoardLayout();
        if (layout == null) return;

        if (DungeonUtils.hasPacketEvents()) {
            this.board = new PacketsBoard(this, layout);
        }
        else {
            this.board = new ProtocolBoard(this, layout);
        }

        this.board.create();
        this.board.update();
    }

    @Override
    public void removeBoard() {
        if (this.board != null) {
            this.board.remove();
            this.board = null;
        }
    }

    @Override
    public void updateBoard() {
        if (this.board != null) {
            this.board.update();
        }
    }

    @Override
    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Override
    @NotNull
    public DungeonInstance getDungeon() {
        return this.dungeon;
    }

    @Override
    @NotNull
    public GameState getState() {
        return this.state;
    }

    @Override
    public void setState(@NotNull GameState state) {
        this.state = state;
    }

    @NotNull
    public List<GameReward> getRewards() {
        return this.rewards;
    }

    public void addReward(@NotNull GameReward reward) {
        this.rewards.add(reward);
    }

    public void takeDeathRewards() {
        this.rewards.removeIf(reward -> !reward.isKeepOnDeath());
    }

    public void takeDefeatRewards() {
        this.rewards.removeIf(reward -> !reward.isKeepOnDefeat());
    }

    public void clearRewards() {
        this.rewards.clear();
    }

    @Nullable
    public Kit getKit() {
        return this.kit;
    }

    public void setKit(@Nullable Kit kit) {
        this.kit = kit;
    }

    public boolean hasKit() {
        return this.kit != null;
    }

    public boolean isKit(@NotNull Kit kit) {
        return this.kit == kit;
    }

    @Override
    public boolean isDead() {
        return this.dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Nullable
    public Location getDeathLocation() {
        return this.deathLocation;
    }

    public void setDeathLocation(@Nullable Location deathLocation) {
        this.deathLocation = deathLocation;
    }

    @Override
    public long getDeathTime() {
        return this.deathTime;
    }

    @Override
    public boolean isAlive() {
        return !this.isDead();
    }

    @Override
    public boolean hasExtraLives() {
        return this.lives > 0;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }

    @Override
    public void addExtraLive() {
        this.setLives(this.lives + 1);
    }

    @Override
    public void takeExtraLive() {
        this.setLives(this.lives - 1);
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public int getKillStreak() {
        return this.killStreak;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = Math.max(0, killStreak);
    }

    public long getKillStreakDecay() {
        return this.killStreakDecay;
    }

    public void setKillStreakDecay(long killStreakDecay) {
        this.killStreakDecay = killStreakDecay;
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = Math.max(0, kills);
    }

    public void addKill() {
        this.kills++;
    }

    public boolean isTeleporting() {
        return this.teleporting;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }
}
