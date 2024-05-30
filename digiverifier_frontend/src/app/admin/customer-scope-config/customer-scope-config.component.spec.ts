import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerScopeConfigComponent } from './customer-scope-config.component';

describe('CustomerScopeConfigComponent', () => {
  let component: CustomerScopeConfigComponent;
  let fixture: ComponentFixture<CustomerScopeConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomerScopeConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerScopeConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
