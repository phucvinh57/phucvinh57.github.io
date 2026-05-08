import rss from '@astrojs/rss';
import { getCollection } from 'astro:content';
import type { APIRoute } from 'astro';
import { BLOG, SITE } from '@/consts';
import { getPostUrl, getPublishedPosts, sortPosts } from '@/lib/blog';

export const GET: APIRoute = async (context) => {
  const posts = sortPosts(getPublishedPosts(await getCollection('blog')));

  return rss({
    title: SITE.NAME,
    description: BLOG.DESCRIPTION,
    site: context.site ?? SITE.URL,
    items: posts.map((post) => ({
      title: post.data.title,
      description: post.data.description,
      pubDate: post.data.pubDate,
      link: getPostUrl(post),
    })),
  });
};
