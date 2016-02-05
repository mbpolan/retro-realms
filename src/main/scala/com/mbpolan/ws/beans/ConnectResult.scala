package com.mbpolan.ws.beans

/** Enumeration of possible results for registering new users.
  *
  * @author Mike Polan
  */
sealed trait ConnectResult { def id: String }

object ConnectResult {
  case object Valid extends ConnectResult { val id = "Valid" }
  case object NameInUse extends ConnectResult { val id = "NameInUse" }
}
