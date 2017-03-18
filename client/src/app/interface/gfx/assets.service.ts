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
    private tileset: PIXI.Sprite;

    public constructor(private http: Http) {
        this.loader = PIXI.loader;
    }

    public load(done: () => void): void {
        this.loader.add('tileset1', `/assets/tileset1.png`);
        this.loader.load((loader, resource) => this.loadDescriptors(loader, resource, done));
    }

    public createTile(id: number): PIXI.Sprite {
        return new PIXI.Sprite(new PIXI.Texture(this.tileset.texture.baseTexture, new PIXI.Rectangle(0, 0, 32, 32)));
    }

    private loadDescriptors(loader: any, resource: any, done: () => void): void {
        this.http.get(`/assets/tileset1.json`)
            .map((res: Response) => res.json())
            .subscribe(data => {
                this.loadTileset(<TilesetInfo> data);
                console.log('tileset loaded');

                done();
            });
    }

    private loadTileset(tileset: TilesetInfo): void {
        this.tileset = new PIXI.Sprite(this.loader.resources['tileset1'].texture);
    }
}