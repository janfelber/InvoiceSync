import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataTransferDetailComponent } from './data-transfer-detail.component';

describe('DataTrasnferDetailComponent', () => {
  let component: DataTransferDetailComponent;
  let fixture: ComponentFixture<DataTransferDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTransferDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataTransferDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
