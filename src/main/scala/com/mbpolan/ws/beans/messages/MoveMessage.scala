package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Message that informs clients that an entity has moved on the map.
  *
  * @author Mike Polan
  */
case class MoveMessage(
    @BeanProperty ref: Long,
    @BeanProperty x: Int,
    @BeanProperty y: Int)
  extends Message(MessageType.EntityMove.id)