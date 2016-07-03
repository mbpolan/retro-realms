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
    lastRef = lastRef + 1

    println(s"Loaded map of ${block.length} tiles and ${entities.size} entities")
  }

  /** Adds a player to the map.
    *
    * @param sessionId The session ID of the player.
    * @param spriteId The ID of the sprite animation to represent the player.
    * @param name The visible name of player.
    * @param bounds The position and bounding rectangle for the player.
    * @return The player's assigned internal ID.
    */
  def addPlayer(sessionId: String, spriteId: String, name: String, bounds: Rect): Int = synchronized {
    addCreatureInternal(new Player(websocket, sessionId, lastRef, spriteId, name, bounds))
  }

  /** Adds a non-player controlled character to the map.
    *
    * @param spriteId The ID of the sprite animation to represent the creature.
    * @param name The visible name of the creature.
    * @param bounds The position and bounding rectangle for the creature.
    * @param dir The direction the creature is initially facing.
    * @param speed The speed at which the creature moves.
    * @return The creature's assigned internal ID.
    */
  def addNpc(spriteId: String, name: String, bounds: Rect, dir: Direction, speed: Int): Int = synchronized {
    addCreatureInternal(new Npc(lastRef, spriteId, name, bounds, dir, speed))
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

  /** Returns a [[Player]] on the map whose internal ID matches of that given one.
    *
    * @param ref The internal ID of the player.
    * @return The corresponding [[Player]].
    */
  def playerBy(ref: Int): Option[Player] = synchronized {
    creatureBy(ref).map(_.asInstanceOf[Player])
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

  /** Returns a list of entities that are [[Animate]].
    *
    * @return A list of animate objects on the map.
    */
  def animateObjects: Vector[Animate] = synchronized {
    entities.filter(_.isInstanceOf[Animate]).map(_.asInstanceOf[Animate])
  }

  /** Returns a list of players that are in viewing distance of a given player.
    *
    * @param ref The internal ID of the player.
    * @return A list of [[Creature]]s that are near the player.
    */
  def nearByPlayers(ref: Int): Vector[Player] = {
    // FIXME: right now we only have one map area, so return all players
    entities.filter(_.isInstanceOf[Player]).map(_.asInstanceOf[Player])
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
        // has the creature changed the direction its facing? if so, notify nearby creatures
        if (dir != e.dir) {
          notifyAll(DirChangeMessage(ref = e.ref, dir = dir.value))
          e.dir = dir
        }

        canMoveToDelta(e, dir.dx, dir.dy) match {
          case true =>
            e.pos = Rect(e.pos.x + dir.dx, e.pos.y + dir.dy, e.pos.w, e.pos.h)
            notifyAll(EntityMoveMessage(ref = ref, x = e.pos.x, y = e.pos.y))

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
      .filter(_.isInstanceOf[Player])
      .foreach(_.asInstanceOf[Player].send(message))
  }

  /** Adds a creature to the map and notifies nearby players.
    *
    * @param c The creature to add.
    * @return The creature's assigned internal ID.
    */
  private def addCreatureInternal(c: Creature): Int = {
    entities = entities :+ c
    lastRef += 1

    notifyAll(AddEntityMessage(ref = c.ref, id = c.id, name = c.name, dir = Direction.Down.value, x = c.pos.x, y = c.pos.y))

    c.ref
  }
}
