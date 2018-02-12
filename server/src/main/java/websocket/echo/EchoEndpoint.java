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

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.Session;

public class EchoEndpoint extends Endpoint {
    private final static Logger logger = Logger.getLogger(EchoEndpoint.class.getName());

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        String prefix = String.format("Programmatic websocket %s ", session.getId());
        logger.info(prefix + "is onOpen. ");

        RemoteEndpoint.Basic remoteEndpointBasic = session.getBasicRemote();
        session.addMessageHandler(new EchoMessageHandlerText(remoteEndpointBasic));
        session.addMessageHandler(new EchoMessageHandlerBinary(remoteEndpointBasic));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        String prefix = String.format("Programmatic websocket %s ", session.getId());
        logger.info(prefix + " is onClose: " + closeReason.toString());
    }

    public void onError(Session session, Throwable thr) {
        String prefix = String.format("Programmatic websocket %s ", session.getId());
        logger.warning(prefix + "is onError.");
    }

    private static class EchoMessageHandlerText
            implements MessageHandler.Partial<String> {

        private final RemoteEndpoint.Basic remoteEndpointBasic;

        private EchoMessageHandlerText(RemoteEndpoint.Basic remoteEndpointBasic) {
            this.remoteEndpointBasic = remoteEndpointBasic;
        }

        @Override
        public void onMessage(String message, boolean last) {
            String prefix = "Programmatic websocket ";
            logger.warning(prefix + "received text msg.");

            try {
                if (remoteEndpointBasic != null) {
                    remoteEndpointBasic.sendText(message, last);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static class EchoMessageHandlerBinary
            implements MessageHandler.Partial<ByteBuffer> {

        private final RemoteEndpoint.Basic remoteEndpointBasic;

        private EchoMessageHandlerBinary(RemoteEndpoint.Basic remoteEndpointBasic) {
            this.remoteEndpointBasic = remoteEndpointBasic;
        }

        @Override
        public void onMessage(ByteBuffer message, boolean last) {
            try {
                if (remoteEndpointBasic != null) {
                    remoteEndpointBasic.sendBinary(message, last);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
