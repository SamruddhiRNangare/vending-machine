import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MachineContextsComponent } from './machine-contexts.component';

describe('MachineContextsComponent', () => {
  let component: MachineContextsComponent;
  let fixture: ComponentFixture<MachineContextsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MachineContextsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MachineContextsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
