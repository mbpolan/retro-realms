package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans._
import com.mbpolan.ws.services._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

/** Controller that handles incoming requests from users in the game.
  *
  * @author Mike Polan
  */
@Controller
class UserController {

  @Autowired
  private var gameService: GameService = _

  /** Endpoint for registering new player sessions.
    *
    * @param message The handshake message identifying a new user.
    * @param header Additional headers sent with the message.
    */
  @MessageMapping(Array("/user/register"))
  def register(message: NewUser, header: SimpMessageHeaderAccessor): Unit = {
    gameService.addPlayer(header.getSessionId, message.name, message.token)
  }

  /** Endpoint for users sending requests to move on the map.
    *
    * @param message The message containing a move request.
    * @param header Additional headers sent with the message.
    */
  @MessageMapping(Array("/user/player/move"))
  def move(message: PlayerMoveRequest, header: SimpMessageHeaderAccessor): Unit = {
    Direction.fromValue(message.dir) match {
      case Some(dir) => gameService.movePlayer(header.getSessionId, dir)
      case None =>
    }
  }

  /** Endpoint for users sending requests to start or stop moving their players.
    *
    * @param message THe message containing a motion request.
    * @param header Additional headers sent with the message.
    */
  @MessageMapping(Array("/user/player/motion"))
  def motion(message: PlayerMotionRequest, header: SimpMessageHeaderAccessor): Unit = {
    gameService.playerMotion(header.getSessionId, message.moving)
  }
}
