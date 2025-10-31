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
import {
  DataOfferDetailContractOffer,
  DataOfferDetailPageResult,
  UiAsset,
} from '@sovity.de/authority-portal-client';
import {AdditionalAssetProperty} from 'src/app/core/api/additional-asset-property';
import {LanguageService} from 'src/app/core/services/languages/language.service';
import {getPrettyDataOfferType} from '../../../core/utils/data-offer-utils';
import {formatDateAgo} from '../../../shared/pipes-and-directives/ago.pipe';
import {JsonDialogService} from '../json-dialog/json-dialog.service';
import {PolicyMapper} from '../policy-editor/model/policy-mapper';
import {PropertyGridGroup} from '../property-grid-group/property-grid-group';
import {PropertyGridField} from '../property-grid/property-grid-field';
import {PropertyGridFieldService} from '../property-grid/property-grid-field.service';
import {UrlListDialogService} from '../url-list-dialog/url-list-dialog.service';

@Injectable()
export class AssetPropertyGridGroupBuilder {
  constructor(
    private propertyGridFieldService: PropertyGridFieldService,
    private jsonDialogService: JsonDialogService,
    private urlListDialogService: UrlListDialogService,
    private languageService: LanguageService,
    private policyMapper: PolicyMapper,
  ) {}

  buildDataOfferGroup(dataOffer: DataOfferDetailPageResult): PropertyGridGroup {
    const lastUpdate = formatDateAgo(
      dataOffer.connectorOfflineSinceOrLastUpdatedAt,
    );
    return {
      groupLabel: 'Data Offer',
      properties: [
        {
          icon: 'dataset',
          label: 'Offer Type',
          ...this.propertyGridFieldService.guessValue(
            getPrettyDataOfferType(dataOffer.asset.dataSourceAvailability),
          ),
        },
        {
          icon: 'category',
          label: 'Connector ID',
          ...this.propertyGridFieldService.guessValue(dataOffer.connectorId),
        },
        {
          ...{
            icon: 'link',
            label: 'Connector Endpoint',
            ...this.propertyGridFieldService.guessValue(
              dataOffer.connectorEndpoint,
            ),
          },
          copyButton: true,
        },
        {
          icon: 'cloud',
          label: 'Status',
          labelTitle: `Last updated ${lastUpdate}`,
          text:
            dataOffer.connectorOnlineStatus == 'ONLINE'
              ? `Online`
              : `Offline since ${lastUpdate}`,
        },
        {
          icon: 'account_circle',
          label: 'Organization ID',
          ...this.propertyGridFieldService.guessValue(dataOffer.organizationId),
        },
        {
          icon: 'account_circle',
          label: 'Organization Name',
          ...this.propertyGridFieldService.guessValue(
            dataOffer.organizationName,
          ),
        },
        {
          icon: 'today',
          label: 'Updated At',
          ...this.propertyGridFieldService.guessValue(
            this.propertyGridFieldService.formatDate(dataOffer.updatedAt),
          ),
        },
      ],
    };
  }

