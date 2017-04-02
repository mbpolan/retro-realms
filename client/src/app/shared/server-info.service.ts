import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {ServerInfo} from "./server/server-info";
import {Observable} from "rxjs";
import {AppService} from "./app.service";

@Injectable()
export class ServerInfoService {

    private cachedInfo: Observable<ServerInfo>;

    public constructor(private app: AppService, private http: Http) {
        this.cachedInfo = null;
    }

    /**
     * Returns information that the server provides about its assets and game parameters.
     *
     * @returns {Observable<ServerInfo>} Information about the server.
     */
    public getServerInfo(): Observable<ServerInfo> {
        if (!this.cachedInfo) {
            this.cachedInfo =  this.http.get(`${this.app.contextPath()}/info`)
                .map(res => <ServerInfo> res.json())
                .catch(this.handleError);
        }

        return this.cachedInfo;
    }

    /**
     * Processes an error response from the server.
     *
     * @param e The response.
     * @returns {ErrorObservable<ServerInfo>} An error to propagate up the chain.
     */
    private handleError(e: Response | any): Observable<ServerInfo> {
        let message = e.message ? e.message : e.toString();
        console.error(message);

        return Observable.throw(message);
    }
}