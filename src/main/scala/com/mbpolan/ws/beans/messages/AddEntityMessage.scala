package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Message sent to clients when a new entity has appeared on the map.
  *
  * @param ref The internal ID of the entity.
  * @param id The sprite ID of the entity.
  * @param name The visible name of the entity.
  * @param dir The direction the entity is facing.
  * @param x The x coordinate of the entity.
  * @param y The y coordinate of the entity.
  * @param speed The amount of milliseconds between entity movements.
  */
case class AddEntityMessage(
    @BeanProperty ref: Integer,
    @BeanProperty id: String,
    @BeanProperty name: String,
    @BeanProperty dir: String,
    @BeanProperty x: Int,
    @BeanProperty y: Int,
    @BeanProperty speed: Int)
  extends Message(MessageType.AddEntity.id)