package de.timesnake.channel.bukkit.main;

import de.timesnake.channel.core.Channel;
import de.timesnake.channel.core.ChannelLogger;
import de.timesnake.channel.core.NetworkChannel;
import de.timesnake.channel.core.SyncRun;
import de.timesnake.channel.util.message.ChannelListenerMessage;
import de.timesnake.channel.util.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ChannelBukkit extends JavaPlugin {

    private static ChannelBukkit plugin;

    @Override
    public void onEnable() {
        plugin = this;
    }

    public static void start(Integer proxyPort) {
        NetworkChannel.start(new Channel(Thread.currentThread(), Bukkit.getPort(), proxyPort, new ChannelLogger() {
            @Override
            public void printInfo(String msg) {
                Bukkit.getLogger().info("[Channel] " + msg);
            }

            @Override
            public void printWarning(String msg) {
                Bukkit.getLogger().warning("[Channel] " + msg);
            }
        }) {
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

        NetworkChannel.getChannel().logInfo("Loaded network-channel", true);

        //request proxy for server listener
        NetworkChannel.getChannel().sendMessageToProxy(new ChannelListenerMessage<>(NetworkChannel.getChannel().getSelf(),
                MessageType.Listener.REGISTER_SERVER, NetworkChannel.getChannel().getServerPort()));
    }

    public static ChannelBukkit getPlugin() {
        return plugin;
    }
}
