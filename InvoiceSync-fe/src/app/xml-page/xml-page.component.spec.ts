import { ComponentFixture, TestBed } from '@angular/core/testing';

import { XmlPageComponent } from './xml-page.component';

describe('XmlPageComponent', () => {
  let component: XmlPageComponent;
  let fixture: ComponentFixture<XmlPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [XmlPageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(XmlPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
