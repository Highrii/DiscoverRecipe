package com.highrii.discoverrecipe;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.ArrayList;

public class DiscoverRecipe extends JavaPlugin implements Listener {

    private List<NamespacedKey> recipes = new ArrayList<>();

    @Override
    public void onEnable() {
        loadRecipes(); // 初始加载配方
        Bukkit.getPluginManager().registerEvents(this, this); // 注册事件监听器
    }

    @Override
    public void onDisable() {
        recipes.clear(); // 卸载时清空配方列表
    }

    private void loadRecipes() {
        recipes.clear(); // 清空现有配方列表
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof Keyed) {
                recipes.add(((Keyed) recipe).getKey());
            }
        });
    }

    private void discoverRecipes(Player player) {
        for (NamespacedKey namespacedKey : recipes) {
            if (!player.hasDiscoveredRecipe(namespacedKey)) {
                player.discoverRecipe(namespacedKey);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            return; // 如果玩家之前玩过，跳过
        }
        // 玩家首次加入服务器时，解锁所有配方
        discoverRecipes(player);
        //player.sendMessage("欢迎来到服务器！已为您解锁所有配方！"); //是否发送message欢迎语
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("discoverRecipe")) {
            if (args.length == 0) {
                // 如果没有指定玩家，解锁所有在线玩家的配方
                for (Player player : Bukkit.getOnlinePlayers()) {
                    discoverRecipes(player);
                }
                sender.sendMessage("已为所有在线玩家解锁所有配方");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("run")) {
                // 如果指定了玩家，解锁该玩家的配方
                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer != null) {
                    discoverRecipes(targetPlayer);
                    sender.sendMessage("已为玩家 " + targetPlayer.getName() + " 解锁所有配方");
                } else {
                    sender.sendMessage("未找到指定的玩家 " + args[1]);
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                // 重新加载所有配方
                loadRecipes();
                sender.sendMessage("配方已重新加载 recode by Highrii");
            } else {
                return false; // 参数错误，返回帮助信息
            }
            return true;
        }
        return false;
    }
}
