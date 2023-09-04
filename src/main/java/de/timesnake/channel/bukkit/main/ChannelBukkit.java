/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.channel.bukkit.main;

import com.moandjiezana.toml.Toml;
import de.timesnake.channel.core.ServerChannel;
import de.timesnake.channel.core.SyncRun;
import de.timesnake.channel.util.ChannelConfig;
import de.timesnake.channel.util.message.ChannelHeartbeatMessage;
import de.timesnake.channel.util.message.ChannelListenerMessage;
import de.timesnake.channel.util.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.Duration;

public class ChannelBukkit extends JavaPlugin {

  public static void start(String serverName) {
    ServerChannel.setInstance(new ServerChannel(Thread.currentThread(), config, serverName, Bukkit.getPort()) {
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

      @Override
      public void onHeartBeatMessage(ChannelHeartbeatMessage<?> msg) {
        super.onHeartBeatMessage(msg);
        if (msg.getMessageType().equals(MessageType.Heartbeat.SERVER_PING)) {
          this.sendMessageToProxy(new ChannelHeartbeatMessage<>(this.getSelf(), MessageType.Heartbeat.SERVER_PONG, this.getServerName()));
        }
      }
    });

    ServerChannel.getInstance().start();

    //request proxy for server listener
    ServerChannel.getInstance().connectToProxy(new ChannelListenerMessage<>(ServerChannel.getInstance().getSelf(),
        MessageType.Listener.REGISTER_SERVER, ServerChannel.getInstance().getServerName()), Duration.ofSeconds(3));
  }

  public static void stop() {
    if (ServerChannel.getInstance() != null) {
      ServerChannel.getInstance().stop();
    }
  }

  public static ChannelBukkit getPlugin() {
    return plugin;
  }

  private static ChannelBukkit plugin;
  private static ChannelConfig config;

  @Override
  public void onEnable() {
    plugin = this;

    Toml toml = new Toml().read(new File("plugins/channel/config.toml"));

    config = new ChannelConfig() {
      @Override
      public String getServerHostName() {
        return toml.getString("host_name");
      }

      @Override
      public String getListenHostName() {
        return toml.getString("listen_host_name");
      }

      @Override
      public String getProxyHostName() {
        return toml.getString("proxy.host_name");
      }

      @Override
      public String getProxyServerName() {
        return toml.getString("proxy.server_name");
      }

      @Override
      public int getPortOffset() {
        return toml.getLong("port_offset").intValue();
      }

      @Override
      public int getProxyPort() {
        return toml.getLong("proxy.port").intValue();
      }
    };
  }
}
