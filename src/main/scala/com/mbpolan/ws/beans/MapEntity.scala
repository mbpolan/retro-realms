package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
case class MapEntity(
    @BeanProperty id: Short,
    @BeanProperty x: Int,
    @BeanProperty y: Int)