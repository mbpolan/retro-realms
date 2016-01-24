package com.mbpolan.ws.services

/**
  * @author Mike Polan
  */
class Creature(
    var ref: Int,
    val id: String,
    val name: String,
    var pos: Rect,
    var dir: Direction,
    val speed: Int)
  extends Entity {

  var lastMove: Long = 0L

  def canMove: Boolean = System.currentTimeMillis() - lastMove > 25
}