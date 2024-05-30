import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandidateTrackerComponent } from './candidate-tracker.component';

describe('CandidateTrackerComponent', () => {
  let component: CandidateTrackerComponent;
  let fixture: ComponentFixture<CandidateTrackerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CandidateTrackerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateTrackerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
