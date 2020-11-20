package micronaut.ws.chat;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

import java.util.Objects;
import java.util.function.Predicate;

@ServerWebSocket(ChatServerWebSocket.chatEndpoint)
public class ChatServerWebSocket {
    final static String chatEndpoint = "/chat/{topic}/{username}";
    final private WebSocketBroadcaster broadcaster;

    public ChatServerWebSocket(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen 
    public void onOpen(String topic, String username, WebSocketSession session) {
        String msg = "[" + username + "] Connected!";
        broadcaster.broadcastAsync(msg, isValid(topic, session));
    }

    @OnMessage 
    public void onMessage(
            String topic,
            String username,
            String message,
            WebSocketSession session) {
        String msg = "[" + username + "]\n" + message;
  //      session.sendAsync(String.format("You sent: %s", message));
        broadcaster.broadcastAsync(msg, isValid(topic, session));
    }

    @OnClose 
    public void onClose(
            String topic,
            String username,
            WebSocketSession session) {
        String msg = "[" + username + "] Disconnected!";
//        broadcaster.broadcastAsync(msg, isValid(topic, session));
    }

    private Predicate<WebSocketSession> isValid(String topic, WebSocketSession session) {
        //return s -> /*s != session &&*/ topic.equalsIgnoreCase(s.getUriVariables().get("topic", String.class, null));
        return Objects::nonNull;
    }
}