import { HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { AuthUtils } from '../auth/auth.utils';
export const API_IGNORE_ACCESS_TOKEN = ['/api/auth', '/api/refresh-token'];

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
    const indexIgnoreAPI = API_IGNORE_ACCESS_TOKEN.findIndex(apiIgnore => request.url.indexOf(apiIgnore) !== -1);
    if (indexIgnoreAPI !== -1) {
        return next(request);
    }

    const authService = inject(AuthService);
    if (authService.accessToken && !AuthUtils.isTokenExpired(authService.accessToken)) {
        // Thêm header Authorization với JWT token
        request = request.clone({
            setHeaders: {
                Authorization: `Bearer ${authService.accessToken}`
            }
        });
    }

    return next(request);
};
