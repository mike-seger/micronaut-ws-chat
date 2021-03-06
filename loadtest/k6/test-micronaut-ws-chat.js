import ws from 'k6/ws';
import { check } from 'k6';

export default function () {
  const url = 'ws://localhost:8881/chat/room1/user1';
  const params = { tags: { tag : 'micronaut-ws-chat' } };

  const res = ws.connect(url, params, function (socket) {
    socket.on('open', function open() {
        socket.setInterval(function timeout() {
            socket.send({
              "content": "chat text user"
              ,"contentType":"text"
            });
        }, 100);
    });

//    socket.on('message', (data) => console.log('Message received: ', data));
    socket.on('close', () => console.log('disconnected'));
  });

  check(res, { 'status is 101': (r) => r && r.status === 101 });
}
