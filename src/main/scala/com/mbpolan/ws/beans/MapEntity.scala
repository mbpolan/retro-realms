package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
case class MapEntity(
    @BeanProperty ref: Integer,
    @BeanProperty id: String,
    @BeanProperty name: String,
    @BeanProperty x: Int,
    @BeanProperty y: Int)