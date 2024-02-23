import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule} from '@angular/router';
import {NgxsModule} from '@ngxs/store';
import {DevUtilsModule} from 'src/app/common/components/dev-utils/dev-utils.module';
import {LoadingElementModule} from 'src/app/common/components/loading-element/loading-element.module';
import {PipesAndDirectivesModule} from 'src/app/common/components/pipes-and-directives/pipes-and-directives.module';
import {MaterialModule} from 'src/app/common/material/material.module';
import {SharedModule} from 'src/app/shared/shared.module';
import {FormElementsModule} from '../../../common/components/form-elements/form-elements.module';
import {RequestConnectorPageComponent} from './request-connector-page/request-connector-page.component';
import {RequestConnectorPageStateImpl} from './state/request-connector-page-state-impl';

@NgModule({
  declarations: [RequestConnectorPageComponent],
  exports: [RequestConnectorPageComponent],
  imports: [
    BrowserModule,
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,

    NgxsModule.forFeature([RequestConnectorPageStateImpl]),

    DevUtilsModule,
    LoadingElementModule,
    FormElementsModule,
    MaterialModule,
    PipesAndDirectivesModule,
    SharedModule,
  ],
})
export class RequestConnectorPageModule {}
