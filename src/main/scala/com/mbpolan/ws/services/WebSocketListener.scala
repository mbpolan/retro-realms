package com.mbpolan.ws.services

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.{AbstractSubProtocolEvent, SessionDisconnectEvent}

/** Listener that processes event notifications from the application.
  *
  * @author Mike Polan
  */
@Component
class WebSocketListener extends ApplicationListener[AbstractSubProtocolEvent] {

  private val Log = LogManager.getLogger(classOf[WebSocketListener])

  @Autowired
  private var gameService: GameService = _

  override def onApplicationEvent(event: AbstractSubProtocolEvent): Unit = {
    event match {
      // a websocket session has either disconnected or expired
      case e: SessionDisconnectEvent =>
        Log.debug(s"Session expired: ${e.getSessionId}")
        gameService.removePlayer(e.getSessionId)

      case _ =>
    }
  }
}