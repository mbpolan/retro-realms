package com.mbpolan.ws.beans.messages

/** Message sent to the clients when an entity has been removed.
  *
  * @param ref The internal ID number of the entity.
  *
  * @author Mike Polan
  */
case class RemoveEntityMessage(ref: Int) extends Message(MessageType.RemoveEntity.id)