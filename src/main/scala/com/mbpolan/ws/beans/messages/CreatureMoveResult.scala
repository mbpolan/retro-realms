package com.mbpolan.ws.beans.messages

/** Enumeration of possible results for attempting to move a creature.
  *
  * @author Mike Polan
  */
sealed trait CreatureMoveResult { def id: String }

object CreatureMoveResult {
  case object Valid extends CreatureMoveResult { val id = "Valid" }
  case object TooSoon extends CreatureMoveResult { val id = "TooSoon" }
  case object Blocked extends CreatureMoveResult { val id = "Blocked" }
}