import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurgedReportComponent } from './purged-report.component';

describe('PurgedReportComponent', () => {
  let component: PurgedReportComponent;
  let fixture: ComponentFixture<PurgedReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PurgedReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurgedReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
