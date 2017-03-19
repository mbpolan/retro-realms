/**
 * Message that contains the results of a user's login attempt.
 *
 * The message contains a flag to indicate whether the login was successful or not. Additionally,
 * if the login was successful, then the player's assigned ID number will be included.
 */
export class LoginMessage {
    id: number;
    success: boolean;
}
