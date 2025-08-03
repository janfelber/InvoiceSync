import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataTransferTableComponent } from './data-transfer-table.component';

describe('DataTransferComponent', () => {
  let component: DataTransferTableComponent;
  let fixture: ComponentFixture<DataTransferTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTransferTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataTransferTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
