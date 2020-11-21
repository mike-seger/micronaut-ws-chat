import ws from 'k6/ws';
import { check, sleep } from 'k6';

//export let options = {
//  stages: [
//    { duration: '10s', target: 200 }
//    , { duration: '30s', target: 200 }
//    , { duration: '10s', target: 0 }
//  ],
//};

export default function () {
  const url = 'ws://localhost:8881/chat/room1/user1';
  const params = { tags: { tag : 'micronaut-ws-chat' } };

  const res = ws.connect(url, params, function (socket) {
    socket.on('open', function open() {
//      console.log('connected');
//      socket.setInterval(function timeout() {
//        socket.ping();
//        console.log('Pinging every 1sec (setInterval test)');
//      }, 1000);
        socket.setInterval(function timeout() {
            socket.send({
              "content": "chat text user"
              ,"contentType":"text"
            });
        }, 1000);
    });

//    socket.setTimeout(function () {
//        socket.send({
//          "content": "chat text user"
//          ,"contentType":"text"
//        });
//    }, 1000);

    //socket.on('ping', () => console.log('PING!'));
    //socket.on('pong', () => console.log('PONG!'));

//    socket.on('message', (data) => console.log('Message received: ', data));
    socket.on('close', () => console.log('disconnected'));
  });

  check(res, { 'status is 101': (r) => r && r.status === 101 });
}
