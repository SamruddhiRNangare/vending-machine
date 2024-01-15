import { TestBed, inject } from '@angular/core/testing';

import { MachineContextService } from './machine-context.service';

describe('MachineContextService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MachineContextService]
    });
  });

  it('should be created', inject([MachineContextService], (service: MachineContextService) => {
    expect(service).toBeTruthy();
  }));
});
