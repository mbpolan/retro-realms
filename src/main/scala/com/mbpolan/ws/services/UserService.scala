package com.mbpolan.ws.services

import org.springframework.stereotype.Component

/** Service that contains mappings between user session IDs and their player IDs.
  *
  * @author Mike Polan
  */
@Component
class UserService {

  case class User(name: String, sessionId: String, ref: Int)

  private var users = Map[String, User]()
  private var refs = Map[Int, User]()

  /** Registers a new player session.
    *
    * @param id The ID of the session connection.
    * @param ref The internal ID assigned to their player.
    */
  def add(name: String, id: String, ref: Int): Unit = synchronized {
    val user = User(name, id, ref)
    users += (id -> user)
    refs += (ref -> user)
  }

  /** Removes a registered user.
    *
    * @param id The session ID of the player to remove.
    * @return The internal ID that used to be assigned to the player.
    */
  def remove(id: String): Option[Int] = synchronized {
    users.get(id).map(user => {
      users -= id
      refs -= user.ref
      user.ref
    })
  }

  /** Returns a list of all user sessions currently registered.
    *
    * @return A list of session IDs.
    */
  def allSessions: List[String] = synchronized {
    users.keys.toList
  }

  /** Determines whether a user with a given name already exists.
    *
    * @param name The name to test.
    * @return true if a user with that name already is registered, false otherwise.
    */
  def exists(name: String): Boolean = synchronized {
    users.exists(_._2.name == name)
  }

  /** Returns the internal ID of a player based on their session ID.
    *
    * @param id The session ID to look up.
    * @return The corresponding internal ID.
    */
  def byId(id: String): Option[Int] = synchronized {
    users.get(id).map(_.ref)
  }

  /** Returns the session ID of a player based on their internal ID.
    *
    * @param ref The internal ID of the player to look up.
    * @return The corresponding session ID.
    */
  def byRef(ref: Int): Option[String] = synchronized {
    refs.get(ref).map(_.sessionId)
  }
}
