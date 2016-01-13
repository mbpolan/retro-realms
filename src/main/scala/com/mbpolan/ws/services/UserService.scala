package com.mbpolan.ws.services

import org.springframework.stereotype.Component

/**
  * @author Mike Polan
  */
@Component
class UserService {

  var users = Map[String, User]()

  def add(user: User): Unit = users += (user.name -> user)

  def remove(name: String): Unit = users - name
}
