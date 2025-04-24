package su.nightexpress.dungeons.dungeon.feature.board.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.score.ScoreFormat;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResetScore;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import io.github.retrooper.packetevents.adventure.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.dungeon.player.DungeonGamer;
import su.nightexpress.dungeons.dungeon.feature.board.AbstractBoard;
import su.nightexpress.dungeons.dungeon.feature.board.BoardLayout;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.Optional;

public class PacketsBoard extends AbstractBoard<PacketWrapper<?>> {

    public PacketsBoard(@NotNull DungeonGamer gamer, @NotNull BoardLayout boardConfig) {
        super(gamer, boardConfig);
    }

    @Override
    @NotNull
    protected WrapperPlayServerScoreboardObjective createObjectivePacket(ObjectiveMode mode, @NotNull String displayName) {
        WrapperPlayServerScoreboardObjective.ObjectiveMode objectiveMode = switch (mode) {
            case CREATE -> WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE;
            case REMOVE -> WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE;
            case UPDATE -> WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE;
        };

        return new WrapperPlayServerScoreboardObjective(
            this.identifier,
            objectiveMode,
            GsonComponentSerializer.gson().deserialize(NightMessage.asJson(displayName)),
            WrapperPlayServerScoreboardObjective.RenderType.INTEGER,
            ScoreFormat.blankScore()
        );
    }

    @Override
    @NotNull
    protected WrapperPlayServerResetScore createResetScorePacket(@NotNull String scoreId) {
        return new WrapperPlayServerResetScore(scoreId, this.identifier);
    }

    @Override
    @NotNull
    protected WrapperPlayServerUpdateScore createScorePacket(@NotNull String scoreId, int score, @NotNull String text) {
        WrapperPlayServerUpdateScore scorePacket = new WrapperPlayServerUpdateScore(
            scoreId,
            WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
            this.identifier,
            Optional.of(score)
        );

        scorePacket.setEntityDisplayName(GsonComponentSerializer.gson().deserialize(NightMessage.asJson(text)));
        scorePacket.setScoreFormat(ScoreFormat.blankScore());

        return scorePacket;
    }

    @Override
    @NotNull
    protected WrapperPlayServerDisplayScoreboard createDisplayPacket() {
        return new WrapperPlayServerDisplayScoreboard(1, this.identifier);
    }

    @Override
    protected void sendPacket(@NotNull Player player, @NotNull PacketWrapper<?> wrapper) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
    }
}
