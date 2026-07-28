import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate']) }
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('refreshes the token transparently when it expires mid-session (no reload)', () => {
    let result: any;
    service.refreshToken().subscribe(res => result = res);

    const req = httpMock.expectOne(r => r.url.includes('/auth/refresh-token'));
    expect(req.request.method).toBe('POST');

    req.flush({ accessToken: 'new-token-123', user: { role: 'ROLE_USER', features: [] } });

    expect(result.accessToken).toBe('new-token-123');
    expect(service.getToken()).toBe('new-token-123');
  });

  it('restores the session after a page reload using the refresh cookie', () => {
    let result: boolean | undefined;
    service.init().subscribe(res => result = res);

    const req = httpMock.expectOne(r => r.url.includes('/auth/refresh-token'));
    req.flush({ accessToken: 'new-token-123', user: { role: 'ROLE_USER', features: [] } });

    expect(result).toBe(true);
    expect(service.getToken()).toBe('new-token-123');
  });
});