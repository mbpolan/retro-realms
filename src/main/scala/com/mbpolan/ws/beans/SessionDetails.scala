package com.mbpolan.ws.beans

import com.mbpolan.ws.beans.messages.AddEntityMessage

import scala.beans.BeanProperty

/** Bean that contains information about a new user session.
  *
  * @param sessionId The ID of the new user session.
  * @param ref The internal ID assigned to the player.
  * @param area IDs of sprites that make up the map area.
  * @param entities List of entities found on the map area.
  *
  * @author Mike Polan
  */
case class SessionDetails(
    @BeanProperty sessionId: String,
    @BeanProperty ref: Int,
    @BeanProperty area: Array[Short],
    @BeanProperty entities: Array[AddEntityMessage])