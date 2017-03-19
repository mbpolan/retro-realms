import {Key} from "./key";

/**
 * Encapsulation of a keyboard event that's relevant to the client.
 */
export class KeyEvent {

    private _key: Key;
    private _pressed: boolean;

    public constructor(key: Key, pressed: boolean) {
        this._key = key;
        this._pressed = pressed;
    }


    public get key(): Key {
        return this._key;
    }

    public get pressed(): boolean {
        return this._pressed;
    }
}
