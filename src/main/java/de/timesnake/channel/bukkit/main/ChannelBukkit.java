/*
 * workspace.channel-bukkit.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
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
