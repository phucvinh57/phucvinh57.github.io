export const BLOG_CATEGORIES = [
  'Artificial Intelligence',
  'Book review',
  'Cybersecurity',
  'Health',
  'Healthcare',
  'History',
  'Life',
  'Programming',
  'Software design',
  'Software Development',
  'Technology',
  'Vietnam',
] as const;

export type BlogCategory = (typeof BLOG_CATEGORIES)[number];
