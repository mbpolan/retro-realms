import {Http, Response} from "@angular/http";
import {Injectable} from "@angular/core";
import {Entity} from "./entity";
import {TilesetInfo, SpriteSheetInfo} from "./metadata";

@Injectable()
export class AssetsService {

    private loader: PIXI.loaders.Loader;
    private tileset: PIXI.Texture;
    private spriteSheet: PIXI.Texture;
    private pendingLoad = 2;
    private loaded = false;

    // map of entity names to their animations, keyed by animation name to list of frame textures
    private entityTextures: Map<string, Map<string, Array<PIXI.Texture>>>;

    // map of tile IDs to their textures
    private tileTextures: Map<number, PIXI.Texture>;

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
        if (this.loaded) {
            done();
        }

        else {
            this.loader.add('tileset1', `/assets/tileset1.png`);
            this.loader.add('char1', `/assets/char1.png`);
            this.loader.load((loader, resource) => this.loadDescriptors(loader, resource, done));
        }
    }

    /**
     * Creates a tile sprint with the given ID.
     *
     * @param id The tile ID.
     * @returns {PIXI.Sprite} A sprite representing the tile's graphics.
     */
    public createTile(id: number): PIXI.Sprite {
        // find the tile's geometry and create a sprite from the base texture
        let texture = this.tileTextures[id];
        if (texture) {
            return new PIXI.Sprite(texture);
        }

        else {
            console.warn(`Missing texture for tile ID ${id}`);
            return null;
        }
    }

    /**
     * Creates an entity with the given name.
     *
     * @param name The name of the entity.
     * @returns {Entity} An entity with that name.
     */
    public createEntity(name: string): Entity {
        let descriptor = this.entityTextures[name];
        if (descriptor) {
            let entity = new Entity();

            for (let key in descriptor) {
                if (descriptor.hasOwnProperty(key)) {
                    entity.addAnimation(key, new PIXI.extras.AnimatedSprite(descriptor[key]));
                }
            }

            return entity;
        }

        else {
            console.warn(`Missing entity for sprite name ${name}`);
            return null;
        }
    }

    /**
     * Invokes a callback once all assets have been loaded.
     *
     * @param cb The callback.
     */
    private notifyIfDone(cb: () => void) {
        this.pendingLoad--;

        if (this.pendingLoad == 0) {
            this.loaded = true;
            cb();
        }
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

                this.notifyIfDone(done);
            });

        this.http.get(`/assets/char1.json`)
            .map((res: Response) => res.json())
            .subscribe(data => {
                this.loadSprites(<SpriteSheetInfo> data);

                this.notifyIfDone(done);
            });
    }

    /**
     * Loads a raw tileset into a usable texture.
     *
     * @param tileset Metadata about the tileset.
     */
    private loadTileset(tileset: TilesetInfo): void {
        // load the base texture for the tileset
        this.tileset = this.loader.resources['tileset1'].texture;

        // create a look-up of tile IDs to their locations on the base texture
        this.tileTextures = new Map<number, PIXI.Texture>();

        tileset.tiles.forEach(t => {
            // compute the frame of the tile based on its metadata rectangle
            let rect = new PIXI.Rectangle(t.box.x, t.box.y, t.box.w, t.box.h);
            this.tileTextures[t.id] = new PIXI.Texture(this.tileset.baseTexture, rect);
        });
    }

    /**
     * Loads a raw spritesheet into usable sprites.
     *
     * @param sheet Metadata about the spritesheet.
     */
    private loadSprites(sheet: SpriteSheetInfo): void {
        this.spriteSheet = this.loader.resources['char1'].texture;

        // process each sprite and create animated entities from them
        this.entityTextures = new Map<string, Map<string, Array<PIXI.Texture>>>();
        sheet.sprites.forEach(s => {
            let anim = new Map<string, Array<PIXI.Texture>>();

            s.animations.forEach(a => {
                let textures = a.frames.map(f => new PIXI.Texture(this.spriteSheet.baseTexture,
                    new PIXI.Rectangle(f.x, f.y, f.w, f.h)));

                anim[a.name] = textures;
            });


            this.entityTextures[s.name] = anim;
        });
    }
}