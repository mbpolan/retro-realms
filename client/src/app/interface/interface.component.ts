import {Component, ViewChild, ElementRef, AfterViewInit} from "@angular/core";
import {AssetsService} from "./gfx/assets.service";
import {ApiService} from "../shared/api.service";
import {
    GameEvent,
    GameEventType,
    MapInfoEvent,
    GameStateEvent,
    MoveStartEvent,
    MoveStopEvent,
    EntityAppearEvent,
    EntityDisappearEvent,
    PlayerInfo
} from "../shared/game-event";
import {KeyboardService} from "./keyboard/keyboard.service";
import {KeyEvent} from "./keyboard/key-event";
import {ISubscription} from "rxjs/Subscription";
import {Direction} from "../shared/direction";
import {Key} from "./keyboard/key";
import {UserInfoService} from "../shared/user-info.service";
import {World} from "./gfx/world";
import {Layer} from "./gfx/layer";

declare let PIXI:any;

@Component({
    selector: 'my-interface',
    templateUrl: './interface.component.html',
    styleUrls: ['./interface.component.scss']
})
export class InterfaceComponent implements AfterViewInit {

    @ViewChild('content')
    private content: ElementRef;

    readonly SCENE_WIDTH = 640;
    readonly SCENE_HEIGHT = 480;

    private renderer: PIXI.CanvasRenderer;
    private stage: PIXI.Container;
    private world: World;
    private loaded = false;
    private pendingEvents: Array<GameEvent> = [];
    private keyEventSub: ISubscription;

    public constructor(private api: ApiService, private assets: AssetsService,
                       private keyboard: KeyboardService, private user: UserInfoService) {

        this.api.subscribe(this.processEvent.bind(this));
    }

    /**
     * Handler invoked when the view is initialized.
     */
    public ngAfterViewInit() {
        this.renderer = PIXI.autoDetectRenderer(this.SCENE_WIDTH, this.SCENE_HEIGHT);
        this.content.nativeElement.appendChild(this.renderer.view);

        // create the stage and world
        this.stage = new PIXI.Container();
        this.renderer.render(this.stage);

        this.assets.load(() => {
            console.log('ready');
            this.loaded = true;

            // create the world renderer for the given tile size
            this.world = new World(this.SCENE_WIDTH, this.SCENE_HEIGHT, this.assets.tileSize);

            // flush all pending events
            this.pendingEvents.forEach(e => this.processEvent(e));
            this.pendingEvents = [];

            // listen for keyboard events
            this.keyEventSub = this.keyboard.subscribe(this.onKey.bind(this));

            this.gameLoop();
        });
    }

    /**
     * Handler invoked when the user disconnects from the server.
     */
    private onLogout(): void {
        this.api.logout();
    }

    /**
     * Handler invoked when a keyboard event is received.
     *
     * @param key The keyboard event.
     */
    private onKey(key: KeyEvent): void {
        console.log((key.pressed ? 'START': 'STOP') + ': ' + key.key);

        // if the key was pressed, initiate movement in its corresponding direction
        if (key.pressed) {
            let dir;

            // map the key to a concrete direction
            switch (key.key) {
                case Key.UP:
                    dir = Direction.UP;
                    break;

                case Key.DOWN:
                    dir = Direction.DOWN;
                    break;

                case Key.LEFT:
                    dir = Direction.LEFT;
                    break;

                case Key.RIGHT:
                    dir = Direction.RIGHT;
                    break;
            }

            this.api.startMovement(dir);
        }

        // otherwise, stop the player's movement
        else {
            this.api.stopMovement();
        }
    }

    /**
     * Runs the rendering loop.
     */
    private gameLoop = () => {
        requestAnimationFrame(this.gameLoop);

        this.world.update();

        this.renderer.render(this.stage);
    };

