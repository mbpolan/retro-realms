package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
case class MapEntity(
    @BeanProperty eType: String,
    @BeanProperty name: String,
    @BeanProperty id: Short,
    @BeanProperty x: Int,
    @BeanProperty y: Int)