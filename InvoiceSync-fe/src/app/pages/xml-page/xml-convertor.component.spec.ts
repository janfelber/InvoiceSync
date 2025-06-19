import { ComponentFixture, TestBed } from '@angular/core/testing';

import { XmlConvertorComponent } from './xml-convertor.component';

describe('XmlPageComponent', () => {
  let component: XmlConvertorComponent;
  let fixture: ComponentFixture<XmlConvertorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [XmlConvertorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(XmlConvertorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
