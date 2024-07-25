import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadListComponentComponent } from './download-list-component.component';

describe('DownloadListComponentComponent', () => {
  let component: DownloadListComponentComponent;
  let fixture: ComponentFixture<DownloadListComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DownloadListComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DownloadListComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
