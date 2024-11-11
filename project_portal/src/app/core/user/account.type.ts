export interface Account {
  //id?: string;
  id: string;
  username: string;
  email: string;
  phone?: string;
  introduction?:string;
  role?: string;
  isActived?: boolean;
  //token?: string;
}

export interface ConnectedUser extends Account {
  authorities?: string[];
}
