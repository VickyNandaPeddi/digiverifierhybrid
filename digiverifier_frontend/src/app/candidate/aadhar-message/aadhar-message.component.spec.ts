import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AadharMessageComponent } from './aadhar-message.component';

describe('AadharMessageComponent', () => {
  let component: AadharMessageComponent;
  let fixture: ComponentFixture<AadharMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AadharMessageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AadharMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
