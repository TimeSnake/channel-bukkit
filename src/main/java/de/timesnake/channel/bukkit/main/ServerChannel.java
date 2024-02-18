/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.channel.bukkit.main;

import de.timesnake.channel.core.Channel;
import de.timesnake.channel.core.ChannelParticipant;
import de.timesnake.channel.util.ChannelConfig;
import de.timesnake.channel.util.listener.ChannelHandler;
import de.timesnake.channel.util.listener.ChannelListener;
import de.timesnake.channel.util.listener.ListenerType;
import de.timesnake.channel.util.message.ChannelServerMessage;
import de.timesnake.channel.util.message.MessageType;
import de.timesnake.channel.util.message.VoidMessage;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

public abstract class ServerChannel extends Channel implements ChannelListener {

  public static ServerChannel getInstance() {
    return (ServerChannel) Channel.getInstance();
  }

  protected final @NotNull String serverName;
  protected final ChannelConfig config;

  protected ServerChannel(@NotNull Thread mainThread, @NotNull ChannelConfig config, @NotNull String serverName,
                          int serverPort) {
    super(mainThread, new ChannelParticipant(config.getServerHostName(), serverPort + config.getPortOffset()),
        config.getListenHostName());

    this.serverName = serverName;
    this.config = config;

    this.addListener(this, List.of(serverName));
  }

  public void registerToNetwork(Duration retryPeriod) {
    this.registerToNetwork(new ChannelParticipant(config.getProxyHostName(), config.getProxyPort()), retryPeriod);
  }

  @ChannelHandler(type = ListenerType.SERVER_PING, filtered = true, async = true)
  public void onServerPingMessage(ChannelServerMessage<VoidMessage> msg) {
    this.getSender().sendMessageSync(msg.getSource(), new ChannelServerMessage<>(this.serverName,
        MessageType.Server.PONG));
  }
}
