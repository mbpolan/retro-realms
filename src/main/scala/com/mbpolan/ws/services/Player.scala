package com.mbpolan.ws.services

import com.mbpolan.ws.beans.messages.{CreatureMoveResult, Message}
import org.springframework.messaging.simp.SimpMessagingTemplate

/** Model for a player in the game world.
  *
  * @param sessionId The player's session ID.
  * @param ref The internal ID assigned to this creature.
  * @param id The ID of the sprite for the creature.
  * @param name The visible name for the creature.
  * @param pos The current position of the creature on the map.
  */
class Player(
    private val sessionId:
    String, ref: Int,
    id: String,
    name: String,
    pos: Rect)
  extends Creature(ref: Int, id: String, name: String, pos: Rect, Direction.Down, 50) {

  var lastMoveResult: CreatureMoveResult = CreatureMoveResult.Valid

  /** Sends a message to the player's client.
    *
    * @param webSocket The socket to send data over.
    * @param message The message to dispatch.
    */
  def send(webSocket: SimpMessagingTemplate, message: Message): Unit = {
    webSocket.convertAndSend(s"/topic/user/$sessionId/message", message)
  }
}
