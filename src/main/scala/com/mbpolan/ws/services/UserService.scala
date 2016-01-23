package com.mbpolan.ws.services

import org.springframework.stereotype.Component

/**
  * @author Mike Polan
  */
@Component
class UserService {

  private var users = Map[String, Int]()

  def add(id: String, ref: Int): Unit = users += (id -> ref)

  def remove(id: String): Unit = users -= id

  def allSessions: List[String] = users.keys.toList

  def byId(id: String): Option[Int] = users.get(id)
}
