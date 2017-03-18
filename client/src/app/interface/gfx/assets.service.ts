import {Http, Response} from "@angular/http";
import {Injectable} from "@angular/core";

class BoxInfo {
    x: number;
    y: number;
}

class TileInfo {
    id: number;
    box: BoxInfo;
}

class TilesetInfo {

    name: string;
    tiles: Array<TileInfo>;
}

@Injectable()
export class AssetsService {

    private loader: PIXI.loaders.Loader;
    private tileset: PIXI.Texture;

    public constructor(private http: Http) {
        this.loader = PIXI.loader;
    }

    /**
     * Loads all client assets.
     *
     * This will bring in and prepare all assets including tiles, sprites and so on. It's an asynchronous
     * operation that may take some time to complete. You may pass a callback to invoke once everything has
     * been squared away.
     *
     * @param done Callback to invoke when all assets have been loaded.
     */
    public load(done: () => void): void {
        this.loader.add('tileset1', `/assets/tileset1.png`);
        this.loader.load((loader, resource) => this.loadDescriptors(loader, resource, done));
    }

    /**
     * Creates a tile sprint with the given ID.
     *
     * @param id The tile ID.
     * @returns {PIXI.Sprite} A sprite representing the tile's graphics.
     */
    public createTile(id: number): PIXI.Sprite {
        // TODO read tile from metadata
        this.tileset.frame = new PIXI.Rectangle(192, 64, 32, 32);
        return new PIXI.Sprite(this.tileset);
    }

    /**
     * Loads metadata about a tileset.
     *
     * @param loader The loader associated with the image.
     * @param resource Resources loaded for the texture.
     * @param done Callback to invoke once processing is done.
     */
    private loadDescriptors(loader: any, resource: any, done: () => void): void {
        this.http.get(`/assets/tileset1.json`)
            .map((res: Response) => res.json())
            .subscribe(data => {
                this.loadTileset(<TilesetInfo> data);
                console.log('tileset loaded');

                done();
            });
    }

    /**
     * Loads a raw tileset into a usable texture.
     *
     * @param tileset Metadata about the tileset.
     */
    private loadTileset(tileset: TilesetInfo): void {
        this.tileset = this.loader.resources['tileset1'].texture;
    }
}