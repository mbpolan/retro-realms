package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/** Bean that stores information about a new player joining the game.
  *
  * @author Mike Polan
  */
class NewUser {

  @BeanProperty var name: String = null
  @BeanProperty var color: String = null
  @BeanProperty var token: String = null
}