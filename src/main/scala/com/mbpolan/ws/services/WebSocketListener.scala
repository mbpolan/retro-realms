package com.mbpolan.ws.services

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.{AbstractSubProtocolEvent, SessionDisconnectEvent}

/**
  * @author Mike Polan
  */
@Component
class WebSocketListener extends ApplicationListener[AbstractSubProtocolEvent] {

  private val Log = LogManager.getLogger(classOf[WebSocketListener])

  @Autowired
  private var userService: UserService = _

  @Autowired
  private var mapService: MapService = _

  override def onApplicationEvent(event: AbstractSubProtocolEvent): Unit = {
    event match {
      case e: SessionDisconnectEvent =>
        userService.remove(e.getSessionId).map(mapService.removeCreature)
        Log.debug(s"Disconnected: ${e.getSessionId}")
      case _ =>
    }
  }
}