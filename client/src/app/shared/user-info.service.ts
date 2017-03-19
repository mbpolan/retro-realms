import {Injectable} from "@angular/core";

/**
 * Service that stores information about the currently logged-in user.
 */
@Injectable()
export class UserInfoService {

    private playerId: number;

    /**
     * Returns the ID number assigned to the player by the server.
     *
     * @returns {number} The player ID.
     */
    public getPlayerId(): number {
        return this.playerId;
    }

    /**
     * Sets the ID number assigned to the player by the server.
     *
     * @param id The player ID.
     */
    public setPlayerId(id: number): void {
        this.playerId = id;
    }
}