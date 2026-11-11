export type User = {
  id: number;
  name: string;
  phone: string;
  email: string;
  title: string;
  createdAt: string;
};

export type SignUpRequest = {
  name: string;
  phone: string;
  email: string;
  title: string;
};

export type UpdateUserRequest = Partial<SignUpRequest>;

export type Resume = {
  documentId: number;
  versionId: number;
  seq: number;
  html: string;
  changeNote: string | null;
  createdAt: string;
};
