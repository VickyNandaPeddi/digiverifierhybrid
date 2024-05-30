import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VendorDashboardPendingDetailsComponent } from './vendor-dashboard-pending-details.component';

describe('VendorDashboardPendingDetailsComponent', () => {
  let component: VendorDashboardPendingDetailsComponent;
  let fixture: ComponentFixture<VendorDashboardPendingDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VendorDashboardPendingDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VendorDashboardPendingDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
