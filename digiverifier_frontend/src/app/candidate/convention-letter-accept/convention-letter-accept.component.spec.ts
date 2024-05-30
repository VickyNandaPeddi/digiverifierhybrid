import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConventionLetterAcceptComponent } from './convention-letter-accept.component';

describe('ConventionLetterAcceptComponent', () => {
  let component: ConventionLetterAcceptComponent;
  let fixture: ComponentFixture<ConventionLetterAcceptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConventionLetterAcceptComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConventionLetterAcceptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
