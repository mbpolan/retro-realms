package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans._
import com.mbpolan.ws.services._
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

    val myRef = mapService.addCreature(new Creature(0, "char", "Mike", Rect(0, 0, 4, 4), 4))
    userService.add(header.getSessionId, myRef)

    ConnectResult(header.getSessionId, myRef, mapService.areaOf,
      mapService.entitiesOf.map {
        case e: StaticObject =>
          MapEntity(ref = null, name = null, id = e.id, x = e.pos.x, y = e.pos.y)
        case e: Creature =>
          MapEntity(ref = e.ref, name = e.name, id = e.id, x = e.pos.x, y = e.pos.y)
        case _ => null
      })
  }

  @MessageMapping(Array("/user/player/move"))
  @SendTo(Array("/topic/user/player"))
  def move(message: PlayerMoveRequest, header: SimpMessageHeaderAccessor): PlayerMove = {
    userService.byId(header.getSessionId)
      .flatMap(mapService.creatureBy)
      .filter(_.canMove)
      .flatMap(user => {
        message.dir match {
          case "up" =>
            Some((user, 0, -1))
          case "down" =>
            Some((user, 0, 1))
          case "left" =>
            Some((user, - 1, 0))
          case "right" =>
            Some((user, 1, 0))
          case _ =>
            None
        }
      }).flatMap(move => {
      mapService.moveDelta(move._1.ref, move._2, move._3) match {
        case true =>
          move._1.lastMove = System.currentTimeMillis()
          Some(PlayerMove(valid = true, move._1.pos.x, move._1.pos.y))
        case false => None
      }
    }).getOrElse(PlayerMove(valid = false, x = 0, y = 0))
  }
}
