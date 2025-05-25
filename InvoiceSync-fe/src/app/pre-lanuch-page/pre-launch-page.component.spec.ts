import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreLaunchPageComponent } from './pre-launch-page.component';

describe('PreLanuchPageComponent', () => {
  let component: PreLaunchPageComponent;
  let fixture: ComponentFixture<PreLaunchPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreLaunchPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreLaunchPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
