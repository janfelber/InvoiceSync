import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceLimiterComponent } from './invoice-limiter.component';

describe('InvoiceLimiterComponent', () => {
  let component: InvoiceLimiterComponent;
  let fixture: ComponentFixture<InvoiceLimiterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvoiceLimiterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvoiceLimiterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
