package dev.bronzylobster.starrpgravity.completers;

import dev.bronzylobster.starrpgravity.StarRPGravity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GravityCompleter implements TabCompleter {

    FileConfiguration config = StarRPGravity.getInstance().getConfig();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        List<String> result = new ArrayList<>();

        switch (strings.length){
            case 1:
                result.add("@a");
                result.add("@p");
                result.add("@r");
                result.add("@e");
                Bukkit.getOnlinePlayers().forEach(player -> result.add(player.getName()));
                break;
            case 2:
                result.add(config.getString("messages.gravity-multiplier"));
                result.add("0.1");
                result.add("0.2");
                result.add("0.5");
                result.add("1.0");
                result.add("2.0");
                result.add("5.0");
                break;
            default:
                result.add("1M");
                result.add("1w");
                result.add("1d");
                result.add("1h");
                result.add("1m");
                result.add("1s");
                break;
        }
        return result;
    }
}
