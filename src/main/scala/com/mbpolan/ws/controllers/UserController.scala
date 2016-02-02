package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans._
import com.mbpolan.ws.beans.messages.{AddEntityMessage, PlayerMoveMessage}
import com.mbpolan.ws.services._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.{SimpMessageHeaderAccessor, SimpMessagingTemplate}
import org.springframework.stereotype.Controller

/** Controller that handles incoming requests from users in the game.
  *
  * @author Mike Polan
  */
@Controller
class UserController {

  @Autowired
  private var mapService: MapService = _

  @Autowired
  private var userService: UserService = _

  @Autowired
  private var websocket: SimpMessagingTemplate = _

  /** Endpoint for registering new player sessions.
    *
    * @param message The handshake message identifying a new user.
    * @param header Additional headers sent with the message.
    */
  @MessageMapping(Array("/user/register"))
  def register(message: NewUser, header: SimpMessageHeaderAccessor): Unit = {
    val myRef = mapService.addCreature(new Creature(0, "char", "Mike", Rect(0, 0, 4, 4), Direction.Down, 4))
    userService.add(header.getSessionId, myRef)

    val result = ConnectResult(header.getSessionId, myRef, mapService.areaOf,
      mapService.entitiesOf.map {
        case e: StaticObject =>
          AddEntityMessage(ref = null, name = null, dir = null, id = e.id, x = e.pos.x, y = e.pos.y)
        case e: Creature =>
          AddEntityMessage(ref = e.ref, name = e.name, dir = e.dir.value, id = e.id, x = e.pos.x, y = e.pos.y)
        case _ => null
      })

    websocket.convertAndSend(s"/topic/user/${message.token}/register", result)
  }

  /** Endpoint for users sending requests to move on the map.
    *
    * @param message The message containing a move request.
    * @param header Additional headers sent with the message.
    */
  @MessageMapping(Array("/user/player/move"))
  def move(message: PlayerMoveRequest, header: SimpMessageHeaderAccessor): Unit = {
    val result = userService.byId(header.getSessionId)
      .flatMap(mapService.creatureBy)
      .filter(_.canMove)
      .flatMap(user => {
        Direction.fromValue(message.dir).map(d => (user, d))
      }).flatMap(req => {
      mapService.moveDelta(req._1.ref, req._2) match {

        case true =>
          req._1.lastMove = System.currentTimeMillis()
          Some(PlayerMoveMessage(valid = true, ref = req._1.ref, req._1.pos.x, req._1.pos.y))

        case false => None
      }

    }).getOrElse(PlayerMoveMessage(valid = false, ref = null, x = 0, y = 0))

    websocket.convertAndSend(s"/topic/user/${header.getSessionId}/message", result)
  }
}
