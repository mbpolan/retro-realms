package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/** Message sent by client when the player wants to start or stop moving.
  *
  * @author Mike Polan
  */
class PlayerMotionRequest {

  @BeanProperty var moving: Boolean = _
}