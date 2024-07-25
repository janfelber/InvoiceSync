import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GridInvoicesComponent } from './grid-invoices.component';

describe('GridInvoicesComponent', () => {
  let component: GridInvoicesComponent;
  let fixture: ComponentFixture<GridInvoicesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GridInvoicesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GridInvoicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
