package com.mbpolan.ws.services

/**
  * @author Mike Polan
  */
class Entity(
    val eType: EntityType,
    val name: Option[String],
    val id: Short,
    var x: Int,
    var y: Int,
    val w: Int,
    val h: Int)