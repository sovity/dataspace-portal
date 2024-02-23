import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {urlValidator} from 'src/app/core/utils/validators/url-validator';
import {buildCertificateInputForm} from '../../../../common/components/form-elements/certificate-input-form/certificate-input-form-builder';
import {certificateInputFormEnabledCtrls} from '../../../../common/components/form-elements/certificate-input-form/certificate-input-form-enabled-ctrls';
import {switchDisabledControls} from '../../../../core/utils/form-utils';
import {
  CertificateTabFormModel,
  CertificateTabFormValue,
  ConnectorTabFormModel,
  DEFAULT_REGISTER_CONNECTOR_PAGE_FORM_VALUE,
  RegisterConnectorPageFormModel,
  RegisterConnectorPageFormValue,
} from './register-connector-page-form-model';

@Injectable()
export class RegisterConnectorPageForm {
  group = this.buildFormGroup();

  get connectorTab(): FormGroup<ConnectorTabFormModel> {
    return this.group.controls.connectorTab;
  }

  get certificateTab(): FormGroup<CertificateTabFormModel> {
    return this.group.controls.certificateTab;
  }

  get value(): RegisterConnectorPageFormValue {
    return this.group.value as RegisterConnectorPageFormValue;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<RegisterConnectorPageFormModel> {
    const initial = DEFAULT_REGISTER_CONNECTOR_PAGE_FORM_VALUE;

    const connectorTab = this.formBuilder.nonNullable.group({
      name: [initial.connectorTab.name, [Validators.required]],
      location: [initial.connectorTab.location, [Validators.required]],
      frontendUrl: [
        initial.connectorTab.frontendUrl,
        [Validators.required, urlValidator],
      ],
      endpointUrl: [
        initial.connectorTab.endpointUrl,
        [Validators.required, urlValidator],
      ],
      managementUrl: [
        initial.connectorTab.managementUrl,
        [Validators.required, urlValidator],
      ],
    });

    const certificateTab = buildCertificateInputForm(
      this.formBuilder,
      initial.certificateTab,
    );

    switchDisabledControls<CertificateTabFormValue>(certificateTab, (value) =>
      certificateInputFormEnabledCtrls(value),
    );

    return this.formBuilder.nonNullable.group({
      canSwitchTabs: [true, Validators.requiredTrue],
      connectorTab,
      certificateTab,
    });
  }
}
