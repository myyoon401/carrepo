export type PostingSummary = {
  id: number;
  companyName: string;
  title: string;
  sourceUrl: string | null;
  deadlineAt: string | null;
  createdAt: string;
};

export type PostingDetail = PostingSummary & {
  rawMd: string;
  updatedAt: string;
};

export type ExtractResult = {
  title: string | null;
  markdown: string;
};

export type CreatePostingRequest = {
  companyName: string;
  title: string;
  rawMd: string;
  sourceUrl?: string | null;
  deadlineAt?: string | null;
};
