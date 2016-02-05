package com.mbpolan.ws.services

import javax.annotation.PostConstruct

import com.mbpolan.ws.beans.{ConnectResponse, ConnectResult, SessionDetails}
import com.mbpolan.ws.beans.messages.{AddEntityMessage, PlayerMoveResult, PlayerMoveResultMessage}
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/** Service that provides high-level game interaction and coordination.
  *
  * @author Mike Polan
  */
@Service
class GameService {

  private val Log = LogManager.getLogger(classOf[GameService])

  @Autowired
  private var mapService: MapService = _

  @Autowired
  private var userService: UserService = _

  @Autowired
  private var websocket: SimpMessagingTemplate = _

  @PostConstruct
  def init(): Unit = {
    Log.info("Initialized game engine")
  }

  /** Adds a player to the game.
    *
    * @param sessionId The session ID of the player to add.
    * @param name The name for the player.
    * @param token The registration token on which to send a response message to.
    */
  def addPlayer(sessionId: String, name: String, token: String): Unit = synchronized {
    websocket.convertAndSend(s"/topic/user/$token/register", userService.exists(name) match {

      case false =>
        val myRef = mapService.addCreature(new Creature(0, "char", name, Rect(0, 0, 4, 4), Direction.Down, 4))
        userService.add(name, sessionId, myRef)

        ConnectResponse(result = ConnectResult.Valid.id,
          session = SessionDetails(sessionId, myRef, mapService.areaOf,
          mapService.entitiesOf.map {
            case e: StaticObject =>
              AddEntityMessage(ref = null, name = null, dir = null, id = e.id, x = e.pos.x, y = e.pos.y)
            case e: Creature =>
              AddEntityMessage(ref = e.ref, name = e.name, dir = e.dir.value, id = e.id, x = e.pos.x, y = e.pos.y)
            case _ => null
          }))

      case true => ConnectResponse(result = ConnectResult.NameInUse.id, session = null)
    })
  }

  /** Removes an existing player from the game.
    *
    * @param sessionId The session ID of the player to remove.
    */
  def removePlayer(sessionId: String): Unit = synchronized {
    userService.remove(sessionId) match {
      case Some(ref) => mapService.removeCreature(ref)
      case None =>
    }
  }

  /** Moves a player on the map in a specific direction.
    *
    * @param sessionId The session ID of the player to move.
    * @param dir The [[Direction]] to move the player.
    */
  def movePlayer(sessionId: String, dir: Direction): Unit = synchronized {
    val result = userService.byId(sessionId)
      .flatMap(mapService.creatureBy)
      .filter(_.canMove)
      .flatMap(user => {
        mapService.moveDelta(user.ref, dir) match {

          case true =>
            user.lastMove = System.currentTimeMillis()
            Some(PlayerMoveResultMessage(result = PlayerMoveResult.Valid.id))

          case false => Some(PlayerMoveResultMessage(result = PlayerMoveResult.Blocked.id))
        }

      }).getOrElse(PlayerMoveResultMessage(result = PlayerMoveResult.TooSoon.id))

    websocket.convertAndSend(s"/topic/user/$sessionId/message", result)
  }
}
