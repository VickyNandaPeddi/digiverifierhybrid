import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmploymentTrackerSheetComponent } from './employment-tracker-sheet.component';

describe('EmploymentTrackerSheetComponent', () => {
  let component: EmploymentTrackerSheetComponent;
  let fixture: ComponentFixture<EmploymentTrackerSheetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmploymentTrackerSheetComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmploymentTrackerSheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
