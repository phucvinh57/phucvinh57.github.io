import { defineConfig } from 'astro/config';
import mdx from '@astrojs/mdx';
import sitemap from '@astrojs/sitemap';
import tailwindcss from '@tailwindcss/vite';
import { fileURLToPath, URL } from 'node:url';
import remarkWikiLinks from './src/lib/remark-wiki-links.mjs';

// https://astro.build/config
export default defineConfig({
  site: 'https://phucvinh57.github.io',
  integrations: [mdx(), sitemap()],
  markdown: {
    remarkPlugins: [
      [
        remarkWikiLinks,
        {
          contentDirectory: fileURLToPath(
            new URL('./src/content/blog', import.meta.url),
          ),
        },
      ],
    ],
  },
  vite: {
    plugins: [tailwindcss()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
  },
});
