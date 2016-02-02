package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Message sent to clients when a player move request was received.
  *
  * @param valid true if the movement is valid, false if not.
  * @param ref The ID of the moving creature.
  * @param x The new x coordinate.
  * @param y The new y coordinate.
  *
  * @author Mike Polan
  */
case class PlayerMoveMessage(
    @BeanProperty valid: Boolean,
    @BeanProperty ref: Integer,
    @BeanProperty x: Int,
    @BeanProperty y: Int)
  extends Message(MessageType.MovePlayer.id)