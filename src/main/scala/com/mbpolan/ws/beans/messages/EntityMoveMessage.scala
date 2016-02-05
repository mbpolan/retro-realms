package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Message that informs clients that an entity has moved on the map.
  *
  * @param ref The internal ID of the entity.
  * @param x The new x coordinate of the entity.
  * @param y The new y coordinate of the entity.
  *
  * @author Mike Polan
  */
case class EntityMoveMessage(
    @BeanProperty ref: Long,
    @BeanProperty x: Int,
    @BeanProperty y: Int)
  extends Message(MessageType.EntityMove.id)