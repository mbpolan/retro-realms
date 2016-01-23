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
  private var entities: Vector[Entity] = Vector()

  private var lastRef: Int = 0

  @PostConstruct
  def init(): Unit = {
    for (line <- Source.fromInputStream(getClass.getResourceAsStream("/map.csv")).getLines()) {
      block = block ++ line.split(",")
        .map(id => new Tile(id.trim.toShort))
        .toVector
    }

    entities = entities :+ new StaticObject(id = "2", pos = Rect(16, 0, 8, 6))

    println(s"Loaded map of ${block.length} tiles and ${entities.size} entities")
  }

  def addCreature(c: Creature): Int = {
    c.ref = lastRef
    entities = entities :+ c

    lastRef += 1
    c.ref
  }

  def creatureBy(ref: Int): Option[Creature] = {
    entities.find {
      case c: Creature => c.ref == ref
      case _ => false
    }.map(_.asInstanceOf[Creature])
  }

  def canMoveToDelta(c: Creature, dx: Int, dy: Int): Boolean = {
    val toPos = Rect(c.pos.x + dx, c.pos.y + dy, c.pos.w, c.pos.h)

    if (toPos.x < 0 || toPos.y < 0 || toPos.x + c.pos.w >= CoordsWide || toPos.y + c.pos.h >= CoordsHigh)
      false

    else {
      !entities.exists {
        case e if e ne c => toPos.intersects(e.pos)
        case _ => false
      }
    }
  }

  def moveDelta(ref: Int, dx: Int, dy: Int): Boolean = {
    creatureBy(ref)
      .flatMap(e => {
        canMoveToDelta(e, dx, dy) match {
          case true =>
            e.pos = Rect(e.pos.x + dx, e.pos.y + dy, e.pos.w, e.pos.h)
            Some(true)
          case false => None
        }
      }).getOrElse(false)
  }

  def areaOf: Array[Short] = block.map(_.id).toArray

  def entitiesOf: Array[Entity] = entities.toArray
}
