package com.mbpolan.ws.services

import javax.annotation.PostConstruct

import com.mbpolan.ws.beans.messages._
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

import scala.io.Source

/** Service that manages the world map and the entities on it.
  *
  * @author Mike Polan
  */
@Service
class MapService {

  private val Log = LogManager.getLogger(classOf[MapService])

  @Autowired
  private var userService: UserService = _

  @Autowired
  private var websocket: SimpMessagingTemplate = _

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

  /** Adds a new creature to the map.
    *
    * @param c The [[Creature]] to add.
    * @return The internal ID assigned to the creature.
    */
  def addCreature(c: Creature): Int = synchronized {
    c.ref = lastRef
    entities = entities :+ c
    lastRef += 1

    notifyAll(AddEntityMessage(ref = c.ref, id = "char", name = c.name, dir = Direction.Down.value, x = c.pos.x, y = c.pos.y))

    c.ref
  }

  /** Removes a creature from the map/
    *
    * @param ref The internal ID of the creature.
    */
  def removeCreature(ref: Int): Unit = synchronized {
    entities = entities.filter {
      case e: Creature if e.ref == ref => false
      case _ => true
    }

    notifyAll(RemoveEntityMessage(ref = ref))
  }

  /** Returns a [[Creature]] on the map whose internal ID matches that of the given one.
    *
    * @param ref The internal ID of the creature.
    * @return The corresponding [[Creature]].
    */
  def creatureBy(ref: Int): Option[Creature] = synchronized {
    entities.find {
      case c: Creature => c.ref == ref
      case _ => false
    }.map(_.asInstanceOf[Creature])
  }

  /** Returns a list of players that are in viewing distance of a given player.
    *
    * @param ref The internal ID of the player.
    * @return A list of [[Creature]]s that are near the player.
    */
  def nearByPlayers(ref: Int): Vector[Creature] = {
    // FIXME: right now we only have one map area, so return all players
    entities.filter(_.isInstanceOf[Creature]).map(_.asInstanceOf[Creature])
  }

  /** Determines if a creature can be moved by some positional delta.
    *
    * @param c The [[Creature]] to move.
    * @param dx The delta x movement.
    * @param dy The delta y movement.
    * @return true if the movement is possible, false if not.
    */
  def canMoveToDelta(c: Creature, dx: Int, dy: Int): Boolean = synchronized {
    val toPos = Rect(c.pos.x + dx, c.pos.y + dy, c.pos.w, c.pos.h)

    // check bounds to see if the creature tried to move outside the map area
    if (toPos.x < 0 || toPos.y < 0 || toPos.x + c.pos.w >= CoordsWide || toPos.y + c.pos.h >= CoordsHigh)
      false

    else {
      !entities.exists {
        case e if e ne c => toPos.intersects(e.pos)
        case _ => false
      }
    }
  }

  /** Moves a creature on the map by some directional delta.
    *
    * @param ref The internal ID of the creature to move.
    * @param dir The direction in which to move the creature.
    * @return true if the movement succeeded, false if not.
    */
  def moveDelta(ref: Int, dir: Direction): Boolean = synchronized {
    creatureBy(ref)
      .flatMap(e => {
        canMoveToDelta(e, dir.dx, dir.dy) match {
          case true =>
            e.pos = Rect(e.pos.x + dir.dx, e.pos.y + dir.dy, e.pos.w, e.pos.h)

            // has the creature changed the direction its facing? if so, notify nearby creatures
            if (dir != e.dir) {
              notifyAll(DirChangeMessage(ref = e.ref, dir = dir.value))
            }

            notifyAll(EntityMoveMessage(ref = ref, x = e.pos.x, y = e.pos.y))
            e.dir = dir

            Some(true)
          case false => None
        }
      }).getOrElse(false)
  }

  /** Notifies all neighboring creatures that another creature has started or stopping moving.
    *
    * @param ref The internal ID of the creature's who motion is changing/
    * @param moving true if the creature started moving, false if they stopped.
    */
  def creatureMotionChange(ref: Int, moving: Boolean): Unit = synchronized {
    notifyAll(EntityMotionMessage(ref = ref, start = moving))
  }

  /** Returns the sprite IDs of the map.
    *
    * @return A description of the map in the form of sprite ID numbers.
    */
  def areaOf: Array[Short] = block.map(_.id).toArray

  /** Returns a list of entities on the map.
    *
    * @return An [[Entity]] list.
    */
  def entitiesOf: Array[Entity] = synchronized {
    entities.toArray
  }

  /** Sends a message to each player on the map.
    *
    * @param message The [[Message]] to dispatch to each client.
    */
  private def notifyAll(message: Message): Unit = {
    entities
      .filter(_.isInstanceOf[Creature])
      .foreach(e => userService.byRef(e.asInstanceOf[Creature].ref) match {
        case Some(r) => websocket.convertAndSend(s"/topic/user/$r/message", message)
        case None =>
      })
  }
}
