package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
case class ConnectResult(
    @BeanProperty sessionId: String,
    @BeanProperty area: Array[Short],
    @BeanProperty entities: Array[MapEntity])