    /**
     * Processes a game-related event from the server.
     *
     * @param e The event.
     */
    private processEvent(e: GameEvent): void {
        if (!this.loaded) {
            console.log(`received event before initialized: ${e.event}`);
            this.pendingEvents.push(e);
        }

        else {
            switch (e.event) {
                case GameEventType.MAP_INFO:
                    this.processMapInfo(<MapInfoEvent> e);
                    break;

                case GameEventType.GAME_STATE:
                    this.processGameState(<GameStateEvent> e);
                    break;

                case GameEventType.MOVE_START:
                    this.processMoveStart(<MoveStartEvent> e);
                    break;

                case GameEventType.MOVE_STOP:
                    this.processMoveStop(<MoveStopEvent> e);
                    break;

                case GameEventType.ENTITY_APPEAR:
                    this.processEntityAppear(<EntityAppearEvent> e);
                    break;

                case GameEventType.ENTITY_DISAPPEAR:
                    this.processEntityDisappear(<EntityDisappearEvent> e);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Adds an entity to the map.
     *
     * @param p The entity to add.
     */
    private addEntity(p: PlayerInfo): void {
        let entity = this.assets.createEntity(p.sprite);
        entity.name = p.username;
        entity.setAnimation(`walk-${p.dir}`);
        entity.position.set(p.x, p.y);

        this.world.addEntity(p.id, entity);

        // tell the world that this entity represents us (the local player)
        if (p.id === this.user.getPlayerId()) {
            this.world.primaryEntity = entity;
        }
    }

    /**
     * Processes a game event containing map information.
     *
     * @param e The event.
     */
    private processMapInfo(e: MapInfoEvent): void {
        // clear out the stage entirely and add the world back to it
        this.stage.removeChildren();
        this.world.reset(e.width, e.height);
        this.stage.addChild(this.world);

        console.log(`map: ${e.width} x ${e.height} tiles and ${e.layers.length} layers`);

        // transform the raw map data into tiles and form layers for the world
        let layers = e.layers.map(layer => {
            let layerTiles = [];

            for (let x = 0; x < e.width; x++) {
                for (let y = 0; y < e.height; y++) {
                    let id = layer[y * e.width + x];
                    let tile = this.assets.createTile(id);

                    if (tile) {
                        // position the tile accordingly, then add it to the stage
                        tile.position.set(x * tile.width, y * tile.height);
                        layerTiles.push(tile);
                    }
                }
            }

            return new Layer(layerTiles);
        });

        this.world.addLayers(layers);

        // place sprites on top of the tiles
        e.players.forEach(p => this.addEntity(p));
    }

    /**
     * Processes a game event containing state information.
     *
     * @param e The event.
     */
    private processGameState(e: GameStateEvent): void {
        // process any changed players
        e.players.forEach(p => {
            let entity = this.world.getEntityById(p.id);
            if (entity) {
                entity.position.set(p.x, p.y);
            }

            else {
                console.warn(`No such entity with ID ${p.id}`);
            }
        });
    }

    /**
     * Processes a game event containing an entity that started moving.
     *
     * @param e The event.
     */
    private processMoveStart(e: MoveStartEvent): void {
        let entity = this.world.getEntityById(e.id);

        // set the entity's direction and start their animation
        if (entity) {
            entity.lastFrame = 0;
            entity.direction = e.dir;
            entity.moving = true;
            entity.setAnimation(`walk-${e.dir}`);
            entity.animate();
        }

        else {
            console.warn(`No such entity with ID ${e.id}`);
        }
    }

    /**
     * Processes a game event containing an entity that stopped moving.
     *
     * @param e The event.
     */
    private processMoveStop(e: MoveStopEvent): void {
        let entity = this.world.getEntityById(e.id);

        // stop the entity from moving, update their final position and also end any animations
        if (entity) {
            entity.moving = false;
            entity.position.set(e.x, e.y);
            entity.stopAnimating();

            // if this entity is us, make sure to clear any pending movement keys
            if (e.id == this.user.getPlayerId()) {
                this.keyboard.clearKeys();
            }
        }

        else {
            console.warn(`No such entity with ID ${e.id}`);
        }
    }

    /**
     * Processes a game event containing an entity that has appeared on the map.
     *
     * @param e The event.
     */
    private processEntityAppear(e: EntityAppearEvent): void {
        this.addEntity(e.player);
    }

    /**
     * Processes a game event containing an entity that has disappeared from the map.
     *
     * @param e The event.
     */
    private processEntityDisappear(e: EntityDisappearEvent): void {
        this.world.removeEntityById(e.id);
    }
}