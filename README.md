# micronaut-ws-chat

This is a simple WebSocket chat server based on micronaut.
The project has been generated by:
```
mn create-app micronaut-ws-chat
```

## Build
```
./gradlew assemble
```

## Run
```
java -jar build/libs/*.jar
```

The ChatServerWebSocket class has been copied from here:
https://docs.micronaut.io/latest/guide/index.html#websocketServer

An additional self echo response has been added to onMessage:
session.sendAsync(String.format("You sent: %s", message));

## Test Sending Messages 
```
wscat -c ws://localhost:8080/chat/room1/user1
``` 

## Links
- [Micronaut](https://docs.micronaut.io/)
- [wscat](https://www.npmjs.com/package/wscat)
