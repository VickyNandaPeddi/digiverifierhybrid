import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VendorUtilizationReportComponent } from './vendor-utilization-report.component';

describe('VendorUtilizationReportComponent', () => {
  let component: VendorUtilizationReportComponent;
  let fixture: ComponentFixture<VendorUtilizationReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VendorUtilizationReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VendorUtilizationReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
