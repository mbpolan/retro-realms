package com.mbpolan.ws.beans

/** Enumeration of possible player character colors.
  *
  * @author Mike Polan
  */
sealed trait PlayerColor { def id: String }

object PlayerColor {
  case object Green extends PlayerColor { val id = "green" }
  case object Blue extends PlayerColor { val id = "blue" }
  case object Red extends PlayerColor { val id = "red" }

  def fromValue(id: String): Option[PlayerColor] = {
    id match {
      case "green" => Some(Green)
      case "blue" => Some(Blue)
      case "red" => Some(Red)
      case _ => None
    }
  }
}
