import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatDialogWindowComponent } from './mat-dialog-window.component';

describe('MatDialogWindowComponent', () => {
  let component: MatDialogWindowComponent;
  let fixture: ComponentFixture<MatDialogWindowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogWindowComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MatDialogWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
