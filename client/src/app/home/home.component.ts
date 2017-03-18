import {Component} from "@angular/core";
import {ApiService} from "../shared/api.service";
import {GameEvent, GameEventType} from "../shared/game-event";
import {Router} from "@angular/router";

@Component({
  selector: 'my-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {

  private loginDisabled: boolean;
  private username: string;
  private password: string;

  public constructor(private api: ApiService, private router: Router) {
    this.loginDisabled = false;

    this.api.subscribe(this.onGameEvent.bind(this));
  }

  /**
   * Handler invoked when the Login button is clicked.
   */
  private onLogin() {
    this.loginDisabled = true;

    this.api.login(this.username, this.password);
  }

  private onLogout() {
    this.api.logout();
  }

  private onGameEvent(e: GameEvent): void {
    switch (e.event) {
      case GameEventType.LOGGED_IN:
        this.router.navigateByUrl('/interface');
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
