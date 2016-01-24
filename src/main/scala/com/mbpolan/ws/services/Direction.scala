package com.mbpolan.ws.services

/**
  * Created by Mike on 2016-01-24.
  */
sealed trait Direction { def value: String; def dx: Int; def dy: Int }
object Direction {
  case object Up extends Direction { val value = "up"; val dx = 0; val dy = -1 }
  case object Down extends Direction { val value = "down"; val dx = 0; val dy = 1 }
  case object Left extends Direction { val value = "left"; val dx = -1; val dy = 0 }
  case object Right extends Direction { val value = "right"; val dx = 1; val dy = 0 }

  def fromValue(value: String): Option[Direction] = {
    value match {
      case "up" => Some(Direction.Up)
      case "down" => Some(Direction.Down)
      case "left" => Some(Direction.Left)
      case "right" => Some(Direction.Right)
      case _ => None
    }
  }
}
