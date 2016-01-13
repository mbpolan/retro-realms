package com.mbpolan.ws.services

import java.util.UUID

import org.springframework.stereotype.Component

/**
  * @author Mike Polan
  */
@Component
class UserService {

  var users = Map[String, User]()

  def add(user: User): Unit = {
    val id = UUID.randomUUID().toString
    users += (id -> user)

    id
  }

  def remove(id: String): Unit = users - id
}
