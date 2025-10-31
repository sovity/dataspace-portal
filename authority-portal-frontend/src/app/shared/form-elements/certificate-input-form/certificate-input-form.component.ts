/*
 * Data Space Portal
 * Copyright (C) 2025 sovity GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import {Component, HostBinding, Input, OnDestroy} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Subject} from 'rxjs';
import {
  CertificateAttributes,
  CertificateGenerateService,
} from 'src/app/core/services/certificate-generate.service';
import {CertificateFormModel} from './certificate-input-form-model';

@Component({
  selector: 'app-certificate-input-form',
  templateUrl: './certificate-input-form.component.html',
})
export class CertificateInputFormComponent implements OnDestroy {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.gap-8')
  @HostBinding('class.my-10')
  cls = true;

  @Input()
  organizationLegalName = '';

  @Input()
  commonName = '';

  @Input()
  location = '';

  @Input()
  group!: FormGroup<CertificateFormModel>;

  get canGenerate(): boolean {
    return (
      !this.isGenerating && !this.hasGenerated && !!this.organizationLegalName
    );
  }

  get isBringOwnCert(): boolean {
    return this.group.controls.bringOwnCert.value;
  }

  isGenerating: boolean = false;
  hasGenerated: boolean = false;
  isDisabled: boolean = false;

  private ngOnDestroy$ = new Subject();

  constructor(private certificateGenerateService: CertificateGenerateService) {}

  onGenerateCertificateClick() {
    if (this.isGenerating) {
      return;
    }

    this.isGenerating = true;
    this.generateAndDisplayCertificateAndPrivateKey()
      .finally(() => (this.isGenerating = false))
      .then(
        () => {
          this.hasGenerated = true;
        },
        (e) => {
          console.error('error while generating certificate', e);
          alert('error while generating certificate. Please check console');
        },
      );
  }

  private async generateAndDisplayCertificateAndPrivateKey() {
    this.group.controls.generatedCertificate.setValue('');
    this.group.controls.generatedPrivateKey.setValue('');

    const certificateAttributes: CertificateAttributes = {
      commonName: this.commonName,
      countryName: this.location,
      organizationName: this.organizationLegalName,
    };

    const keyPair = await this.certificateGenerateService.generateKeyPair(2048);
    const validUntil = this.plusYears(new Date(), 5);
    const selfSignedCertificate =
      this.certificateGenerateService.generateSelfSignedCertificate(
        keyPair,
        certificateAttributes,
        validUntil,
      );

    const pemCertificate = this.certificateGenerateService.certificateToPem(
      selfSignedCertificate,
    );
    const pemPrivateKey = this.certificateGenerateService.privateKeyToPem(
      keyPair.privateKey,
    );

    this.group.controls.generatedCertificate.setValue(pemCertificate);
    this.group.controls.generatedPrivateKey.setValue(pemPrivateKey);
  }

  private plusYears(date: Date, plusYears: number) {
    const copy = new Date(date);
    copy.setFullYear(date.getFullYear() + plusYears);
    return copy;
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
