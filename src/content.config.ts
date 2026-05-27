import { defineCollection } from 'astro:content';
import { glob } from 'astro/loaders';
import { z } from 'astro/zod';
import { BLOG_CATEGORIES } from './lib/categories';

const blog = defineCollection({
  loader: glob({ pattern: '**/*.{md,mdx}', base: './src/content/blog' }),
  schema: z.object({
    title: z.string(),
    description: z.string(),
    pubDate: z.coerce.date(),
    categories: z.array(z.enum(BLOG_CATEGORIES)).min(1),
    relatedPosts: z.array(z.string()).default([]),
    references: z
      .array(
        z.object({
          title: z.string(),
          url: z.url(),
          description: z.string().optional(),
        }),
      )
      .default([]),
    draft: z.boolean().optional(),
  }),
});

export const collections = { blog };
