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
./gradlew run
or
java -jar build/libs/*-all.jar
```

The ChatServerWebSocket class has been copied from here:
https://docs.micronaut.io/latest/guide/index.html#websocketServer

## Test Sending Messages 
```
wscat -c ws://localhost:8080/chat/room1/user1
``` 

## Links
- [Micronaut](https://docs.micronaut.io/)
- [wscat](https://www.npmjs.com/package/wscat)
- [showdown](http://showdownjs.com/)

