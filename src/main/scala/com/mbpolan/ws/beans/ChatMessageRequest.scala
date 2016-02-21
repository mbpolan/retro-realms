package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/** Bean that represents a player's chat message that was inputted.
  *
  * @author Mike Polan
  */
class ChatMessageRequest {

  @BeanProperty var message: String = _
}