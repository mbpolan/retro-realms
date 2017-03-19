import {Injectable} from "@angular/core";
import {ISubscription} from "rxjs/Subscription";
import {Subject} from "rxjs";
import {KeyEvent} from "./key-event";
import {Key} from "./key";

/**
 * Service that manages and reports keyboard events.
 */
@Injectable()
export class KeyboardService {

    private events: Subject<KeyEvent>;
    private _activeKeys: Array<Key>;

    public constructor() {
        this.events = new Subject<KeyEvent>();
        this._activeKeys = [];

        this.bindKey('up', Key.UP);
        this.bindKey('down', Key.DOWN);
        this.bindKey('left', Key.LEFT);
        this.bindKey('right', Key.RIGHT);
    }

    /**
     * Returns the list of keys that are currently pressed.
     *
     * The most recently pressed key will be at the head of the list.
     *
     * @returns {Array<Key>} An array of keys.
     */
    public get activeKeys(): Array<Key> {
        return this._activeKeys;
    }

    /**
     * Subscribes for keyboard events.
     *
     * @param cb The callback to invoke when an event is available.
     * @returns {Subscription} Handle for the subscription.
     */
    public subscribe(cb: (KeyEvent) => void): ISubscription {
        return this.events.subscribe(cb);
    }

    /**
     * Handler invoked when a key is pressed.
     *
     * @param e The event.
     * @param key The key.
     */
    private onKeyDown(e: ExtendedKeyboardEvent, key: Key): void {
        this.suppressEvent(e);

        // has this key not been pressed yet?
        if (this._activeKeys.indexOf(key) == -1) {
            this._activeKeys.unshift(key);

            this.events.next(new KeyEvent(key, true));
        }
    }

    /**
     * Handler invoked when a key is released.
     *
     * @param e The event.
     * @param key The key.
     */
    private onKeyUp(e: ExtendedKeyboardEvent, key: Key): void {
        this.suppressEvent(e);

        // remove the key from the active key list
        this._activeKeys = this._activeKeys.filter(k => k != key);

        this.events.next(new KeyEvent(key, false));
    }

    /**
     * Binds a keyboard event to a keyup and keydown handlers.
     *
     * @param sequence The key sequence to bind.
     * @param key The {Key} to represent this event.
     */
    private bindKey(sequence: string, key: Key): void {
        Mousetrap.bind(sequence, (e: ExtendedKeyboardEvent) => this.onKeyDown(e, key), 'keydown');
        Mousetrap.bind(sequence, (e: ExtendedKeyboardEvent) => this.onKeyUp(e, key), 'keyup');
    }

    /**
     * Configures a key event to suppress its default behavior.
     *
     * @param e The event.
     */
    private suppressEvent(e: ExtendedKeyboardEvent): void {
        if (e.preventDefault) {
            e.preventDefault();
        }

        else {
            e.returnValue = false;
        }
    }
}