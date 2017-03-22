import {Component} from "@angular/core";
import {ApiService} from "../shared/api.service";
import {GameEvent, GameEventType, LoginEvent, LoginResult} from "../shared/game-event";
import {Router} from "@angular/router";
import {UserInfoService} from "../shared/user-info.service";

@Component({
    selector: 'my-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent {

    private loginDisabled: boolean;
    private username: string;
    private password: string;
    private errorMessage: string;

    public constructor(private api: ApiService, private router: Router, private userInfo: UserInfoService) {
        this.loginDisabled = false;

        this.api.subscribe(this.onGameEvent.bind(this));
    }

    /**
     * Handler invoked when the Login button is clicked.
     */
    private onLogin() {
        // disable the form and clear any errors
        this.loginDisabled = true;
        this.errorMessage = null;

        // send a login request to the server
        this.api.login(this.username, this.password);
    }

    /**
     * Handler invoked when a game event is received.
     *
     * @param e The event.
     */
    private onGameEvent(e: GameEvent): void {
        switch (e.event) {
            case GameEventType.LOGGED_IN:
                let loginEvent = <LoginEvent> e;

                // login was successful
                switch (loginEvent.result) {
                    case LoginResult.SUCCESS:
                        // store the assigned player ID then route to the client interface
                        this.userInfo.setPlayerId(loginEvent.id);
                        this.router.navigateByUrl('/interface');
                        break;

                    case LoginResult.INVALID_LOGIN:
                        this.errorMessage = 'Username or password is not correct.';
                        break;

                    case LoginResult.SERVER_ERROR:
                        this.errorMessage = 'The server is currently unavailable.';
                        break;
                }

                break;

            case GameEventType.LOGGED_OUT:
                this.router.navigateByUrl('/');
                this.loginDisabled = false;
                break;

            default:
                break;
        }
    }
}
