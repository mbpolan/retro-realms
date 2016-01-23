package com.mbpolan.ws.services

/**
  * @author Mike Polan
  */
sealed trait EntityType { def _type: String }
object EntityType {
  case object Creature extends EntityType { val _type = "Creature" }
  case object Static extends EntityType { val _type = "Static" }
}