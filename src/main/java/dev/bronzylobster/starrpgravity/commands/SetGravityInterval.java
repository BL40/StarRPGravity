package dev.bronzylobster.starrpgravity.commands;

import dev.bronzylobster.starrpcore.Commands.AbstractCommand;
import dev.bronzylobster.starrpcore.Utils.MessageManager;
import dev.bronzylobster.starrpgravity.StarRPGravity;
import dev.bronzylobster.starrpgravity.managers.GravityManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SetGravityInterval extends AbstractCommand {

    private final JavaPlugin plugin;
    private final GravityManager gravityManager;
    private final MessageManager messageManager;

    public SetGravityInterval() {
        super("setGravityInterval", StarRPGravity.getInstance());
        plugin = StarRPGravity.getInstance();
        gravityManager = StarRPGravity.getGravityManager();
        messageManager = StarRPGravity.getMessageManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("invalid-usage", null)));
            return;
        }

        try {
            int interval = Integer.parseInt(args[0]);
            if (interval < 1) {
                sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("invalid-interval", null)));
                return;
            }

            gravityManager.setUpdateInterval(interval);
            plugin.getConfig().set("update-interval", interval);
            plugin.saveConfig();

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("interval", String.valueOf(interval));
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("gravity-interval-changed", placeholders)));
        } catch (NumberFormatException e) {
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("invalid-number", null)));
        }
    }
}
