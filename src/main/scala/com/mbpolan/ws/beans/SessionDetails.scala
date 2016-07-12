package com.mbpolan.ws.beans

import com.mbpolan.ws.beans.messages.AddEntityMessage

import scala.beans.BeanProperty

/** Bean that contains information about a new user session.
  *
  * @param sessionId The ID of the new user session.
  * @param ref The internal ID assigned to the player.
  * @param area IDs of sprites that make up the map area.
  * @param tilesWide The amount of tiles spanning the width map.
  * @param tilesHigh THe among of tiles spanning the height of the map.
  * @param entities List of entities found on the map area.
  *
  * @author Mike Polan
  */
case class SessionDetails(
    @BeanProperty sessionId: String,
    @BeanProperty ref: Int,
    @BeanProperty area: Array[Short],
    @BeanProperty tilesWide: Int,
    @BeanProperty tilesHigh: Int,
    @BeanProperty entities: Array[AddEntityMessage])