import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LetteracceptComponent } from './letteraccept.component';

describe('LetteracceptComponent', () => {
  let component: LetteracceptComponent;
  let fixture: ComponentFixture<LetteracceptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LetteracceptComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LetteracceptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
