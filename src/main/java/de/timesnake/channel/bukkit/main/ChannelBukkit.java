/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.channel.bukkit.main;

import com.moandjiezana.toml.Toml;
import de.timesnake.channel.util.ChannelConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.Duration;

public class ChannelBukkit extends JavaPlugin {

  public static void start(String serverName) {
    ServerChannel.setInstance(new ServerChannel(Thread.currentThread(), config, serverName, Bukkit.getPort()) {
      @Override
      public void runSync(Runnable runnable) {
        if (getPlugin().isEnabled()) {
          new BukkitRunnable() {
            @Override
            public void run() {
              runnable.run();
            }
          }.runTask(getPlugin());
        }
      }
    });

    Configurator.setAllLevels(ServerChannel.getInstance().getLogger().getName(), Level.WARN);

    ServerChannel.getInstance().start();
    ServerChannel.getInstance().registerToNetwork(Duration.ofSeconds(3));
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
