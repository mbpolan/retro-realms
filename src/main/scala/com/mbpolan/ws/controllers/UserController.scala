package com.mbpolan.ws.controllers

import com.mbpolan.ws.beans.NewUser
import net.kanadkid.ws.beans.Greeting
import org.springframework.messaging.handler.annotation.{MessageMapping, SendTo}
import org.springframework.stereotype.Controller

/**
  * @author Mike Polan
  */
@Controller
class UserController {

  @MessageMapping(Array("/user"))
  @SendTo(Array("/topic/user"))
  def greeting(message: NewUser): Greeting = Greeting(s"Hello, ${message.name}!")
}
