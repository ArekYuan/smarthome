package cn.interlinx.iot;

import java.nio.channels.SocketChannel;

public class ChannelClient {
    private String key;
    private SocketChannel channel;

    public ChannelClient(String key, SocketChannel channel) {
        this.key = key;
        this.channel = channel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }
}
