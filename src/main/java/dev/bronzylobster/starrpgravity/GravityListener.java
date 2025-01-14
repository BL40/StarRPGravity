package dev.bronzylobster.starrpgravity;

import dev.bronzylobster.starrpcore.Utils.MessageManager;
import dev.bronzylobster.starrpgravity.managers.GravityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class GravityListener implements Listener {
    private final GravityManager gravityManager;
    private final MessageManager messageManager;

    public GravityListener() {
        this.gravityManager = StarRPGravity.getGravityManager();
        this.messageManager = StarRPGravity.getMessageManager();
    }

    @EventHandler
    public void onPlayerJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        double gravityMultiplier = gravityManager.getCustomGravityPlayers().getOrDefault(player.getUniqueId(), 1.0);

        if (Math.abs(gravityMultiplier) < gravityManager.getGravityThreshold()) {
            Vector velocity = player.getVelocity();
            velocity.setY(0.5);
            player.setVelocity(velocity);
            event.setCancelled(true);
            player.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("move-up", null)));
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        double gravityMultiplier = gravityManager.getCustomGravityPlayers().getOrDefault(player.getUniqueId(), 1.0);

        if (Math.abs(gravityMultiplier) < gravityManager.getGravityThreshold()) {
            Vector velocity = player.getVelocity();
            velocity.setY(-0.5);
            player.setVelocity(velocity);
            event.setCancelled(true);
            player.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("move-down", null)));
        }
    }
}
