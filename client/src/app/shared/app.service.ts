import {Injectable} from "@angular/core";

@Injectable()
export class AppService {

    public contextPath(): string {
        return process.env.BASE_URL;
    }
}