/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.channel.bukkit.main;

import de.timesnake.channel.core.Channel;
import de.timesnake.channel.core.SyncRun;
import de.timesnake.channel.util.message.ChannelListenerMessage;
import de.timesnake.channel.util.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ChannelBukkit extends JavaPlugin {

    public static void start(String serverName, Integer proxyPort) {
        Channel.setInstance(new Channel(Thread.currentThread(), serverName, Bukkit.getPort(), proxyPort) {
            @Override
            public void runSync(SyncRun syncRun) {
                if (getPlugin().isEnabled()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            syncRun.run();
                        }
                    }.runTask(getPlugin());
                }
            }
        });

        Channel.getInstance().start();

        //request proxy for server listener
        Channel.getInstance().sendMessageToProxy(new ChannelListenerMessage<>(Channel.getInstance().getSelf(),
                MessageType.Listener.REGISTER_SERVER, Channel.getInstance().getServerName()));
    }

    public static void stop() {
        if (Channel.getInstance() != null) {
            Channel.getInstance().stop();
        }
    }

    public static ChannelBukkit getPlugin() {
        return plugin;
    }

    private static ChannelBukkit plugin;

    @Override
    public void onEnable() {
        plugin = this;
    }
}
