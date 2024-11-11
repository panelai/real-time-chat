import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);

    return next(req).pipe(
        catchError((err: HttpErrorResponse) => {
            if ([401, 403].includes(err.status) && !authService._authenticated) {
                // Tự động đăng xuất nếu nhận được phản hồi 401 hoặc 403 từ API
                // authService.signOut();
            }

            // Xử lý thông báo lỗi
            const error = err.error?.message || err.message || 'Unknown error';
            console.error('Interceptor error:', error);

            // Sử dụng throwError với Error object để rõ ràng hơn
            return throwError(() => new Error(error));
        })
    );
};
