import {Component, OnDestroy, OnInit, inject} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {Observable, Subscription, filter} from 'rxjs';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
})
export class BreadcrumbComponent implements OnDestroy {
  fullRoute: string[] = [];
  routeSubscription!: Subscription;

  constructor() {
    const router = inject(Router);
    this.routeSubscription = router.events
      .pipe(filter((value) => value instanceof NavigationEnd))
      .subscribe((value) => {
        this.fullRoute = [];
        router.url
          .toString()
          .split('/')
          .forEach((r, idx) => {
            // console.log(`r (${idx})`, r);
            r = r.charAt(0).toUpperCase() + r.slice(1);
            if (r.toLowerCase() != 'dashboard') {
              this.fullRoute.push(r === '' ? 'Home' : r.replace('_', ' '));
            }
          });
        document.title = this.fullRoute[this.fullRoute.length - 1] || 'Portal';
      });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
  }

  getLink(idx: number) {
    return this.fullRoute
      .slice(1, idx + 1)
      .map((s) => s.toLowerCase())
      .join('/');
  }
}
