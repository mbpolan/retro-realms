package com.mbpolan.ws.beans

import scala.beans.BeanProperty

/** Message used to reply to a new user connection request.
  *
  * @param result The result of the connection request (see [[ConnectResult]].
  * @param session Session information, if the connection was successful.
  *
  * @author Mike Polan
  */
case class ConnectResponse(
    @BeanProperty result: String,
    @BeanProperty session: SessionDetails)