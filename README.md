# playWebSocket
play with websocket server and clients

# Build the websocket server (a war file)

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
