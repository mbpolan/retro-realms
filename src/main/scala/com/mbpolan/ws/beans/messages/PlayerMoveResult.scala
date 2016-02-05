package com.mbpolan.ws.beans.messages

/** Enumeration of possible results for attempting to move a player.
  *
  * @author Mike Polan
  */
sealed trait PlayerMoveResult { def id: String }

object PlayerMoveResult {
  case object Valid extends PlayerMoveResult { val id = "Valid" }
  case object TooSoon extends PlayerMoveResult { val id = "TooSoon" }
  case object Blocked extends PlayerMoveResult { val id = "Blocked" }
}