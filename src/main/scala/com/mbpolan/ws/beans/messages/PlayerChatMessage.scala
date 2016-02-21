package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Message that informs clients that a player has send a chat message.
  *
  * @param ref The internal ID of the player that sent the message.
  * @param name The name of the player that sent the message.
  * @param text The content of the message.
  *
  * @author Mike Polan
  */
case class PlayerChatMessage(
    @BeanProperty ref: Long,
    @BeanProperty name: String,
    @BeanProperty text: String)
  extends Message(MessageType.PlayerChat.id)