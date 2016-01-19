package com.mbpolan.ws.services

import org.springframework.stereotype.Component

/**
  * @author Mike Polan
  */
@Component
class UserService {

  private var users = Map[String, User]()

  def add(id: String, user: User): Unit = users += (id -> user)

  def remove(id: String): Unit = users -= id

  def allSessions: List[String] = users.keys.toList

  def byId(id: String): Option[User] = users.get(id)
}
