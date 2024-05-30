import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifyRelationAddressComponent } from './verify-relation-address.component';

describe('VerifyRelationAddressComponent', () => {
  let component: VerifyRelationAddressComponent;
  let fixture: ComponentFixture<VerifyRelationAddressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VerifyRelationAddressComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifyRelationAddressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
