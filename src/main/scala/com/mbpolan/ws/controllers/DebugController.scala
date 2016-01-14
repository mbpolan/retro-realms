package com.mbpolan.ws.controllers

import com.mbpolan.ws.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RestController}

/**
  * @author Mike Polan
  */
@RestController
@RequestMapping(Array("/api/debug"))
class DebugController {

  @Autowired
  private var userService: UserService = null

  @RequestMapping(Array("/sessions"))
  def sessions: List[String] = userService.allSessions
}
