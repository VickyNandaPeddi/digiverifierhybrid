import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EpfoLoginComponent } from './epfo-login.component';

describe('EpfoLoginComponent', () => {
  let component: EpfoLoginComponent;
  let fixture: ComponentFixture<EpfoLoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EpfoLoginComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EpfoLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
