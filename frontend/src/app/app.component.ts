import { Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { LoaderService } from './util/loader.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isLoading = false;
  private loaderSubscription: Subscription | undefined;

  constructor(private loaderService: LoaderService) {
  }

  ngOnInit() {
    this.loaderSubscription = this.loaderService
      .getLoaderState()
      .subscribe((isLoading) => {
        this.isLoading = isLoading;
      });
  }

  ngOnDestroy() {
    this.loaderSubscription?.unsubscribe();
  }
}
