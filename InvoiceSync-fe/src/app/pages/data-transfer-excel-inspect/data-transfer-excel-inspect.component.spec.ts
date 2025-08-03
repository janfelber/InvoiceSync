import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataTransferExcelInspectComponent } from './data-transfer-excel-inspect.component';

describe('DataTransferExcelInspectComponent', () => {
  let component: DataTransferExcelInspectComponent;
  let fixture: ComponentFixture<DataTransferExcelInspectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTransferExcelInspectComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataTransferExcelInspectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
