import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DigidocumentComponent } from './digidocument.component';

describe('DigidocumentComponent', () => {
  let component: DigidocumentComponent;
  let fixture: ComponentFixture<DigidocumentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DigidocumentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DigidocumentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
