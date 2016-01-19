package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
case class PlayerMove(
    @BeanProperty valid: Boolean,
    @BeanProperty x: Int,
    @BeanProperty y: Int)