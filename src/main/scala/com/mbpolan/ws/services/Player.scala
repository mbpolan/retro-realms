package com.mbpolan.ws.services

import com.mbpolan.ws.beans.messages.CreatureMoveResult.{Blocked, TooSoon, Valid}
import com.mbpolan.ws.beans.messages.{CreatureMoveResult, Message, PlayerMoveResultMessage}
import org.springframework.messaging.simp.SimpMessagingTemplate

/** Model for a player in the game world.
  *
  * @param socket The web socket that the player is connected over.
  * @param sessionId The player's session ID.
  * @param ref The internal ID assigned to this creature.
  * @param id The ID of the sprite for the creature.
  * @param name The visible name for the creature.
  * @param pos The current position of the creature on the map.
  */
class Player(
    private val socket: SimpMessagingTemplate,
    private val sessionId: String,
    ref: Int,
    id: String,
    name: String,
    pos: Rect)
  extends Creature(ref: Int, id: String, name: String, pos: Rect, Direction.Down, 50) {

  var moveRequested = false
  var lastMoveResult: CreatureMoveResult = CreatureMoveResult.Valid

  /** Sends a message to the player's client.
    *
    * @param message The message to dispatch.
    */
  def send(message: Message): Unit = {
    socket.convertAndSend(s"/topic/user/$sessionId/message", message)
  }

  /** Causes the entity to reevaluate its current state and take actions.
    *
    * @param game The instance of the game engine to work against.
    */
  override def tick(game: GameService): Unit = {
    // if a move has been requested and we haven't yet started moving, schedule a new movement now
    if (moveRequested && nextMove.isEmpty) {
      moveRequested = false
      planMove(game)
    }
  }

  /** Plans the player's next movement based on current state.
    *
    * @param game THe instance of the game engine to work against.
    */
  private def planMove(game: GameService): Unit = {
    lazy val processMoveResult: (CreatureMoveResult) => Unit = (result) => {
      result match {

        // if the movement was valid, continue moving in the current direction
        case Valid =>
          game.scheduleCreatureMove(this, processMoveResult)

        // otherwise, stop moving the player
        case TooSoon | Blocked =>
          game.stopCreature(this)
      }

      // notify the player if the result of this movement was different than before
      if (result != lastMoveResult) {
        lastMoveResult = result
        send(PlayerMoveResultMessage(result.id))
      }
    }

    processMoveResult(Valid)
  }
}
