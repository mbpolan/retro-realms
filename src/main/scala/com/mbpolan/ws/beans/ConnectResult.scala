package com.mbpolan.ws.beans

import com.mbpolan.ws.beans.messages.AddEntityMessage

import scala.beans.BeanProperty

/**
  * @author Mike Polan
  */
case class ConnectResult(
    @BeanProperty sessionId: String,
    @BeanProperty ref: Int,
    @BeanProperty area: Array[Short],
    @BeanProperty entities: Array[AddEntityMessage])