package com.mbpolan.ws.services

import org.springframework.stereotype.Component

/** Service that contains mappings between user session IDs and their player IDs.
  *
  * @author Mike Polan
  */
@Component
class UserService {

  private var users = Map[String, Int]()
  private var refs = Map[Int, String]()

  /** Registers a new player session.
    *
    * @param id The ID of the session connection.
    * @param ref The internal ID assigned to their player.
    */
  def add(id: String, ref: Int): Unit = synchronized {
    users += (id -> ref)
    refs += (ref -> id)
  }

  /** Removes a registered user.
    *
    * @param id The session ID of the player to remove.
    * @return The internal ID that used to be assigned to the player.
    */
  def remove(id: String): Option[Int] = synchronized {
    users.get(id).map(ref => {
      users -= id
      refs -= ref
      ref
    })
  }

  /** Returns a list of all user sessions currently registered.
    *
    * @return A list of session IDs.
    */
  def allSessions: List[String] = synchronized {
    users.keys.toList
  }

  /** Returns the internal ID of a player based on their session ID.
    *
    * @param id The session ID to look up.
    * @return The corresponding internal ID.
    */
  def byId(id: String): Option[Int] = synchronized {
    users.get(id)
  }

  /** Returns the session ID of a player based on their internal ID.
    *
    * @param ref The internal ID of the player to look up.
    * @return The corresponding session ID.
    */
  def byRef(ref: Int): Option[String] = synchronized {
    refs.get(ref)
  }
}
