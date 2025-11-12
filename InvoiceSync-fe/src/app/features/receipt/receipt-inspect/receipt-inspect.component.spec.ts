import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptInspectComponent } from './receipt-inspect.component';

describe('ReceiptInspectComponent', () => {
  let component: ReceiptInspectComponent;
  let fixture: ComponentFixture<ReceiptInspectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReceiptInspectComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReceiptInspectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
