package com.mbpolan.ws.services

/**
  * @author Mike Polan
  */
class User(
    val name: String,
    var x: Int,
    var y: Int,
    val speed: Int) {

  var lastMove: Long = 0L

  def canMove: Boolean = System.currentTimeMillis() - lastMove > 25
}