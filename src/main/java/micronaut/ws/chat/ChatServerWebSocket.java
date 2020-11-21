package micronaut.ws.chat;

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
        broadcaster.broadcastAsync(
            new Message(userName, ContentType.text, "Connected").toJson());
    }

    @OnMessage 
    public void onMessage(
            String topic,
            String userName,
            String messageJson,
            WebSocketSession session) {
        Message message = new Message();
        message.fromJson(messageJson);
        // ensure user and time data are from server
        message.userName = userName;
        message.timeStamp = ZonedDateTime.now(ZoneOffset.UTC);
        broadcaster.broadcastAsync(message.toJson());
    }

    @OnClose 
    public void onClose(
            String topic,
            String userName,
            WebSocketSession session) {
        broadcaster.broadcastAsync(
            new Message(userName, ContentType.text, "Disconnected").toJson());
    }

    enum ContentType { text, markdown }
    private static class Message implements JsonAware {
        public Message() {}
        public Message(String userName, ContentType contentType, String content) {
            this.userName = userName;
            this.contentType = contentType;
            this.content = content;
        }

        public ZonedDateTime timeStamp = ZonedDateTime.now(ZoneOffset.UTC);
        public String userName;
        public ContentType contentType;
        public String content;
    }
}