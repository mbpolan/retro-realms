package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans.{ConnectResult, NewUser}
import org.springframework.messaging.handler.annotation.{MessageMapping, SendTo}
import org.springframework.stereotype.Controller

/**
  * @author Mike Polan
  */
@Controller
class UserController {

  @MessageMapping(Array("/user"))
  @SendTo(Array("/topic/user"))
  def greeting(message: NewUser): ConnectResult = ConnectResult(s"Hello, ${message.name}!")
}
