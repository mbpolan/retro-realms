import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './home/home.component';
import {InterfaceComponent} from "./interface/interface.component";

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'interface', component: InterfaceComponent }
];

export const routing = RouterModule.forRoot(routes);
