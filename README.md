# HelpingYourSelf – WebSocket STOMP realtime

This project now provides real‑time notifications using Spring WebSocket and STOMP.

## Why WebSocket/STOMP?

HTTP is request/response. For instant updates (chat, notifications, etc.) the server needs a persistent connection with the client. WebSocket with STOMP provides a lightweight pub/sub protocol over this connection, making it simple to broadcast messages to many clients.

## Running the app

```bash
mvn spring-boot:run
```

Then open [http://localhost:8080/ws-demo.html](http://localhost:8080/ws-demo.html) in a browser to try the demo client.

## Demo client snippet

```html
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
<script>
  const socket = new SockJS('/ws');
  const stompClient = Stomp.over(socket);
  stompClient.connect({}, () => {
    stompClient.subscribe('/topic/notifications', msg => {
      console.log(JSON.parse(msg.body));
    });
  });
  stompClient.send('/app/notify', {}, JSON.stringify({content: 'hello'}));
</script>
```

Clients publish to `/app/notify` and subscribe to `/topic/notifications`.

## Security & production notes

* CORS origins are configured via `app.cors.allowed-origins` (comma separated). Replace `*` with explicit domains in production.
* CSRF protection is disabled for `/ws/**` to allow the WebSocket handshake.
* The sample uses the in‑memory simple broker; switch to an external broker (RabbitMQ, etc.) for heavy workloads.

