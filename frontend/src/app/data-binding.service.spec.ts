import { TestBed } from '@angular/core/testing';

import { DataBindingService } from './data-binding.service';

describe('DataBindingService', () => {
  let service: DataBindingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataBindingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
