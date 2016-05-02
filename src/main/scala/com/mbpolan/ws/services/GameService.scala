package com.mbpolan.ws.services

import javax.annotation.PostConstruct

import com.mbpolan.ws.beans.messages._
import com.mbpolan.ws.beans.{ConnectResponse, ConnectResult, PlayerColor, SessionDetails}
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

  @Autowired
  private var scheduler: Scheduler = _

  @PostConstruct
  def init(): Unit = {
    Log.info("Initialized game engine")

    // add an npc to the map
    mapService.addNpc("villager-purple", "Villager", Rect(20, 20, 4, 4), Direction.Down, 2)

    // schedule a task to review each creature's state
    scheduler.scheduleFixedRate(() => {
      mapService.nonPlayers.foreach(c => c.tick())
    }, 1000L)
  }

  /** Adds a player to the game.
    *
    * @param sessionId The session ID of the player to add.
    * @param name The name for the player.
    * @param color The color of the player's character.
    * @param token The registration token on which to send a response message to.
    */
  def addPlayer(sessionId: String, name: String, color: PlayerColor, token: String): Unit = synchronized {
    websocket.convertAndSend(s"/topic/user/$token/register", userService.exists(name) match {

      case false =>
        // add the player to the map
        val myRef = mapService.addPlayer(sessionId, s"char-${color.id}", name, Rect(0, 0, 4, 4))
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
    userService.byId(sessionId)
      .flatMap(mapService.creatureBy)
      .filter(_.canMove)
      .flatMap(user => {

        lazy val f: () => Unit = {
          () => {
            // attempt to move the player in their current direction
            mapService.moveDelta(user.ref, dir) match {

              case true =>
                // if the player wasn't previously moving, update their status
                if (!user.isMoving) {
                  mapService.creatureMotionChange(user.ref, moving = true)
                  user.isMoving = true
                }

                user.lastMove = System.currentTimeMillis()

                // schedule the next movement but only if the player is still moving at that time
                scheduler.schedule(() => {
                  if (user.isMoving) f()
                }, 50)

              case false =>
                mapService.creatureMotionChange(user.ref, moving = false)
            }
          }
        }

        // schedule the initial movement right away
        scheduler.schedule(f)
        websocket.convertAndSend(s"/topic/user/$sessionId/message", PlayerMoveResultMessage(result = PlayerMoveResult.Valid.id))
        None
      })
  }

  /** Stops a currently moving player.
    *
    * @param sessionId The session ID of the player to halt.
    */
  def stopPlayer(sessionId: String): Unit = synchronized {
    userService.byId(sessionId)
      .flatMap(mapService.creatureBy)
      .foreach(c => {
        c.isMoving = false
        mapService.creatureMotionChange(c.ref, moving = false)
      })
  }

  /** Sends a chat message to nearby players.
    *
    * @param sessionId The session ID of the player in question.
    * @param message The text content of the message to send.
    */
  def playerChatMessage(sessionId: String, message: String): Unit = synchronized {
    userService.byId(sessionId) match {
      case Some(ref) =>
        mapService.creatureBy(ref) match {

          case Some(sender) =>
            mapService.nearByPlayers(ref)
              .foreach(_.send(websocket, PlayerChatMessage(ref = ref, name = sender.name, text = message)))

          case None =>
        }

      case None =>
    }
  }
}
