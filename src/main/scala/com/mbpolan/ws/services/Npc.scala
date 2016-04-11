package com.mbpolan.ws.services

/** Model for a non-player controlled character in the game world.
  *
  * @param ref The internal ID assigned to this creature.
  * @param id The ID of the sprite for the creature.
  * @param name The visible name for the creature.
  * @param pos The current position of the creature on the map.
  * @param dir The direction the creature is facing.
  * @param speed The speed at which the creature moves.
  *
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

}
