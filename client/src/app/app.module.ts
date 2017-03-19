import {NgModule, ApplicationRef} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HttpModule} from '@angular/http';
import {FormsModule} from '@angular/forms';

import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {ApiService} from './shared';
import {SocketService} from './shared';
import {routing} from './app.routing';

import {removeNgStyles, createNewHosts} from '@angularclass/hmr';
import {InterfaceComponent} from "./interface/interface.component";
import {AssetsService} from "./interface/gfx/assets.service";
import {UserInfoService} from "./shared/user-info.service";
import {KeyboardService} from "./interface/keyboard/keyboard.service";

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        FormsModule,
        routing
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        InterfaceComponent
    ],
    providers: [
        AssetsService,
        ApiService,
        KeyboardService,
        SocketService,
        UserInfoService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
    constructor(public appRef: ApplicationRef) {
    }

    hmrOnInit(store) {
        console.log('HMR store', store);
    }

    hmrOnDestroy(store) {
        let cmpLocation = this.appRef.components.map(cmp => cmp.location.nativeElement);
        // recreate elements
        store.disposeOldHosts = createNewHosts(cmpLocation);
        // remove styles
        removeNgStyles();
    }

    hmrAfterDestroy(store) {
        // display new elements
        store.disposeOldHosts();
        delete store.disposeOldHosts;
    }
}
