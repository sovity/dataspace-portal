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
import {Injectable, OnDestroy} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {ignoreElements, switchMap, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {
  CatalogPageQuery,
  CatalogPageResult,
  CnfFilter,
  CnfFilterAttribute,
} from '@sovity.de/authority-portal-client';
import {CatalogApiService} from 'src/app/core/api/catalog-api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {Fetched} from 'src/app/core/utils/fetched';
import {associateAsObj} from 'src/app/core/utils/object-utils';
import {FilterBoxItem} from '../../filter-box/filter-box-item';
import {
  FilterBoxModel,
  buildFilterBoxModelWithNewData,
} from '../../filter-box/filter-box-model';
import {FilterBoxVisibleState} from '../../filter-box/filter-box-visible-state';
import {CatalogActiveFilterPill} from './catalog-active-filter-pill';
import {CatalogPage} from './catalog-page-actions';
import {
  CatalogPageStateModel,
  DEFAULT_CATALOG_PAGE_STATE_MODEL,
} from './catalog-page-state-model';
import {NgxsUtils} from './ngxs-utils';

type Ctx = StateContext<CatalogPageStateModel>;

@State<CatalogPageStateModel>({
  name: 'CatalogPageState',
  defaults: DEFAULT_CATALOG_PAGE_STATE_MODEL,
})
@Injectable()
export class CatalogPageState implements OnDestroy {
  constructor(
    private catalogApiService: CatalogApiService,
    private ngxsUtils: NgxsUtils,
    private globalStateUtils: GlobalStateUtils,
  ) {
    this.ngxsUtils.sampleTime(CatalogPage.NeedFetch, CatalogPage.Fetch, 200);
  }

  @Action(CatalogPage.Reset)
  onReset(ctx: Ctx, action: CatalogPage.Reset) {
    let state = ctx.getState();
    state = DEFAULT_CATALOG_PAGE_STATE_MODEL;
    if (action.initialOrganizationIds?.length) {
      state = this._addFilterBoxes(state, [
        this._buildOrganizationIdFilterBoxModel(action.initialOrganizationIds),
      ]);
    }
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.Fetch, {cancelUncompleted: true})
  onFetch(ctx: Ctx): Observable<never> {
    const query = this.buildCatalogPageQuery(ctx.getState());

    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.catalogApiService.catalogPage(deploymentEnvironmentId, query),
      ),
      Fetched.wrap({failureMessage: 'Failed fetching data offers.'}),
      this.ngxsUtils.takeUntil(CatalogPage.Reset),
      tap((fetchedData) => {
        let state = {...ctx.getState(), fetchedData};
        fetchedData.ifReady((data) => {
          state = this._withReadyCatalogResult(state, data);
        });
        ctx.setState(state);
      }),
      ignoreElements(),
    );
  }

  @Action(CatalogPage.UpdatePage)
  onUpdatePage(ctx: Ctx, action: CatalogPage.UpdatePage) {
    let state = ctx.getState();
    state = {...state, pageZeroBased: action.pageZeroBased};
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateSearchText)
  onUpdateSearchText(ctx: Ctx, action: CatalogPage.UpdateSearchText) {
    let state = ctx.getState();
    state = {...state, searchText: action.searchText};
    state = this._recalculateActiveFilterItems(state);
    state = this._resetPage(state);
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateSorting)
  onUpdateSorting(ctx: Ctx, action: CatalogPage.UpdateSorting) {
    let state = ctx.getState();
    state = {...state, activeSorting: action.sorting};
    state = this._resetPage(state);
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateFilterSelectedItems)
  onUpdateFilterSelectedItems(
    ctx: Ctx,
    action: CatalogPage.UpdateFilterSelectedItems,
  ) {
    let state = ctx.getState();
    state = this._updateFilterModelById(state, action.filterId, (model) => ({
      ...model,
      selectedItems: action.selectedItems,
    }));
    state = this._recalculateActiveFilterItems(state);
    state = this._resetPage(state);
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateFilterSearchText)
  onUpdateFilterSearchText(
    ctx: Ctx,
    action: CatalogPage.UpdateFilterSearchText,
  ) {
    let state = ctx.getState();
    state = this._updateFilterModelById(state, action.filterId, (model) => ({
      ...model,
      searchText: action.searchText,
    }));
    ctx.setState(state);
  }

  @Action(CatalogPage.RemoveActiveFilterItem)
  onRemoveActiveFilterItem(
    ctx: Ctx,
    action: CatalogPage.RemoveActiveFilterItem,
  ) {
    const state: CatalogPageStateModel = ctx.getState();
    const item = action.item;
    if (item.type === 'SEARCH_TEXT') {
      // Reset the Search
      this.onUpdateSearchText(ctx, new CatalogPage.UpdateSearchText(''));
    } else if (item.type === 'SELECTED_FILTER_ITEM') {
      // Remove the selected filter option
      const filterId = item.selectedFilterId!;
      const itemId = item.selectedFilterItem!.id;
      const selectedItems = state.filters[filterId].model.selectedItems;

      this.onUpdateFilterSelectedItems(
        ctx,
        new CatalogPage.UpdateFilterSelectedItems(
          filterId,
          selectedItems.filter((it) => it.id !== itemId),
        ),
      );
    }
  }

  @Action(CatalogPage.EnvironmentChange)
  onEnvironmentChange(ctx: Ctx) {
    let state = ctx.getState();
    state = this._resetPage(state);
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  private _buildOrganizationIdFilterBoxModel(
    endpoints: string[],
  ): FilterBoxModel {
    const items: FilterBoxItem[] = endpoints.map((x) => ({
      type: 'ITEM',
      id: x,
      label: x,
    }));
    return {
      id: 'organization',
      title: 'Organization',
      selectedItems: items,
      availableItems: items,
      searchText: '',
      displayType: 'TITLE_ONLY',
    };
  }

  private _addFilterBoxes(
    state: CatalogPageStateModel,
    filterBoxes: FilterBoxModel[],
  ): CatalogPageStateModel {
    return this._recalculateActiveFilterItems({
      ...state,
      filters: {
        ...state.filters,
        ...associateAsObj(
          filterBoxes,
          (x) => x.id,
          (x) => FilterBoxVisibleState.buildVisibleState(x),
        ),
      },
    });
  }

  private _resetPage(state: CatalogPageStateModel): CatalogPageStateModel {
    return {...state, pageZeroBased: 0, paginationDisabled: true};
  }

  private _recalculateActiveFilterItems(
    state: CatalogPageStateModel,
  ): CatalogPageStateModel {
    const activeFilterItems: CatalogActiveFilterPill[] = [];
    if (state.searchText?.trim()) {
      activeFilterItems.push({
        type: 'SEARCH_TEXT',
        label: 'Search',
        value: state.searchText,
      });
    }

    const buildFilterItem = (
      filter: FilterBoxVisibleState,
      item: FilterBoxItem,
    ): CatalogActiveFilterPill => ({
      type: 'SELECTED_FILTER_ITEM',
      label: filter.model.title,
      value: item.label,
      selectedFilterId: filter.model.id,
      selectedFilterItem: item,
    });

    Object.values(state.filters)
      .flatMap((filter) =>
        filter.model.selectedItems.map((item) => buildFilterItem(filter, item)),
      )
      .forEach((item) => activeFilterItems.push(item));

    return {
      ...state,
      activeFilterPills: activeFilterItems,
    };
  }

  private _updateFilterModelById(
    state: CatalogPageStateModel,
    filterId: string,
    patcher: (filter: FilterBoxModel) => FilterBoxModel,
  ): CatalogPageStateModel {
    const newModel = patcher(state.filters[filterId].model);
    return {
      ...state,
      filters: {
        ...state.filters,
        [filterId]: FilterBoxVisibleState.buildVisibleState(newModel),
      },
    };
  }

  private _withReadyCatalogResult(
    state: CatalogPageStateModel,
    data: CatalogPageResult,
  ): CatalogPageStateModel {
    const filters = this.buildFiltersWithNewData(
      data.availableFilters,
      state.filters,
    );

    state = {
      ...state,
      isPageReady: true,
      paginationDisabled: false,
      paginationMetadata: data.paginationMetadata,
      sortings: data.availableSortings,
      activeSorting: state.activeSorting ?? data.availableSortings[0] ?? null,
      filters,
    };

    return this._recalculateActiveFilterItems(state);
  }

  private buildFiltersWithNewData(
    cnfFilter: CnfFilter,
    oldFilter: Record<string, FilterBoxVisibleState>,
  ): Record<string, FilterBoxVisibleState> {
    return associateAsObj(
      cnfFilter.fields,
      (it: CnfFilterAttribute) => it.id,
      (it: CnfFilterAttribute) => {
        const old = oldFilter[it.id]?.model ?? null;
        const updated = buildFilterBoxModelWithNewData(it, old);
        return FilterBoxVisibleState.buildVisibleState(updated);
      },
    );
  }

  private buildCatalogPageQuery(state: CatalogPageStateModel) {
    const query: CatalogPageQuery = {
      searchQuery: state.searchText,
      filter: {
        selectedAttributeValues: Object.values(state.filters)
          .map((filter) => ({
            id: filter.model.id,
            selectedIds: [...filter.selectedIds],
          }))
          .filter((it) => it.selectedIds.length),
      },
      sorting: state.activeSorting?.sorting,
      pageOneBased: state.pageZeroBased + 1,
    };
    return query;
  }

  ngOnDestroy$ = new Subject<void>();

  ngOnDestroy() {
    this.ngOnDestroy$.next();
    this.ngOnDestroy$.complete();
  }
}
