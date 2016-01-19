package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
class PlayerMoveRequest {

  @BeanProperty var dir: String = _
}