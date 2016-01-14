package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans.{ConnectResult, NewUser}
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

  @Autowired
  private var userService: UserService = null

  @MessageMapping(Array("/user/register"))
  @SendTo(Array("/topic/user/register"))
  def register(message: NewUser, header: SimpMessageHeaderAccessor): ConnectResult = {
    userService.add(header.getSessionId, User(message.name))
    ConnectResult(header.getSessionId)
  }
}
