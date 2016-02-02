package com.mbpolan.ws.services

import javax.annotation.PostConstruct

import com.mbpolan.ws.beans.ConnectResult
import com.mbpolan.ws.beans.messages.{AddEntityMessage, PlayerMoveMessage}
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
    * @param token The registration token on which to send a response message to.
    */
  def addPlayer(sessionId: String, token: String): Unit = {
    val myRef = mapService.addCreature(new Creature(0, "char", "Mike", Rect(0, 0, 4, 4), Direction.Down, 4))
    userService.add(sessionId, myRef)

    val result = ConnectResult(sessionId, myRef, mapService.areaOf,
      mapService.entitiesOf.map {
        case e: StaticObject =>
          AddEntityMessage(ref = null, name = null, dir = null, id = e.id, x = e.pos.x, y = e.pos.y)
        case e: Creature =>
          AddEntityMessage(ref = e.ref, name = e.name, dir = e.dir.value, id = e.id, x = e.pos.x, y = e.pos.y)
        case _ => null
      })

    websocket.convertAndSend(s"/topic/user/$token/register", result)
  }

  /** Moves a player on the map in a specific direction.
    *
    * @param sessionId The session ID of the player to move.
    * @param dir The [[Direction]] to move the player.
    */
  def movePlayer(sessionId: String, dir: Direction): Unit = {
    val result = userService.byId(sessionId)
      .flatMap(mapService.creatureBy)
      .filter(_.canMove)
      .flatMap(user => {
        mapService.moveDelta(user.ref, dir) match {

          case true =>
            user.lastMove = System.currentTimeMillis()
            Some(PlayerMoveMessage(valid = true, ref = user.ref, user.pos.x, user.pos.y))

          case false => None
        }

      }).getOrElse(PlayerMoveMessage(valid = false, ref = null, x = 0, y = 0))

    websocket.convertAndSend(s"/topic/user/$sessionId/message", result)
  }
}
