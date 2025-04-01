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
import {
  Component,
  HostBinding,
  OnDestroy,
  OnInit,
  TrackByFunction,
} from '@angular/core';
import {FormControl} from '@angular/forms';
import {PageEvent} from '@angular/material/paginator';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';
import {Subject, distinctUntilChanged, of, switchMap, tap} from 'rxjs';
import {finalize, map, take, takeUntil} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {
  CatalogDataOffer,
  CatalogPageSortingItem,
} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {LocalStoredValue} from 'src/app/core/utils/local-stored-value';
import {DeploymentEnvironmentUrlSyncService} from '../../../core/global-state/deployment-environment-url-sync.service';
import {HeaderBarConfig} from '../../../shared/common/header-bar/header-bar.model';
import {AssetDetailDialogService} from '../asset-detail-dialog/asset-detail-dialog.service';
import {FilterBoxItem} from '../filter-box/filter-box-item';
import {FilterBoxVisibleState} from '../filter-box/filter-box-visible-state';
import {ViewModeEnum, isViewMode} from '../view-selection/view-mode-enum';
import {CatalogActiveFilterPill} from './state/catalog-active-filter-pill';
import {CatalogPage} from './state/catalog-page-actions';
import {CatalogPageState} from './state/catalog-page-state';
import {CatalogPageStateModel} from './state/catalog-page-state-model';

@Component({
  selector: 'catalog-page',
  templateUrl: './catalog-page.component.html',
})
export class CatalogPageComponent implements OnInit, OnDestroy {
  @HostBinding('class.flex')
  cls = true;

  trackFilterBy: TrackByFunction<FilterBoxVisibleState> = (_, item) => item.id;

  headerConfig: HeaderBarConfig = this.buildHeaderConfig(false);

  state!: CatalogPageStateModel;
  searchText = new FormControl('');
  sortBy = new FormControl<CatalogPageSortingItem | null>(null);
  viewModeEnum = ViewModeEnum;
  viewMode = new LocalStoredValue<ViewModeEnum>(
    ViewModeEnum.GRID,
    'brokerui.viewMode',
    isViewMode,
  );

  // only tracked to prevent the component from resetting
  expandedFilterId = '';

  catalogType = this.route.snapshot.data.catalogType;

  constructor(
    private assetDetailDialogService: AssetDetailDialogService,
    private store: Store,
    private route: ActivatedRoute,
    private router: Router,
    private globalStateUtils: GlobalStateUtils,
    private deploymentEnvironmentUrlSyncService: DeploymentEnvironmentUrlSyncService,
    private titleService: Title,
  ) {
    this.setTitle();
  }

  ngOnInit(): void {
    this.deploymentEnvironmentUrlSyncService.updateFromUrlOnce(
      this.route,
      this.ngOnDestroy$,
    );
    this.deploymentEnvironmentUrlSyncService.syncToUrl(
      this.route,
      this.ngOnDestroy$,
    );
    this.initializePage();
    this.startListeningToStore();
    this.startListeningToEnvironmentChanges();
    this.startEmittingSearchText();
    this.startEmittingSortBy();
    this.openDataOfferDetailDialogOnceFromUrl();
  }

  private initializePage() {
    this.route.data
      .pipe(
        takeUntil(this.ngOnDestroy$),
        map((data) => data.catalogType === 'my-data-offers'),
        distinctUntilChanged(),
        tap((isMyDataOffers) => {
          this.headerConfig = this.buildHeaderConfig(isMyDataOffers);
        }),
        switchMap((isMyDataOffers) => {
          if (isMyDataOffers) {
            return this.globalStateUtils.userInfo$.pipe(
              take(1),
              map((it) => it.organizationId),
            );
          }

          return of(null);
        }),
      )
      .subscribe((organizationId) => {
        const initialOrganizationIds = organizationId
          ? [organizationId]
          : undefined;
        this.store.dispatch(new CatalogPage.Reset(initialOrganizationIds));
      });
  }

  private openDataOfferDetailDialogOnceFromUrl() {
    const params = this.route.firstChild?.snapshot.params;
    if (params) {
      this.openDataOfferDialog(params['assetId'], params['connectorId']);
    }
  }

