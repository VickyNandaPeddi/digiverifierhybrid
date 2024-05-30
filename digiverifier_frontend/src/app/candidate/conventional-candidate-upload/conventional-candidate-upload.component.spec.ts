import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConventionalCandidateUploadComponent } from './conventional-candidate-upload.component';

describe('ConventionalCandidateUploadComponent', () => {
  let component: ConventionalCandidateUploadComponent;
  let fixture: ComponentFixture<ConventionalCandidateUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConventionalCandidateUploadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConventionalCandidateUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
