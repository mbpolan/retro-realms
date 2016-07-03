package com.mbpolan.ws.services

import com.mbpolan.ws.services.Direction.{Down, Left, Right, Up}

/** Model for a creature on the map.
  *
  * @param ref The internal ID assigned to this creature.
  * @param id The ID of the sprite for the creature.
  * @param name The visible name for the creature.
  * @param pos The current position of the creature on the map.
  * @param dir The direction the creature is facing.
  * @param speed The speed at which the creature moves.
  * @author Mike Polan
  */
abstract class Creature(
    var ref: Int,
    val id: String,
    val name: String,
    var pos: Rect,
    var dir: Direction,
    val speed: Int)
  extends Entity with Animate {

  var lastMove: Long = 0L
  var isMoving = false
  var moveDir = dir
  var nextMove: Option[Task] = None

  /** Determines if the creature can moved again.
    *
    * @param dir The direction the creature intends to move.
    * @return true if the creature can move, false if not.
    */
  def canMove(dir: Direction): Boolean = !isMoving || dir != this.dir

  /** Stops the creature from moving and cancels all planned movement. */
  def stopMoving(): Unit = {
    nextMove = nextMove.flatMap(t => {
      t.cancel()
      None
    })

    isMoving = false
  }

  /** Inverts the direction the creature is currently facing. */
  def invertDirection(): Unit = {
    dir = dir match {
      case Up => Down
      case Down => Up
      case Left => Right
      case Right => Left
    }
  }
}