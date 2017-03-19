import {Component, ViewChild, ElementRef, AfterViewInit} from "@angular/core";
import {AssetsService} from "./gfx/assets.service";
import {ApiService} from "../shared/api.service";
import {GameEvent, GameEventType, MapInfoEvent, GameStateEvent} from "../shared/game-event";
import {KeyboardService} from "./keyboard/keyboard.service";
import {KeyEvent} from "./keyboard/key-event";
import {ISubscription} from "rxjs/Subscription";

declare let PIXI:any;

@Component({
    selector: 'my-interface',
    templateUrl: './interface.component.html',
    styleUrls: ['./interface.component.scss']
})
export class InterfaceComponent implements AfterViewInit {

    @ViewChild('content')
    private content: ElementRef;

    private renderer: PIXI.CanvasRenderer;
    private stage: PIXI.Container;
    private loaded = false;
    private pendingEvents: Array<GameEvent> = [];
    private keyEventSub: ISubscription;

    public constructor(private api: ApiService, private assets: AssetsService, private keyboard: KeyboardService) {
        this.api.subscribe(this.processEvent.bind(this));
    }

    /**
     * Handler invoked when the view is initialized.
     */
    public ngAfterViewInit() {
        this.renderer = PIXI.autoDetectRenderer(960, 640);
        this.content.nativeElement.appendChild(this.renderer.view);

        this.stage = new PIXI.Container();
        this.renderer.render(this.stage);

        this.assets.load(() => {
            console.log('ready');
            this.loaded = true;

            // flush all pending events
            this.pendingEvents.forEach(e => this.processEvent(e));
            this.pendingEvents = [];

            // listen for keyboard events
            this.keyEventSub = this.keyboard.subscribe(this.onKey);

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
    }

    /**
     * Runs the rendering loop.
     */
    private gameLoop = () => {
        requestAnimationFrame(this.gameLoop);

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

                default:
                    break;
            }
        }
    }

    /**
     * Processes a game event containing map information.
     *
     * @param e The event.
     */
    private processMapInfo(e: MapInfoEvent): void {
        // clear out the stage entirely
        this.stage.removeChildren();

        console.log(`map: ${e.width} x ${e.height} tiles`);

        // place new tiles on the stage instead
        for (let x = 0; x < e.width; x++) {
            for (let y = 0; y < e.height; y++) {
                let id = e.tiles[y * e.width + x];
                let tile = this.assets.createTile(id);

                if (tile) {
                    // position the tile accordingly, then add it to the stage
                    tile.position.set(x * tile.width, y * tile.height);
                    this.stage.addChild(tile);
                }
            }
        }

        // place sprites on top of the tiles
        e.players.forEach(p => {
            let entity = this.assets.createEntity(p.sprite);
            entity.setAnimation(`walk-${p.dir}`);
            entity.position.set(p.x, p.y);

            this.stage.addChild(entity);
        });
    }

    /**
     * Processes a game event containing state information.
     *
     * @param e The event.
     */
    private processGameState(e: GameStateEvent): void {

    }
}