package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans._
import com.mbpolan.ws.services.{MapService, User, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.{MessageMapping, SendTo}
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

/**
  * @author Mike Polan
  */
@Controller
class UserController {

  @Autowired
  private var mapService: MapService = _

  @Autowired
  private var userService: UserService = _

  @MessageMapping(Array("/user/register"))
  @SendTo(Array("/topic/user/register"))
  def register(message: NewUser, header: SimpMessageHeaderAccessor): ConnectResult = {
    userService.add(header.getSessionId, new User(message.name, 0, 0, 4))

    ConnectResult(header.getSessionId, mapService.areaOf,
      mapService.entitiesOf.map(e =>
        MapEntity(e.id, e.x, e.y)))
  }

  @MessageMapping(Array("/user/player/move"))
  @SendTo(Array("/topic/user/player"))
  def move(message: PlayerMoveRequest, header: SimpMessageHeaderAccessor): PlayerMove = {
    userService.byId(header.getSessionId)
      .filter(_.canMove)
      .flatMap(user => {
        message.dir match {
          case "up" =>
            Some((user, user.x, user.y - 1))
          case "down" =>
            Some((user, user.x, user.y + 1))
          case "left" =>
            Some((user, user.x - 1, user.y))
          case "right" =>
            Some((user, user.x + 1, user.y))
          case _ =>
            None
        }
      }).flatMap(move => {
      mapService.canMoveTo(move._2, move._3, 4, 4) match {
        case true =>
          move._1.lastMove = System.currentTimeMillis()

          move._1.x = move._2
          move._1.y = move._3
          Some(PlayerMove(valid = true, move._2, move._3))
        case false => None
      }
    }).getOrElse(PlayerMove(valid = false, x = 0, y = 0))
  }
}
