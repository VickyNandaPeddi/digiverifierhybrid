import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EpfoOtpCaptchaComponent } from './epfo-otp-captcha.component';

describe('EpfoOtpCaptchaComponent', () => {
  let component: EpfoOtpCaptchaComponent;
  let fixture: ComponentFixture<EpfoOtpCaptchaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EpfoOtpCaptchaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EpfoOtpCaptchaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
