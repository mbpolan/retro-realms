import {Component, ViewChild, ElementRef, AfterViewInit} from "@angular/core";
import {AssetsService} from "./gfx/assets.service";
import {ApiService} from "../shared/api.service";
import {GameEvent, GameEventType, MapInfoEvent} from "../shared/game-event";

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

    public constructor(private api: ApiService, private assets: AssetsService) {
        this.api.subscribe(this.processEvent);
    }

    /**
     * Handler invoked when the view is initialized.
     */
    public ngAfterViewInit() {
        this.renderer = PIXI.autoDetectRenderer(640, 480);
        this.content.nativeElement.appendChild(this.renderer.view);

        this.stage = new PIXI.Container();
        this.renderer.render(this.stage);

        this.assets.load(() => {
            console.log('ready');
        });
    }

    /**
     * Processes a game-related event from the server.
     *
     * @param e The event.
     */
    private processEvent(e: GameEvent): void {
        switch (e.event) {
            case GameEventType.MAP_INFO:
                break;

            default:
                break;
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

        // place new tiles on the stage instead
        for (let x = 0; x < e.width; x++) {
            for (let y = 0; y < e.height; y++) {
                let tile = this.assets.createTile(e.tiles[y * e.width + x]);
                tile.position.set(x * 32, y * 32);

                this.stage.addChild(tile);
            }
        }

        this.renderer.render(this.stage);
    }
}