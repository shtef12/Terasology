/*
 * Copyright 2013 Moving Blocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.logic.chat;

import org.terasology.network.NetworkEvent;
import org.terasology.network.Replicate;
import org.terasology.network.ServerEvent;

/**
 * This event is used to send a chat message to the server, from which it is broadcast to all players.
 * @author Immortius
 */
@ServerEvent
public class SendChatMessage extends NetworkEvent {
    @Replicate
    private String message;

    private SendChatMessage() {
    }

    public SendChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{message = '" + message + "'}";
    }
}
