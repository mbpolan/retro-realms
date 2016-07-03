package com.mbpolan.ws.services

/** Trait that represents any non-static entity on the world.
  *
  * @author Mike Polan
  */
trait Animate {

  /** Causes the entity to reevaluate its current state and take actions.
    *
    * @param gameService The instance of the game engine to work against.
    */
  def tick(gameService: GameService): Unit
}
