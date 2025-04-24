package su.nightexpress.dungeons.selection.visual.highlight;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.selection.visual.FakeEntity;
import su.nightexpress.nightcore.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.*;

public class BlockPacketsHighlighter extends BlockHighlighter {

    private final PlayerManager manager;

    public BlockPacketsHighlighter(@NotNull DungeonPlugin plugin) {
        super(plugin);
        this.manager = PacketEvents.getAPI().getPlayerManager();
    }

    @Override
    @NotNull
    protected FakeEntity spawnVisualBlock(int entityID, @NotNull Player player, @NotNull Location location, @NotNull BlockData blockData, @NotNull ChatColor color, float size) {
        EntityType type = EntityType.BLOCK_DISPLAY;
        UUID uuid = UUID.randomUUID();
        String entityUID = uuid.toString();
        //StateType stateType = this.stateTypeMap.computeIfAbsent(material,  k -> StateTypes.getByName(BukkitThing.toString(material)));
        WrappedBlockState state = WrappedBlockState.getByString(blockData.getAsString());//WrappedBlockState.getDefaultState(stateType);

        var spawnPacket = this.createSpawnPacket(type, location, entityID, uuid);

        var dataPacket = this.createMetadataPacket(entityID, dataList -> {
            dataList.add(new EntityData(0, EntityDataTypes.BYTE, (byte) (0x20 | 0x40))); // glow
            dataList.add(new EntityData(12, EntityDataTypes.VECTOR3F, new Vector3f(size, size, size))); // scale
            dataList.add(new EntityData(23, EntityDataTypes.BLOCK_STATE, state.getGlobalId())); // block ID
        });

        ScoreBoardTeamInfo info = new ScoreBoardTeamInfo(
            Component.text(entityUID),
            Component.text(""),
            Component.text(""),
            NameTagVisibility.ALWAYS,
            CollisionRule.ALWAYS,
            NamedTextColor.NAMES.valueOr(color.name().toLowerCase(), NamedTextColor.WHITE),
            OptionData.NONE
        );
        var teamPacket = new WrapperPlayServerTeams(entityUID, TeamMode.CREATE, info, Lists.newList(entityUID));

        this.manager.sendPacket(player, spawnPacket);
        this.manager.sendPacket(player, teamPacket);
        this.manager.sendPacket(player, dataPacket);

        return new FakeEntity(entityID, uuid);
    }

    @Override
    protected void destroyEntity(@NotNull Player player, @NotNull List<FakeEntity> idList) {
        idList.forEach(fakeEntity -> {
            String entityUID = fakeEntity.getUUID().toString();

            WrapperPlayServerTeams teamPacket = new WrapperPlayServerTeams(entityUID, TeamMode.REMOVE, (ScoreBoardTeamInfo) null, Lists.newList(entityUID));
            this.manager.sendPacket(player, teamPacket);
        });

        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(idList.stream().mapToInt(FakeEntity::getId).toArray());
        this.manager.sendPacket(player, destroyPacket);
    }

    @NotNull
    private WrapperPlayServerSpawnEntity createSpawnPacket(@NotNull EntityType type, @NotNull Location location, int entityID, @NotNull UUID uuid) {
        com.github.retrooper.packetevents.protocol.entity.type.EntityType wrappedType = SpigotConversionUtil.fromBukkitEntityType(type);
        com.github.retrooper.packetevents.protocol.world.Location wrappedLocation = SpigotConversionUtil.fromBukkitLocation(location);

        return new WrapperPlayServerSpawnEntity(entityID, uuid, wrappedType, wrappedLocation, 0f, 0, new Vector3d());
    }

    @NotNull
    private PacketWrapper<?> createMetadataPacket(int entityID, @NotNull Consumer<List<EntityData>> consumer) {
        List<EntityData> dataList = new ArrayList<>();

        consumer.accept(dataList);

        return new WrapperPlayServerEntityMetadata(entityID, dataList);
    }
}
