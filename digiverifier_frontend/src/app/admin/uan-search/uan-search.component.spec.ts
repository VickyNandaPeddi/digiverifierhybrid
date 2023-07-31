import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UanSearchComponent } from './uan-search.component';

describe('UanSearchComponent', () => {
  let component: UanSearchComponent;
  let fixture: ComponentFixture<UanSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UanSearchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UanSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
