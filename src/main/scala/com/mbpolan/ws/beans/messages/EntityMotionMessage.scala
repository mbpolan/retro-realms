package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Message that represents an entity that has started or stopped moving.
  *
  * @param ref The internal ID of the entity.
  * @param start true if the entity is now moving, false if stopped.
  */
case class EntityMotionMessage(
    @BeanProperty ref: Int,
    @BeanProperty start: Boolean)
  extends Message(MessageType.EntityMotion.id)