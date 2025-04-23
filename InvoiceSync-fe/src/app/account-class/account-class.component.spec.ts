import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountClassComponent } from './account-class.component';

describe('AccountClassComponent', () => {
  let component: AccountClassComponent;
  let fixture: ComponentFixture<AccountClassComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountClassComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountClassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
