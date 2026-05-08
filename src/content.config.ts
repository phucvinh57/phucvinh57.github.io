import { defineCollection } from 'astro:content';
import { glob } from 'astro/loaders';
import { z } from 'astro/zod';

const blog = defineCollection({
  loader: glob({ pattern: '**/*.{md,mdx}', base: './src/content/blog' }),
  schema: z.object({
    title: z.string(),
    description: z.string(),
    pubDate: z.coerce.date(),
    categories: z.array(z.string()).min(1),
    relatedPosts: z.array(z.string()).default([]),
    draft: z.boolean().optional(),
  }),
});

export const collections = { blog };
