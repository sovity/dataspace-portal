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
import {Injectable} from '@angular/core';
import * as forge from 'node-forge';

export interface CertificateAttributes {
  commonName: string;
  countryName: string;
  organizationName: string;
}

@Injectable({
  providedIn: 'root',
})
export class CertificateGenerateService {
  /**
   * generate key pair
   * @param bits
   * @returns
   */
  generateKeyPair(bits: number): Promise<forge.pki.rsa.KeyPair> {
    // Defer certificate generation to prevent the UI from freezing
    return new Promise((resolve, reject) => {
      forge.pki.rsa.generateKeyPair(
        {bits: bits, workers: -1},
        (err, keyPair) => {
          if (err) {
            reject(err);
          } else {
            resolve(keyPair);
          }
        },
      );
    });
  }

  /**
   * Generate a self signed Certificate based on the inputs
   * @param keyPair
   * @param attributes
   * @param validUntil
   * @returns
   */
  generateSelfSignedCertificate(
    keyPair: forge.pki.rsa.KeyPair,
    attributes: CertificateAttributes,
    validUntil: Date,
  ) {
    // Create a certificate
    const cert = forge.pki.createCertificate();
    cert.publicKey = keyPair.publicKey;

    // Set certificate fields
    cert.serialNumber = Date.now().toString(); // Set serial number to current Unix time;
    cert.validity.notBefore = new Date();
    cert.validity.notAfter = validUntil;

    // To support international characters in the certificate, we use the UTF8 type for everything.
    // The default type is ASN.1 PrintableString, which has an extremely restricted set of supported characters.
    let attrs: forge.pki.CertificateField[] = [
      {
        name: 'commonName',
        value: attributes.commonName,
        // The naming of this attribute and the type definitions are incorrect;
        // it holds an ASN.1 type, not a class.
        // @ts-expect-error
        valueTagClass: forge.asn1.Type.UTF8,
      },
      {
        name: 'countryName',
        value: attributes.countryName,
        // @ts-expect-error
        valueTagClass: forge.asn1.Type.UTF8,
      },
      {
        name: 'organizationName',
        value: attributes.organizationName,
        // @ts-expect-error
        valueTagClass: forge.asn1.Type.UTF8,
      },
    ];

    cert.setSubject(attrs);
    cert.setIssuer(attrs);

    cert.setExtensions([
      {
        name: 'basicConstraints',
        cA: false,
      },
      {
        name: 'keyUsage',
        digitalSignature: true,
        nonRepudiation: true,
        keyEncipherment: true,
        dataEncipherment: true,
      },
      {
        name: 'extKeyUsage',
        serverAuth: true,
        clientAuth: true,
      },
      {
        name: 'subjectKeyIdentifier',
      },
      {
        name: 'authorityKeyIdentifier',
        keyIdentifier: true,
      },
    ]);

    // Sign the certificate with the key pair
    cert.sign(keyPair.privateKey, forge.md.sha256.create());

    // Convert certificate to PEM format
    return cert;
  }

  certificateToPem(certificate: forge.pki.Certificate) {
    return forge.pki.certificateToPem(certificate);
  }

  /**
   * converts public key to pem format
   * @param publicKey
   * @returns
   */
  publicKeyToPem(publicKey: forge.pki.PublicKey) {
    return forge.pki.publicKeyToPem(publicKey);
  }

  /**
   * converts private key to pem format
   * @param privateKey
   * @returns
   */
  privateKeyToPem(privateKey: forge.pki.PrivateKey): string {
    // This outputs a generic "PRIVATE KEY" header instead of "RSA PRIVATE KEY"
    return forge.pki.privateKeyInfoToPem(
      forge.pki.wrapRsaPrivateKey(forge.pki.privateKeyToAsn1(privateKey)),
    );
  }
}
