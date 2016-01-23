package com.mbpolan.ws.services

/**
  * @author Mike Polan
  */
case class Rect(x: Int = 0, y: Int = 0, w: Int = 0, h: Int = 0) {

  def intersects(r: Rect): Boolean = {
    !((r.x > x + w) || (r.x + r.w < x) || (r.y > y + h) || (r.y + r.h < y))
  }
}
