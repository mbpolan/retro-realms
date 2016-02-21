package com.mbpolan.ws.beans.messages

/** Enumeration of possible events that can occur while connected.
  *
  * @author Mike Polan
  */
sealed trait MessageType { def id: String }
object MessageType {
  case object AddEntity extends MessageType { val id = "AddEntity" }
  case object RemoveEntity extends MessageType { val id = "RemoveEntity" }
  case object MovePlayer extends MessageType { val id = "MovePlayer" }
  case object DirectionChange extends MessageType { val id = "DirectionChange" }
  case object EntityMove extends MessageType { val id = "EntityMove" }
  case object EntityMotion extends MessageType { val id = "EntityMotion" }
  case object PlayerChat extends MessageType { val id = "PlayerChat" }
}