  private startListeningToStore() {
    this.store
      .select<CatalogPageStateModel>(CatalogPageState)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        if (this.searchText.value != state.searchText) {
          this.searchText.setValue(state.searchText);
        }
        if (this.sortBy.value?.sorting !== state.activeSorting?.sorting) {
          this.sortBy.setValue(state.activeSorting);
        }
        if (!this.expandedFilterId && this.state.fetchedData.isReady) {
          this.expandedFilterId =
            this.state.fetchedData.data.availableFilters.fields[0].id;
        }
      });
  }

  private startEmittingSearchText() {
    this.searchText.valueChanges
      .pipe(map((value) => value ?? ''))
      .subscribe((searchText) => {
        if (searchText != this.state.searchText) {
          this.store.dispatch(new CatalogPage.UpdateSearchText(searchText));
        }
      });
  }

  private startEmittingSortBy() {
    this.sortBy.valueChanges
      .pipe(map((value) => value ?? null))
      .subscribe((value) => {
        if (value?.sorting !== this.state.activeSorting?.sorting) {
          this.store.dispatch(new CatalogPage.UpdateSorting(value));
        }
      });
  }

  onDataOfferClick(dataOffer: CatalogDataOffer) {
    this.openDataOfferDialog(dataOffer.assetId, dataOffer.connectorId);
  }

  private openDataOfferDialog(assetId: string, connectorId: string) {
    this.assetDetailDialogService
      .open(assetId, connectorId, this.ngOnDestroy$)
      .pipe(
        finalize(() => {
          this.changeUrlToCatalogRoot();
          this.setTitle();
        }),
      )
      .subscribe();

    this.router.navigate([connectorId, assetId], {
      relativeTo: this.route,
      queryParams: {
        environmentId: this.route.snapshot.queryParams.environmentId,
      },
    });
    // BreadcrumbService builds the name from the URL which is nonsensical in case of asset IDs
    this.titleService.setTitle('Catalog - Data Offer');
  }

  private changeUrlToCatalogRoot() {
    const currentRoute = this.route.snapshot;
    this.router.navigate(
      [currentRoute.url.map((segment) => segment.path).join('/')],
      {
        queryParams: {
          environmentId: currentRoute.queryParams.environmentId,
        },
      },
    );
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  onSelectedItemsChange(
    filter: FilterBoxVisibleState,
    newSelectedItems: FilterBoxItem[],
  ) {
    this.store.dispatch(
      new CatalogPage.UpdateFilterSelectedItems(
        filter.model.id,
        newSelectedItems,
      ),
    );
  }

  onSearchTextChange(filter: FilterBoxVisibleState, newSearchText: string) {
    this.store.dispatch(
      new CatalogPage.UpdateFilterSearchText(filter.model.id, newSearchText),
    );
  }

  onRemoveActiveFilterItem(item: CatalogActiveFilterPill) {
    this.store.dispatch(new CatalogPage.RemoveActiveFilterItem(item));
  }

  onPageChange(event: PageEvent) {
    this.store.dispatch(new CatalogPage.UpdatePage(event.pageIndex));
  }

  onExpandedFilterChange(filterId: string, expanded: boolean) {
    if (expanded) {
      this.expandedFilterId = filterId;
    }
  }

  private startListeningToEnvironmentChanges() {
    this.globalStateUtils.onDeploymentEnvironmentChangeSkipFirst({
      onChanged: () => this.store.dispatch(new CatalogPage.EnvironmentChange()),
      ngOnDestroy$: this.ngOnDestroy$,
    });
  }

  private buildHeaderConfig(isMyDataOffers: boolean): HeaderBarConfig {
    if (isMyDataOffers) {
      return {
        title: 'My Data Offers',
        subtitle: `Catalog of your public Data Offers`,
        headerActions: [],
      };
    }

    return {
      title: 'Catalog',
      subtitle: `Catalog of all public Data Offers`,
      headerActions: [],
    };
  }

  private setTitle() {
    this.catalogType === 'my-data-offers'
      ? this.titleService.setTitle('My Data Offers')
      : this.titleService.setTitle('Catalog');
  }
}
