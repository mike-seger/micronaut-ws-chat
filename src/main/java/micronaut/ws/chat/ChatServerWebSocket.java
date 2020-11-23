package micronaut.ws.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@ServerWebSocket(ChatServerWebSocket.chatEndpoint)
public class ChatServerWebSocket {
    final static String chatEndpoint = "/chat/{topic}/{userName}";
    final private WebSocketBroadcaster broadcaster;

    public ChatServerWebSocket(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen 
    public void onOpen(String topic, String userName, WebSocketSession session) {
        broadcastRaw(userName,"connected");
    }

    @OnMessage 
    public void onMessage(
            String topic,
            String userName,
            String messageJson,
            WebSocketSession session) {
        if(messageJson.trim().startsWith("{")) {
            try {
                Message message = new Message(messageJson);
                // ensure user and time values are from server
                message.userName = userName;
                message.timeStamp = ZonedDateTime.now(ZoneOffset.UTC);
                broadcaster.broadcastAsync(message.toJson());
            } catch(Exception e) {
                broadcastRaw(userName, messageJson);
            }
        } else {
            broadcastRaw(userName, messageJson);
        }
    }

    @OnClose 
    public void onClose(
            String topic,
            String userName,
            WebSocketSession session) {
        broadcastRaw(userName,  "disconnected");
    }

    private void broadcastRaw(String userName, String content) {
        String timeStamp = ZonedDateTime.now(ZoneOffset.UTC)
            .toOffsetDateTime().toString();
        timeStamp = timeStamp.substring(0, Math.min(23, timeStamp.length()))
            .replace("T", " ")+"Z";
        broadcaster.broadcastAsync(String.format("%s - %s:\n%s", timeStamp, userName, content));
    }

    enum ContentType { text, markdown }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Message implements JsonAware<Message> {
        public Message() {}
        public Message(String json) {
            fromJson(json);
        }

        public ZonedDateTime timeStamp = ZonedDateTime.now(ZoneOffset.UTC);
        public String userName;
        public ContentType contentType;
        public String content;
    }
}