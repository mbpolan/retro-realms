package com.mbpolan.ws.services

import com.mbpolan.ws.beans.messages.CreatureMoveResult
import com.mbpolan.ws.beans.messages.CreatureMoveResult.{Blocked, TooSoon, Valid}

import scala.util.Random

/** Model for a non-player controlled character in the game world.
  *
  * @param ref The internal ID assigned to this creature.
  * @param id The ID of the sprite for the creature.
  * @param name The visible name for the creature.
  * @param pos The current position of the creature on the map.
  * @param dir The direction the creature is facing.
  * @param speed The speed at which the creature moves.
  * @author Mike Polan
  */
class Npc(
    ref: Int,
    id: String,
    name: String,
    pos: Rect,
    dir: Direction,
    speed: Int)
  extends Creature(ref: Int, id: String, name: String, pos: Rect, dir: Direction, speed: Int) {

  // the directions this NPC is capable of moving in
  private val MoveDirections: List[Direction] = List(Direction.Up, Direction.Down, Direction.Left, Direction.Right)

  // the amount of moves planned in the current direction
  var plannedMoves = 0

  /** Triggers the creature to reevaluate its current state and perform any actions.
    *
    * @param game Instance of [[GameService]] to work against.
    */
  override def tick(game: GameService): Unit = {
    Random.nextInt(100) match {
      // one in three chance of moving
      case i if i >= 0 && i <= 30 && isReadyToMove =>
        planMove(game)

      // otherwise do nothing
      case _ =>
    }
  }

  /** Determines if the creature is ready to make another move.
    *
    * @return true if the creature is ready to move, false if not.
    */
  private def isReadyToMove: Boolean = !isMoving && plannedMoves == 0

  /** Plans the creature's next movement.
    *
    * @param game Instance of [[GameService]] to work against.
    */
  private def planMove(game: GameService): Unit = {
    // randomize the amount of steps to take and the direction in which to move
    plannedMoves = Random.nextInt(10) + 3
    moveDir = MoveDirections(Random.nextInt(MoveDirections.size))

    lazy val processMoveResult: (CreatureMoveResult) => Unit = {

      // was the last movement successful?
      case r if r == Valid =>
        // schedule the next if we still have moves planned
        if (plannedMoves > 0) {
          game.scheduleCreatureMove(this, processMoveResult)
          plannedMoves -= 1
        }

        // stop moving if not
        else {
          game.stopCreature(this)
        }

      // the last movement was either blocked or stopped by an external factor
      case TooSoon | Blocked =>
        plannedMoves = 0
        game.stopCreature(this)
    }

    // start moving right away
    processMoveResult(Valid)
  }
}
