package dev.bronzylobster.starrpgravity.commands;

import dev.bronzylobster.starrpcore.Commands.AbstractCommand;
import dev.bronzylobster.starrpcore.Utils.MessageManager;
import dev.bronzylobster.starrpgravity.StarRPGravity;
import dev.bronzylobster.starrpgravity.managers.GravityManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetGravity extends AbstractCommand {

    private final GravityManager gravityManager;
    private final MessageManager messageManager;
    public ResetGravity() {
        super("resetGravity", StarRPGravity.getInstance());
        gravityManager = StarRPGravity.getGravityManager();
        messageManager = StarRPGravity.getMessageManager();
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("command-player-only", null)));
            return;
        }

        Player player = (Player) sender;
        gravityManager.resetGravity(sender, player.getUniqueId());
    }
}
