/**
 * An animated sprite with multiple animations.
 *
 * This class represents a sprite that can have multiple animations and frames. Animations are keyed
 * by their names, and can be toggled at any time.
 */
export class Entity extends PIXI.Container {

    private _lastFrame: number;
    private _moving: boolean;
    private _direction: string;
    private displayName: PIXI.Text;
    private currentAnim: string;
    private anim: Map<string, PIXI.extras.AnimatedSprite>;

    public constructor(private bbox: PIXI.Rectangle) {
        super();

        this.anim = new Map<string, PIXI.extras.AnimatedSprite>();
        this._moving = false;
    }

    /**
     * Sets the display name for this entity.
     *
     * This will recompute and reposition the text above the entity.
     *
     * @param value The name of the entity.
     */
    public set name(value: string) {
        // remove any previous name text
        if (this.displayName) {
            this.removeChild(this.displayName);
        }

        // create a container for the text
        this.displayName = new PIXI.Text(value, new PIXI.TextStyle({
            fontFamily: 'Arial',
            fontSize: 12,
            fontWeight: 'bold',
            fill: '#0F4',
            miterLimit: 4,
            stroke: '#000',
            strokeThickness: 3
        }));

        this.addChild(this.displayName);
        this.repositionDisplayName();
    }

    /**
     * Adds an animation to the entity.
     *
     * @param name The name of the animation.
     * @param sprite The animation sprite to add.
     */
    public addAnimation(name: string, sprite: PIXI.extras.AnimatedSprite) {
        this.anim[name] = sprite;
    }

    /**
     * Sets the current animation.
     *
     * This will stop any ongoing animation and reset it to the new animation.
     *
     * @param name The name of the animation.
     */
    public setAnimation(name: string): void {
        this.currentAnim = name;

        // remove any previous animations and add the new one
        this.removeChildren();

        // adjust the frame to account for the bounding box by shifting the sprite so it aligns
        // at the origin
        let anim = this.anim[name];
        anim.position.set(-this.bbox.x, -this.bbox.y);

        this.addChild(anim);

        // add the entity's name text on top
        if (this.displayName) {
            this.addChild(this.displayName);
            this.repositionDisplayName();
        }
    }

    /**
     * Returns the direction the entity is currently facing.
     *
     * @returns {string} The direction.
     */
    public get direction(): string {
        return this._direction;
    }

    /**
     * Sets the direction the entity is currently facing.
     *
     * @param value The direction.
     */
    public set direction(value: string) {
        this._direction = value;
    }

    /**
     * Returns whether the entity is currently in motion or not.
     *
     * @returns {boolean} true if the entity is moving, false if stationary.
     */
    public get moving(): boolean {
        return this._moving;
    }

    /**
     * Flags whether the entity is currently in motion or not.
     *
     * @param value true if the entity is moving, false if stationary.
     */
    public set moving(value: boolean) {
        this._moving = value;
    }

    /**
     * Returns the timestamp of the last rendering frame where this entity was animated.
     *
     * @returns {number} The last frame timestamp, or 0 if never.
     */
    public get lastFrame(): number {
        return this._lastFrame;
    }

    /**
     * Sets the timestamp of the last rendering frame where this entity was animated.
     *
     * @param value The last frame timestamp.
     */
    public set lastFrame(value: number) {
        this._lastFrame = value;
    }

    /**
     * Begins animating the entity.
     */
    public animate(): void {
        let anim = this.getAnimation();
        anim.animationSpeed = 0.1;
        anim.play();
    }

    /**
     * Stops animating the entity.
     */
    public stopAnimating(): void {
        this._lastFrame = 0;
        this.getAnimation().stop();
    }

    /**
     * Returns the current animation.
     *
     * @returns {PIXI.extras.AnimatedSprite} The current sprite animation.
     */
    private getAnimation(): PIXI.extras.AnimatedSprite {
        return this.anim[this.currentAnim];
    }

    /**
     * Positions the entity's display name.
     */
    private repositionDisplayName(): void {
        // position the text to appear just above the entity's bounding box
        let rect = this.displayName.getBounds(false);
        let x = (this.bbox.width / 2) - (rect.width / 2);
        let y = -(this.bbox.y + rect.height);

        this.displayName.position.set(x, y);
    }
}