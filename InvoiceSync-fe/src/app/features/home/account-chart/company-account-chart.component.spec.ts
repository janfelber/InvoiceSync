import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyAccountChartComponent } from './company-account-chart.component';

describe('AccountChartComponent', () => {
  let component: CompanyAccountChartComponent;
  let fixture: ComponentFixture<CompanyAccountChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyAccountChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyAccountChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
