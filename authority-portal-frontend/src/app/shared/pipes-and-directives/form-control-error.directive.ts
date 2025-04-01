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
import {Directive, ElementRef, Input, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormControl} from '@angular/forms';
import {Subscription} from 'rxjs';
import {debounceTime, map} from 'rxjs/operators';

@Directive({
  selector: '[appFormControlError]',
})
export class FormControlErrorDirective implements OnInit, OnDestroy {
  @Input() control!: AbstractControl | null;
  @Input() fieldName!: string;

  public errorMessage$!: Subscription | undefined;

  constructor(private el: ElementRef) {}

  ngOnInit(): void {
    const formControl = this.control as FormControl;
    this.errorMessage$ = formControl.statusChanges
      .pipe(
        debounceTime(500),
        map(() => {
          const {dirty, invalid, touched} = formControl || {};
          return (dirty || touched) && invalid
            ? this.getErrorMessage(formControl, this.fieldName)
            : '';
        }),
      )
      .subscribe((response) => {
        this.el.nativeElement.textContent = response;
      });
  }

  /**
   * this method generates a list of error messages based on the control errors object
   * @param control control
   * @param fieldName Field name to show on the error message
   * @returns
   */
  private getErrorMessage(
    control: FormControl | null,
    fieldName: string,
  ): string {
    if (!control || !control.errors) {
      return '';
    }

    const errors = control.errors;

    if (errors['required']) {
      return `${fieldName} is required`;
    }

    if (errors['email']) {
      return `${fieldName} is invalid`;
    }

    if (errors['isNotUnique']) {
      return `${fieldName} exists already`;
    }

    if (errors['mismatch']) {
      return `${fieldName}s doesn't match`;
    }

    if (errors['minlength']) {
      const {requiredLength} = errors['minlength'];
      return `${fieldName} minimum length is ${requiredLength} character${
        requiredLength !== 1 ? 's' : ''
      }`;
    }

    if (errors['invalidUrl']) {
      return `${fieldName} invalid, Please ensure it adheres to the following format: https://www.example.com`;
    }

    if (errors['invalidSubdomain']) {
      return `${fieldName} needs to be a valid subdomain`;
    }

    if (errors['errorMessage']) {
      return `Invalid Identification Credentials`;
    }

    if (errors['pattern']) {
      return `${fieldName} invalid pattern`;
    }

    if (errors['hasNumber']) {
      return `${fieldName} should contain at least one number (0-9)`;
    }

    if (errors['hasCapitalCase']) {
      return `${fieldName} should contain at least one uppercase character (A-Z)`;
    }

    if (errors['hasSmallCase']) {
      return `${fieldName} should contain at least one lowercase character (a-z)`;
    }

    if (errors['hasSpecialCharacters']) {
      return `${fieldName} should contain at least one special character !“#$%&()*+,-./:;<=>?@[\\]^_{|}~`;
    }

    return '';
  }

  ngOnDestroy(): void {
    this.errorMessage$?.unsubscribe();
  }
}
