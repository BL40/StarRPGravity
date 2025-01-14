package dev.bronzylobster.starrpgravity.managers;

import dev.bronzylobster.starrpcore.Utils.MessageManager;
import dev.bronzylobster.starrpgravity.StarRPGravity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GravityManager {

    private final JavaPlugin plugin;
    private final GravityDataManager dataManager;
    private final MessageManager messageManager;
    @Getter
    private final Map<UUID, Double> customGravityPlayers = new HashMap<>();
    @Getter
    private final Map<UUID, Long> gravityExpiryTimes = new HashMap<>();
    @Setter
    @Getter
    private int updateInterval;
    @Getter
    private double gravityThreshold;

    private static final double DEFAULT_GRAVITY = -0.08;

    public GravityManager(JavaPlugin plugin, int updateInterval) {
        this.plugin = plugin;
        this.dataManager = StarRPGravity.getDataManager();
        this.messageManager = StarRPGravity.getMessageManager();
        this.updateInterval = updateInterval;
        this.gravityThreshold = plugin.getConfig().getDouble("gravity-threshold");

        // Загрузка данных о гравитации
        customGravityPlayers.putAll(dataManager.loadData(gravityExpiryTimes));
    }

    public void applyGravity(CommandSender sender, String selector, double gravityMultiplier, long durationTicks) {
        List<Entity> entities;
        try {
            entities = Bukkit.selectEntities(sender, selector);
        } catch (IllegalArgumentException e) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("selector", selector);
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("invalid-selector", placeholders)));
            return;
        }

        if (entities.isEmpty()) {
            sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("no-entities-found", null)));
            return;
        }

        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof ArmorStand) {
                continue;
            }

            entity.setGravity(false);
            customGravityPlayers.put(entity.getUniqueId(), gravityMultiplier);

            if (durationTicks > 0) {
                gravityExpiryTimes.put(entity.getUniqueId(), System.currentTimeMillis() + (durationTicks * 50L));
            }

            if (entity instanceof Player) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("gravity", String.valueOf(gravityMultiplier));
                placeholders.put("time", formatTime(durationTicks));
                ((Player) entity).sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("gravity-changed", placeholders)));
            }

            count++;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("count", String.valueOf(count));
        sender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("gravity-applied", placeholders)));
    }

    public void resetGravity(CommandSender sender, UUID entityId) {
        Entity entity = Bukkit.getEntity(entityId);
        if (entity != null) {
            entity.setGravity(true);
            entity.setVelocity(new Vector(0, 0, 0));
            customGravityPlayers.remove(entityId);
            gravityExpiryTimes.remove(entityId);

            if (entity instanceof Player) {
                ((Player) entity).sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders("gravity-reset", null)));
            }
        }
    }

    public void resetAllGravity() {
        for (UUID entityId : customGravityPlayers.keySet()) {
            Entity entity = Bukkit.getEntity(entityId);
            if (entity != null) {
                entity.setGravity(true);
                entity.setVelocity(new Vector(0, 0, 0));
            }
        }
        customGravityPlayers.clear();
        gravityExpiryTimes.clear();
    }

    public void startGravityTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                for (Map.Entry<UUID, Double> entry : customGravityPlayers.entrySet()) {
                    UUID entityId = entry.getKey();
                    double gravityMultiplier = entry.getValue();

                    if (gravityExpiryTimes.containsKey(entityId) && gravityExpiryTimes.get(entityId) <= currentTime) {
                        resetGravity(null, entityId);
                        continue;
                    }

                    Entity entity = Bukkit.getEntity(entityId);
                    if (entity != null && entity.isValid()) {
                        if (entity.isOnGround()) {
                            Vector currentVelocity = entity.getVelocity();
                            entity.setVelocity(new Vector(currentVelocity.getX(), 0, currentVelocity.getZ()));
                        } else {
                            Vector currentVelocity = entity.getVelocity();
                            Vector newVelocity = currentVelocity.add(new Vector(0, DEFAULT_GRAVITY * gravityMultiplier, 0));
                            entity.setVelocity(newVelocity);
                        }
                    } else {
                        customGravityPlayers.remove(entityId);
                        gravityExpiryTimes.remove(entityId);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, updateInterval);
    }

    private String formatTime(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes + "m " + seconds + "s";
    }
}

