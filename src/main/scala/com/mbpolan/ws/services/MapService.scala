package com.mbpolan.ws.services

import javax.annotation.PostConstruct

import org.springframework.stereotype.Service

import scala.io.Source

/**
  * @author Mike Polan
  */
@Service
class MapService {

  private val TilesHigh = 20
  private val TilesWide = 25
  private val TileSpace = 4
  private val CoordsWide = TilesWide * TileSpace
  private val CoordsHigh = TilesHigh * TileSpace
  private val TileCoords = CoordsWide * CoordsHigh

  private var block: Vector[Tile] = Vector()
  private var bitmap: Vector[Boolean] = Vector()
  private var entities: Vector[Entity] = Vector()

  @PostConstruct
  def init(): Unit = {
    for (line <- Source.fromInputStream(getClass.getResourceAsStream("/map.csv")).getLines()) {
      block = block ++ line.split(",")
        .map(id => new Tile(id.trim.toShort))
        .toVector
    }

    bitmap = Vector.fill(TileCoords)(true)
    entities = entities :+ new Entity(eType = EntityType.Static, name = None, id = 2, x = 16, y = 0, w = 8, h = 6)
    entities = entities :+ new Entity(eType = EntityType.Creature, name = Some("Link"), id = 0, x = 5, y = 5, w = 4, h = 4)

    entities.foreach(e => {
      for (x <- e.x to e.x + e.w - 1;
           y <- e.y to e.y + e.h - 1) {

        val idx = y * CoordsWide + x
        bitmap = bitmap.updated(idx, false)
      }
    })


    println(s"Loaded map of ${block.length} tiles (${bitmap.size} coords) and ${entities.size} entities")
  }

  def canMoveTo(x: Int, y: Int, w: Int, h: Int): Boolean = {
    if (x < 0 || y < 0 || x + w >= CoordsWide || y + h >= CoordsHigh)
      false

    else {
      val right = x + w
      val bottom = y + h
      var blocked = 0

      for (dx <- x to right;
           dy <- y to bottom) {

        if (!bitmap(dy * CoordsWide + dx))
          blocked += 1
      }

      blocked == 0
    }
  }

  def areaOf: Array[Short] = block.map(_.id).toArray

  def entitiesOf: Array[Entity] = entities.toArray

  private def tileAt(x: Int, y: Int): Option[Tile] = {
    val idx = y * TilesWide + x
    if (idx < 0 || idx >= block.size) None else Some(block(idx))
  }
}
