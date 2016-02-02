package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/** Bean that represents a direction change by a creature.
  *
  * @author Mike Polan
  */
case class DirChange(
  @BeanProperty ref: Int,
  @BeanProperty dir: String
)
