import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EpfoLoginNewComponent } from './epfo-login-new.component';

describe('EpfoLoginNewComponent', () => {
  let component: EpfoLoginNewComponent;
  let fixture: ComponentFixture<EpfoLoginNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EpfoLoginNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EpfoLoginNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