  buildAssetPropertiesGroup(
    dataOffer: DataOfferDetailPageResult,
    groupLabel: string | null,
  ): PropertyGridGroup {
    const asset = dataOffer.asset;
    const fields: PropertyGridField[] = [
      {
        icon: 'category',
        label: 'ID',
        ...this.propertyGridFieldService.guessValue(asset.assetId),
      },
      {
        icon: 'file_copy',
        label: 'Version',
        ...this.propertyGridFieldService.guessValue(asset.version),
      },
      {
        icon: 'language',
        label: 'Language',
        ...this.propertyGridFieldService.guessValue(this.getLanguage(asset)),
      },
      {
        icon: 'apartment',
        label: 'Publisher',
        ...this.propertyGridFieldService.guessValue(asset.publisherHomepage),
      },
      {
        icon: 'bookmarks',
        label: 'Endpoint Documentation',
        ...this.propertyGridFieldService.guessValue(asset.landingPageUrl),
      },
      {
        icon: 'gavel',
        label: 'Standard License',
        ...this.propertyGridFieldService.guessValue(asset.licenseUrl),
      },
      ...this.buildHttpDatasourceFields(dataOffer),
    ];

    if (asset.dataModel) {
      fields.push({
        icon: 'category',
        label: 'Data Model',
        ...this.propertyGridFieldService.guessValue(asset.dataModel),
      });
    }
    if (asset.sovereignLegalName) {
      fields.push({
        icon: 'account_balance',
        label: 'Sovereign',
        ...this.propertyGridFieldService.guessValue(asset.sovereignLegalName),
      });
    }
    if (asset.dataSampleUrls?.length) {
      fields.push(
        this.buildDataSampleUrlsField(asset.dataSampleUrls, asset.title),
      );
    }
    if (asset.referenceFileUrls?.length) {
      fields.push(
        this.buildReferenceFileUrlsField(
          asset.referenceFileUrls,
          asset.referenceFilesDescription,
          asset.title,
        ),
      );
    }
    if (asset.conditionsForUse) {
      fields.push({
        icon: 'description',
        label: 'Conditions For Use',
        ...this.propertyGridFieldService.guessValue(asset.conditionsForUse),
      });
    }
    if (asset.dataUpdateFrequency) {
      fields.push({
        icon: 'timelapse',
        label: 'Data Update Frequency',
        ...this.propertyGridFieldService.guessValue(asset.dataUpdateFrequency),
      });
    }
    if (asset.temporalCoverageFrom || asset.temporalCoverageToInclusive) {
      fields.push({
        icon: 'today',
        label: 'Temporal Coverage',
        ...this.propertyGridFieldService.guessValue(
          this.buildTemporalCoverageString(
            asset.temporalCoverageFrom,
            asset.temporalCoverageToInclusive,
          ),
        ),
      });
    }

    return {
      groupLabel,
      properties: fields,
    };
  }

  buildAdditionalPropertiesGroups(
    dataOffer: DataOfferDetailPageResult,
  ): PropertyGridGroup[] {
    const asset = dataOffer.asset;

    const additionalProperties: PropertyGridField[] = [];
    const customProperties: PropertyGridField[] = [
      asset.customJsonAsString,
      asset.customJsonLdAsString,
    ]
      .map((json) => this.buildAdditionalProperties(json))
      .flat()
      .map((prop) => {
        return {
          icon: 'category ',
          label: prop.key,
          labelTitle: prop.key,
          ...this.propertyGridFieldService.guessValue(prop.value),
        };
      });

    const privateCustomProperties: PropertyGridField[] = [
      asset.privateCustomJsonAsString,
      asset.privateCustomJsonLdAsString,
    ]
      .map((json) => this.buildAdditionalProperties(json))
      .flat()
      .map((prop) => {
        return {
          icon: 'category ',
          label: prop.key,
          labelTitle: prop.key,
          ...this.propertyGridFieldService.guessValue(prop.value),
        };
      });

    return [
      {
        groupLabel: 'Additional Properties',
        properties: additionalProperties,
      },
      {
        groupLabel: 'Custom Properties',
        properties: customProperties,
      },
      {
        groupLabel: 'Private Properties',
        properties: privateCustomProperties,
      },
    ];
  }

  buildContractOfferGroup(
    dataOffer: DataOfferDetailPageResult,
    contractOffer: DataOfferDetailContractOffer,
    i: number,
    total: number,
  ) {
    const policy = contractOffer.contractPolicy;
    const groupLabel = `Contract Offer ${total > 1 ? i + 1 : ''}`;
    const properties: PropertyGridField[] = [
      {
        icon: 'policy',
        label: 'Contract Policy',
        policy: this.policyMapper.buildPolicy(policy.expression!),
        policyErrors: policy.errors || [],
        additionalContainerClasses: 'col-span-2',
      },
      {
        icon: 'policy',
        label: 'Contract Policy JSON-LD',
        text: 'Show Policy Details',
        onclick: () =>
          this.jsonDialogService.showJsonDetailDialog({
            title: `${groupLabel} Contract Policy`,
            subtitle: dataOffer.asset.title,
            icon: 'policy',
            objectForJson: JSON.parse(
              contractOffer.contractPolicy.policyJsonLd,
            ),
          }),
      },
    ];
    return {groupLabel, properties};
  }

