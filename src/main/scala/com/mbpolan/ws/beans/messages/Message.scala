package com.mbpolan.ws.beans.messages

import scala.beans.BeanProperty

/** Top-level class for any in-game event message sent to the client.
  *
  * @author Mike Polan
  */
abstract class Message(@BeanProperty val event: String)