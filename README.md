# playWebSocket
play with websocket server and clients

# Build the websocket service (a war file)
It provides an echo serveice of websocket. (copied from Tomcat example)

### build the war file
```bash
cd server
mvn package
cp target/echo.war TOMCAT_DIR/webapps
```

### access it
Access the echo websocket service through the web client:
```terminal
http://<tomcat_server_host>:<port>/echo/echo.xhtml
```

### access it via the golang client
```terminal
cd client
go build
./client --serverHost http://127.0.0.1:8080 --path /echo/websocket/echoAnnotation
```

# Access it via Httpd proxy

## set up the websocket proxy
Add the following into the configuration for Apache Httpd(change the tomcat port 8080 if necessary).

```bash
LoadModule proxy_module modules/mod_proxy.so
LoadModule proxy_wstunnel_module modules/mod_proxy_wstunnel.so

<IfModule mod_proxy_wstunnel.c>
ProxyPass /vmturbo/remoteMediation ws://localhost:8080/vmturbo/remoteMediation
ProxyPassReverse /vmturbo/remoteMediation ws://localhost:8080/vmturbo/remoteMediation

ProxyPass /echo/websocket/echoAnnotation ws://localhost:8080/echo/websocket/echoAnnotation
ProxyPassReverse /echo/websocket/echoAnnotation ws://localhost:8080/echo/websocket/echoAnnotation

ProxyPass /echo/websocket/echoProgrammatic ws://localhost:8080/echo/websocket/echoProgrammatic
ProxyPassReverse /echo/websocket/echoProgrammatic ws://localhost:8080/echo/websocket/echoProgrammatic
</IfModule>
```
## access the websocket via httpd proxy
```terminal
cd client
# go build
./client --serverHost http://127.0.0.1:9191 --path /echo/websocket/echoAnnotation
# 9191 is the port for apache httpd
```
