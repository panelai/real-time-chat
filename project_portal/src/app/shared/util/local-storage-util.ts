export class LocalStorageUtil {
  static removeLoginInfo() {
    localStorage.clear();
  }

  static saveAccessToken(accessToken: string) {
    localStorage.setItem('accessToken', accessToken);
  }

  static saveRefreshToken(refreshToken: string) {
    localStorage.setItem('refreshToken', refreshToken);
  }

  static getAccessToken() {
    return localStorage.getItem('accessToken');
  }

  static getRefreshToken() {
    return localStorage.getItem('refreshToken');
  }

  static saveUser(user: string) {
    localStorage.setItem('user', user);
  }

  static getUser() {
      return localStorage.getItem('user');
  }

  // static getUserAccount(): string {
  //   return JSON.parse(localStorage.getItem('user')).account;
  // }

  static saveRoles(roles: string) {
    localStorage.setItem('roles', roles);
  }

  static getRoles() {
    return localStorage.getItem('roles');
  }
}
