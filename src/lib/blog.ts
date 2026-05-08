import type { CollectionEntry } from 'astro:content';

export type BlogPost = CollectionEntry<'blog'>;

export interface PostYearGroup {
  year: string;
  posts: BlogPost[];
}

export function getPublishedPosts(posts: BlogPost[]) {
  return posts.filter((post) => !post.data.draft);
}

export function sortPosts(posts: BlogPost[]) {
  return [...posts].sort(
    (a, b) => b.data.pubDate.getTime() - a.data.pubDate.getTime(),
  );
}

export function groupPostsByYear(posts: BlogPost[]): PostYearGroup[] {
  const groups = new Map<string, BlogPost[]>();

  for (const post of sortPosts(posts)) {
    const year = post.data.pubDate.getFullYear().toString();
    groups.set(year, [...(groups.get(year) ?? []), post]);
  }

  return [...groups.entries()]
    .map(([year, groupedPosts]) => ({ year, posts: groupedPosts }))
    .sort((a, b) => Number(b.year) - Number(a.year));
}

export function slugifyCategory(category: string) {
  return category
    .toLowerCase()
    .trim()
    .replace(/&/g, 'and')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');
}

function safelyDecode(value: string) {
  try {
    return decodeURIComponent(value);
  } catch {
    return value;
  }
}

export function normalizePostReference(value: string) {
  return safelyDecode(value)
    .trim()
    .replace(/^\/+/, '')
    .replace(/^blog\//, '')
    .replace(/\/+$/, '')
    .replace(/\.mdx?$/i, '')
    .replace(/\/index$/i, '')
    .toLowerCase()
    .replace(/\s+/g, '-');
}

function getPostsByReference(posts: BlogPost[]) {
  const postsByReference = new Map<string, BlogPost>();

  for (const post of posts) {
    postsByReference.set(normalizePostReference(getPostSlug(post)), post);
    postsByReference.set(normalizePostReference(post.id), post);
    postsByReference.set(slugifyCategory(post.data.title), post);
  }

  return postsByReference;
}

export function resolvePostReference(posts: BlogPost[], reference: string) {
  const postsByReference = getPostsByReference(posts);

  return (
    postsByReference.get(normalizePostReference(reference)) ??
    postsByReference.get(slugifyCategory(reference))
  );
}

export function getPostSlug(post: BlogPost) {
  return post.id.replace(/\/index$/, '');
}

export function getPostUrl(post: BlogPost) {
  return `/blog/${getPostSlug(post)}/`;
}

export function getCategoryUrl(category: string) {
  return `/blog/category/${slugifyCategory(category)}/`;
}

export function getCategories(posts: BlogPost[]) {
  const categories = new Map<string, string>();

  for (const post of posts) {
    for (const category of post.data.categories) {
      categories.set(slugifyCategory(category), category);
    }
  }

  return [...categories.entries()]
    .map(([slug, name]) => ({ slug, name }))
    .sort((a, b) => a.name.localeCompare(b.name));
}

export function getRelatedPosts(post: BlogPost, posts: BlogPost[]) {
  const relatedPosts = new Map<string, BlogPost>();
  const currentPostSlug = getPostSlug(post);

  function addRelatedPost(relatedPost: BlogPost | undefined) {
    if (!relatedPost || getPostSlug(relatedPost) === currentPostSlug) {
      return;
    }

    relatedPosts.set(getPostSlug(relatedPost), relatedPost);
  }

  for (const reference of post.data.relatedPosts) {
    addRelatedPost(resolvePostReference(posts, reference));
  }

  for (const candidatePost of sortPosts(posts)) {
    if (getPostSlug(candidatePost) === currentPostSlug) {
      continue;
    }

    const linksToCurrentPost = candidatePost.data.relatedPosts.some(
      (reference) => {
        const relatedPost = resolvePostReference(posts, reference);

        return relatedPost
          ? getPostSlug(relatedPost) === currentPostSlug
          : false;
      },
    );

    if (linksToCurrentPost) {
      addRelatedPost(candidatePost);
    }
  }

  return [...relatedPosts.values()];
}

export function readingTime(body: string) {
  const textOnly = body.replace(/<[^>]+>/g, ' ');
  const wordCount = textOnly.trim().split(/\s+/).filter(Boolean).length;
  const minutes = Math.max(1, Math.ceil(wordCount / 200));

  return `${minutes} min read`;
}