  private buildDataSampleUrlsField(
    dataSampleUrls: string[],
    title: string,
  ): PropertyGridField {
    return {
      icon: 'attachment',
      label: 'Data Samples',
      text: 'Show Data Samples',
      onclick: () =>
        this.urlListDialogService.showUrlListDialog({
          title: `Data Samples`,
          subtitle: title,
          icon: 'attachment',
          urls: dataSampleUrls,
        }),
    };
  }

  private buildReferenceFileUrlsField(
    referenceFileUrls: string[],
    description: string | undefined,
    title: string,
  ): PropertyGridField {
    return {
      icon: 'receipt',
      label: 'Reference Files',
      text: 'Show Reference Files',
      onclick: () =>
        this.urlListDialogService.showUrlListDialog({
          title: `Reference Files`,
          subtitle: title,
          icon: 'receipt',
          urls: referenceFileUrls,
          description: description,
        }),
    };
  }

  private buildTemporalCoverageString(
    start: Date | undefined,
    end: Date | undefined,
  ): string {
    if (!end) {
      return `Start: ${start!.toLocaleDateString()}`;
    }

    if (!start) {
      return `End: ${end.toLocaleDateString()}`;
    }

    return `${start.toLocaleDateString()} - ${end.toLocaleDateString()}`;
  }

  private getLanguage(asset: UiAsset): string {
    if (!asset.language) {
      return '';
    }
    return this.languageService.findLabel(asset.language.toUpperCase());
  }

  private buildHttpDatasourceFields(
    dataOffer: DataOfferDetailPageResult,
  ): PropertyGridField[] {
    const asset = dataOffer.asset;
    const fields: PropertyGridField[] = [];

    const hints: {label: string; value: boolean | undefined}[] = [
      {label: 'Method', value: asset.httpDatasourceHintsProxyMethod},
      {label: 'Path', value: asset.httpDatasourceHintsProxyPath},
      {label: 'Query Params', value: asset.httpDatasourceHintsProxyQueryParams},
      {label: 'Body', value: asset.httpDatasourceHintsProxyBody},
    ];
    if (hints.some((hint) => hint.value != null)) {
      const text = hints.some((hint) => hint.value)
        ? hints
            .filter((hint) => hint.value)
            .map((hint) => hint.label)
            .join(', ')
        : 'Disabled';

      fields.push({
        icon: 'api',
        label: 'HTTP Data Source Parameterization',
        text,
      });
    }

    if (asset.mediaType) {
      fields.push({
        icon: 'category',
        label: 'Content Type',
        ...this.propertyGridFieldService.guessValue(asset.mediaType),
      });
    }

    return fields;
  }

  private buildAdditionalProperties(
    json: string | undefined,
  ): AdditionalAssetProperty[] {
    const obj = this.tryParseJsonObj(json || '{}');
    return Object.entries(obj).map(
      ([key, value]): AdditionalAssetProperty => ({
        key: `${key}`,
        value:
          typeof value === 'object'
            ? JSON.stringify(value, null, 2)
            : `${value}`,
      }),
    );
  }

  private tryParseJsonObj(json: string): any {
    const bad = {'Conversion Failure': `Bad JSON: ${json}`};

    try {
      const parsed = JSON.parse(json);
      if (parsed == null) {
        return {};
      } else if (typeof parsed === 'object') {
        return parsed;
      }
    } catch (e) {}

    return bad;
  }

  buildOnRequestContactInformation(
    dataOffer: DataOfferDetailPageResult,
  ): PropertyGridGroup[] {
    const asset = dataOffer.asset;
    if (asset.dataSourceAvailability === 'LIVE') {
      return [];
    }
    return [
      {
        groupLabel: 'Contact Information',
        properties: [
          {
            icon: 'mail',
            label: 'Contact E-Mail Address',
            copyButton: true,
            hideFieldValue: true,
            ...this.propertyGridFieldService.guessValue(
              asset.onRequestContactEmail,
            ),
          },
          {
            icon: 'subject',
            label: 'Preferred E-Mail Subject',
            copyButton: true,
            ...this.propertyGridFieldService.guessValue(
              asset.onRequestContactEmailSubject,
            ),
          },
        ],
      },
    ];
  }
}
