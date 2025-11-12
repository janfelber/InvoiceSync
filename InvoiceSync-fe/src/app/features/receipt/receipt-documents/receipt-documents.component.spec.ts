import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptDocumentsComponent } from './receipt-documents.component';

describe('ReceiptDocumentsComponent', () => {
  let component: ReceiptDocumentsComponent;
  let fixture: ComponentFixture<ReceiptDocumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReceiptDocumentsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReceiptDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
