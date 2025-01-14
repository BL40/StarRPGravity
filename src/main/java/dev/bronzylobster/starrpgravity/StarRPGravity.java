package dev.bronzylobster.starrpgravity;

import dev.bronzylobster.starrpcore.Utils.MessageManager;
import dev.bronzylobster.starrpgravity.commands.Gravity;
import dev.bronzylobster.starrpgravity.commands.ResetGravity;
import dev.bronzylobster.starrpgravity.commands.SetGravityInterval;
import dev.bronzylobster.starrpgravity.managers.GravityDataManager;
import dev.bronzylobster.starrpgravity.managers.GravityManager;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class StarRPGravity extends JavaPlugin {

    @Getter
    private static GravityDataManager dataManager;
    @Getter
    private static GravityManager gravityManager;
    @Getter
    private static MessageManager messageManager;
    @Getter
    private static StarRPGravity instance;

    @Override
    public void onEnable() {
        // Сохранение конфигурации, если она не существует
        saveDefaultConfig();

        instance = this;

        // Инициализация менеджера данных
        dataManager = new GravityDataManager();

        // Инициализация менеджера сообщений
        messageManager = new MessageManager(this);

        // Инициализация менеджера гравитации
        gravityManager = new GravityManager(this, getConfig().getInt("update-interval"));

        // Регистрация слушателя событий
        getServer().getPluginManager().registerEvents(new GravityListener(), this);

        // Регистрация команд
        new Gravity();
        new ResetGravity();
        new SetGravityInterval();

        // Запуск задачи для обработки гравитации
        gravityManager.startGravityTask();
    }

    @Override
    public void onDisable() {
        // Сохранение данных о гравитации
        dataManager.saveData(gravityManager.getCustomGravityPlayers(), gravityManager.getGravityExpiryTimes());

        // Сброс гравитации для всех сущностей при отключении плагина
        gravityManager.resetAllGravity();
    }
}
