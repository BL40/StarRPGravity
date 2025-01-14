package dev.bronzylobster.starrpgravity.commands;

import dev.bronzylobster.starrpcore.Commands.AbstractCommand;
import dev.bronzylobster.starrpcore.Utils.MessageManager;
import dev.bronzylobster.starrpcore.Utils.Utils;
import dev.bronzylobster.starrpgravity.StarRPGravity;
import dev.bronzylobster.starrpgravity.completers.GravityCompleter;
import dev.bronzylobster.starrpgravity.managers.GravityManager;
import org.bukkit.command.CommandSender;

public class Gravity extends AbstractCommand {

    private final GravityManager gravityManager;
    private final MessageManager messageManager;

    public Gravity() {
        super("gravity", new GravityCompleter(), StarRPGravity.getInstance());
        gravityManager = StarRPGravity.getGravityManager();
        messageManager = StarRPGravity.getMessageManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("invalid-usage", null)));
            return;
        }

        String selector = args[0];
        double gravityMultiplier;

        try {
            gravityMultiplier = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("invalid-number", null)));
            return;
        }

        long durationTicks = 0;
        if (args.length >= 3) {
            durationTicks = Utils.parseTime(args[2]);
            if (durationTicks < 0) {
                sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("invalid-time-format", null)));
                return;
            }
        }

        gravityManager.applyGravity(sender, selector, gravityMultiplier, durationTicks);
    }
}
