import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerEmailTemplateComponent } from './customer-email-template.component';

describe('CustomerEmailTemplateComponent', () => {
  let component: CustomerEmailTemplateComponent;
  let fixture: ComponentFixture<CustomerEmailTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomerEmailTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerEmailTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
