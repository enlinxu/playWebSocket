/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package websocket.echo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import java.nio.charset.Charset;

import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * The three annotated echo endpoints can be used to test with Autobahn and
 * the following command "wstest -m fuzzingclient -s servers.json". See the
 * Autobahn documentation for setup and general information.
 */
@ServerEndpoint("/websocket/echoAnnotation")
public class EchoAnnotation {
    private final static Logger logger = Logger.getLogger(EchoAnnotation.class.getName());

    @OnOpen
    public void onOpen(Session ss) {
        logger.warning("Annotated websocket is on open.");
    }

    @OnClose
    public void onClose(Session session) {
        logger.warning("Annotated websocket is closed.");
    }

    @OnError
    public void onError(Session session, Throwable t) {
        logger.warning("Annotated websocket is error.");
    }

    protected String bigMsg(String seed) {
        StringBuilder sb = new StringBuilder();
        int baselen = seed.length() + 1;
        int maxlen = 20240;
        int current = 0;
        while (current < maxlen) {
            sb.append(seed);
            sb.append(";");
            current += baselen;
        }
        sb.append(current);

        return sb.toString();
    }

    protected ByteBuffer bigBinary(ByteBuffer bb) {
        String dat = "hello";
        try {
            dat = new String(bb.array());
        } catch (Exception e) {
            e.printStackTrace();
            dat = "error";
        }
        String big = bigMsg(dat);

        logger.info("binary dat: " + dat);
        return ByteBuffer.wrap(big.getBytes(Charset.forName("UTF-8")));
    }

    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last) {
        logger.info("Annotated websocket received text msg.");

        try {
            if (session.isOpen()) {
                //session.getBasicRemote().sendText(bigMsg(msg), last);
                session.getBasicRemote().sendText(msg, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    @OnMessage
    public void echoBinaryMessage(Session session, ByteBuffer bb,
            boolean last) {
        try {
            if (session.isOpen()) {
                //session.getBasicRemote().sendBinary(bigBinary(bb), last);
                session.getBasicRemote().sendBinary(bb, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    /**
     * Process a received pong. This is a NO-OP.
     *
     * @param pm    Ignored.
     */
    @OnMessage
    public void echoPongMessage(PongMessage pm) {
        logger.warning("Annotated websocket received pong msg");
        // NO-OP
    }
}
