package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Bean that represents a direction change by a creature.
  *
  * @author Mike Polan
  */
case class DirChangeMessage(
    @BeanProperty ref: Int,
    @BeanProperty dir: String)
  extends Message(MessageType.DirectionChange.id)
