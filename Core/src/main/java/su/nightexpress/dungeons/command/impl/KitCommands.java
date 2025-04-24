package su.nightexpress.dungeons.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.dungeons.DungeonPlugin;
import su.nightexpress.dungeons.Placeholders;
import su.nightexpress.dungeons.command.CommandArguments;
import su.nightexpress.dungeons.config.Lang;
import su.nightexpress.dungeons.config.Perms;
import su.nightexpress.dungeons.kit.KitUtils;
import su.nightexpress.dungeons.kit.impl.Kit;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.language.entry.LangText;

public class KitCommands {

    public static void load(@NotNull DungeonPlugin plugin, @NotNull ChainedNode root) {
        var kitRoot = ChainedNode.builder(plugin, "kit")
            .description(Lang.COMMAND_KIT_DESC)
            .permission(Perms.COMMAND_KIT);

        kitRoot.addDirect("create", builder -> builder
            .playerOnly()
            .description(Lang.COMMAND_KIT_CREATE_DESC)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
            .executes((context, arguments) -> createKit(plugin, context, arguments))
        );

        kitRoot.addDirect("setitems", builder -> builder
            .playerOnly()
            .description(Lang.COMMAND_KIT_SET_ITEMS_DESC)
            .withArgument(CommandArguments.forKit(plugin).required())
            .executes((context, arguments) -> updateKit(plugin, context, arguments))
        );

        if (!KitUtils.isRentMode()) {
            kitRoot.addDirect("grant", builder -> builder
                .description(Lang.COMMAND_KIT_GRANT_DESC)
                .withArgument(CommandArguments.forKit(plugin).required())
                .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
                .executes((context, arguments) -> grantOrRevokeKit(plugin, context, arguments, true))
            );

            kitRoot.addDirect("revoke", builder -> builder
                .description(Lang.COMMAND_KIT_REVOKE_DESC)
                .withArgument(CommandArguments.forKit(plugin).required())
                .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
                .executes((context, arguments) -> grantOrRevokeKit(plugin, context, arguments, false))
            );
        }

        root.addChildren(kitRoot);
    }

    private static boolean createKit(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String name = arguments.getStringArgument(CommandArguments.NAME);

        plugin.getKitManager().createKit(player, name);
        return true;
    }

    private static boolean updateKit(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Kit kit = arguments.getArgument(CommandArguments.KIT, Kit.class);

        plugin.getKitManager().updateKit(player, kit);
        return true;
    }

    private static boolean grantOrRevokeKit(@NotNull DungeonPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, boolean grant) {
        String playerName = arguments.getStringArgument(CommandArguments.PLAYER);
        Kit kit = arguments.getArgument(CommandArguments.KIT, Kit.class);

        plugin.getUserManager().manageUser(playerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            LangText text;
            if (grant) {
                user.addKit(kit);
                text = Lang.KIT_GRANT_DONE;
            }
            else {
                user.removeKit(kit);
                text = Lang.KIT_REVOKE_DONE;
            }

            plugin.getUserManager().save(user);

            context.send(text, replacer -> replacer.replace(kit.replacePlaceholders()).replace(Placeholders.PLAYER_NAME, user.getName()));
        });
        return true;
    }
}
