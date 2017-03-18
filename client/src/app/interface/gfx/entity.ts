/**
 * An animated sprite with multiple animations.
 *
 * This class represents a sprite that can have multiple animations and frames. Animations are keyed
 * by their names, and can be toggled at any time.
 */
export class Entity extends PIXI.Container {

    private currentAnim: string;
    private anim: Map<string, PIXI.extras.AnimatedSprite>;

    public constructor() {
        super();
        this.anim = new Map<string, PIXI.extras.AnimatedSprite>();
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
        this.addChild(this.anim[name]);
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
}