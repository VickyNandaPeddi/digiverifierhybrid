import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UanConfirmationComponent } from './uan-confirmation.component';

describe('UanConfirmationComponent', () => {
  let component: UanConfirmationComponent;
  let fixture: ComponentFixture<UanConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UanConfirmationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UanConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
