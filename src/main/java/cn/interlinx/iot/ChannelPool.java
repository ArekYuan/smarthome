package cn.interlinx.iot;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelPool {

    public static final ConcurrentHashMap<String, ChannelClient> channelMap = new ConcurrentHashMap<>();

    public static void add(ChannelClient clientSocket) {
        if (clientSocket != null && !clientSocket.getKey().isEmpty())
            channelMap.put(clientSocket.getKey(), clientSocket);
    }

    public static void remove(String key) {
        if (!key.isEmpty())
            channelMap.remove(key);
    }

    public static ChannelClient getChannelClient(String key) {
        ChannelClient clientSocket = null;
        if (!key.isEmpty()) {
            clientSocket = channelMap.get(key);
        }
        return clientSocket;
    }


    public static String getKey( SocketChannel value) {
        String key = null;
        for (String getKey : channelMap.keySet()) {
            if (channelMap.get(getKey).getChannel().equals(value)) {
                key = getKey;
            }
        }
        return key;
    }


}
