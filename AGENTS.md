# Agent Guide

When adding categories to a new post, use the canonical list in
`src/lib/categories.ts`.

- Reuse an existing category when the meaning is similar.
- Add a new category to `BLOG_CATEGORIES` only when it is clearly different from
  every existing category.
- After changing categories or blog frontmatter, run `bun run build`.
