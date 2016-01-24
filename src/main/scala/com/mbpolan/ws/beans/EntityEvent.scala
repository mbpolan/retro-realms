package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
case class EntityEvent(
    @BeanProperty added: Boolean,
    @BeanProperty entity: MapEntity)