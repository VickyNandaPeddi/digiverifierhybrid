import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DigiOtpComponent } from './digi-otp.component';

describe('DigiOtpComponent', () => {
  let component: DigiOtpComponent;
  let fixture: ComponentFixture<DigiOtpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DigiOtpComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DigiOtpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
