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
  extends Entity {

  var lastMove: Long = 0L

  /** Determines if the creature can moved again.
    *
    * @return true if the creature can move, false if not.
    */
  def canMove: Boolean = System.currentTimeMillis() - lastMove > 25

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