package com.mbpolan.ws.services

/** Trait that represents any logical entity in the game world.
  *
  * @author Mike Polan
  */
trait Entity {

  def id: String
  def pos: Rect
}
