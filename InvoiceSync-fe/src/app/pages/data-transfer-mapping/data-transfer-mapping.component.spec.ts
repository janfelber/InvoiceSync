import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataTransferMappingComponent } from './data-transfer-mapping.component';

describe('ConvertorComponent', () => {
  let component: DataTransferMappingComponent;
  let fixture: ComponentFixture<DataTransferMappingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTransferMappingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataTransferMappingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
