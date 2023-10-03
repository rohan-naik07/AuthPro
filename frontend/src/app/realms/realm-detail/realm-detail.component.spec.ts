import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RealmDetailComponent } from './realm-detail.component';

describe('RealmDetailComponent', () => {
  let component: RealmDetailComponent;
  let fixture: ComponentFixture<RealmDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RealmDetailComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RealmDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
