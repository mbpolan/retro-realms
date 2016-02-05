package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Message sent to clients when a player move request was received.
  *
  * @param result The result of the movement action.
  *
  * @author Mike Polan
  */
case class PlayerMoveResultMessage(
    @BeanProperty result: String)
  extends Message(MessageType.MovePlayer.id)