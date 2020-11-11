package micronaut.ws.chat;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/")
public class RootController {
    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return String.format("micronaut.ws.echo\nChat Endpoint: %s", micronaut.ws.chat.ChatServerWebSocket.chatEndpoint);
    }
}