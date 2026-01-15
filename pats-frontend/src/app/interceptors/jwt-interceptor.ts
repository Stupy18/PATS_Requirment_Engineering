import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  
  console.log('JWT Interceptor - Token exists:', !!token);
  console.log('JWT Interceptor - Request URL:', req.url);
  
  if (token) {
    // Clone the request and add the Authorization header
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    console.log('JWT Interceptor - Added Authorization header');
  }
  
  return next(req);
};
