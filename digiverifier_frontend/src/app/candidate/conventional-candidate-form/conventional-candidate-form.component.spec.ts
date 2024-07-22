import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConventionalCandidateFormComponent } from './conventional-candidate-form.component';

describe('ConventionalCandidateFormComponent', () => {
  let component: ConventionalCandidateFormComponent;
  let fixture: ComponentFixture<ConventionalCandidateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConventionalCandidateFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConventionalCandidateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
