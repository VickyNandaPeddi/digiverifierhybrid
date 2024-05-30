import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItrLoginComponent } from './itr-login.component';

describe('ItrLoginComponent', () => {
  let component: ItrLoginComponent;
  let fixture: ComponentFixture<ItrLoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ItrLoginComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ItrLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
