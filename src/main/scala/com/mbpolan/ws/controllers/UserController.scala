package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans.{PlayerMove, PlayerMoveRequest, ConnectResult, NewUser}
import com.mbpolan.ws.services.{User, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.{MessageMapping, SendTo}
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

/**
  * @author Mike Polan
  */
@Controller
class UserController {

  private val TilesHigh = 20
  private val TilesWide = 25

  @Autowired
  private var userService: UserService = null

  @MessageMapping(Array("/user/register"))
  @SendTo(Array("/topic/user/register"))
  def register(message: NewUser, header: SimpMessageHeaderAccessor): ConnectResult = {
    userService.add(header.getSessionId, new User(message.name, 0, 0, 4))
    ConnectResult(header.getSessionId, Array.fill[Short](TilesWide * TilesHigh)(0))
  }

  @MessageMapping(Array("/user/player/move"))
  @SendTo(Array("/topic/user/player"))
  def move(message: PlayerMoveRequest, header: SimpMessageHeaderAccessor): PlayerMove = {
    userService.byId(header.getSessionId).flatMap(user => {
      message.dir match {
        case "up" =>
          user.y -= user.speed
          Some(PlayerMove(valid = true, x = user.x, y = user.y))
        case "down" =>
          user.y += user.speed
          Some(PlayerMove(valid = true, x = user.x, y = user.y))
        case "left" =>
          user.x -= user.speed
          Some(PlayerMove(valid = true, x = user.x, y = user.y))
        case "right" =>
          user.x += user.speed
          Some(PlayerMove(valid = true, x = user.x, y = user.y))
        case _ =>
          None
      }
    }).getOrElse(PlayerMove(valid = false, x = 0, y = 0))
  }
}
