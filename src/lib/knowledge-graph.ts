import type { BlogPost } from '@/lib/blog';
import {
  getCategoryUrl,
  getPostSlug,
  getPostUrl,
  slugifyCategory,
} from '@/lib/blog';

export type KnowledgeGraphNodeType = 'post' | 'category' | 'reference';
export type KnowledgeGraphLinkType = 'category' | 'related' | 'mention';

export interface KnowledgeGraphNode {
  id: string;
  label: string;
  type: KnowledgeGraphNodeType;
  url?: string;
  description?: string;
  date?: string;
  degree: number;
}

export interface KnowledgeGraphLink {
  id: string;
  source: string;
  target: string;
  type: KnowledgeGraphLinkType;
  weight: number;
}

export interface KnowledgeGraphData {
  nodes: KnowledgeGraphNode[];
  links: KnowledgeGraphLink[];
  stats: {
    postCount: number;
    categoryCount: number;
    referenceCount: number;
    linkCount: number;
  };
}

const WIKI_LINK_PATTERN = /\[\[([^\]|#]+)(?:#[^\]|]+)?(?:\|[^\]]+)?\]\]/g;
const BLOG_LINK_PATTERN =
  /^(?:https?:\/\/[^)\s]+)?\/blog\/([^)\s#?]+)\/?(?:[?#][^)\s]*)?$/i;
const MARKDOWN_LINK_PATTERN = /\[([^\]]+)\]\(([^)\s]+)(?:\s+["'][^"']*["'])?\)/g;

interface ReferenceCandidate {
  reference: string;
  label?: string;
  url?: string;
  description?: string;
}

function safelyDecode(value: string) {
  try {
    return decodeURIComponent(value);
  } catch {
    return value;
  }
}

function normalizeReference(value: string) {
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

function normalizeTitle(value: string) {
  return slugifyCategory(value);
}

function getReferenceLabel(reference: string) {
  return safelyDecode(reference)
    .replace(/\.mdx?$/i, '')
    .replace(/[-_]+/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();
}

function stripMarkdown(value: string) {
  return value
    .replace(/[`*_~]/g, '')
    .replace(/\s+/g, ' ')
    .trim();
}

function getBlogReferenceFromUrl(url: string) {
  return BLOG_LINK_PATTERN.exec(url)?.[1];
}

function getReferenceKey(reference: ReferenceCandidate) {
  return reference.url
    ? `external:${reference.url}`
    : `internal:${normalizeReference(reference.reference)}`;
}

function extractReferences(body: string) {
  const references = new Map<string, ReferenceCandidate>();

  function addReference(reference: ReferenceCandidate) {
    references.set(getReferenceKey(reference), reference);
  }

  for (const match of body.matchAll(WIKI_LINK_PATTERN)) {
    if (match[1]) {
      addReference({ reference: match[1] });
    }
  }

  for (const match of body.matchAll(MARKDOWN_LINK_PATTERN)) {
    const matchIndex = match.index ?? 0;

    if (body[matchIndex - 1] === '!') {
      continue;
    }

    const [, label, url] = match;
    const blogReference = getBlogReferenceFromUrl(url);

    if (blogReference) {
      addReference({ reference: blogReference });
    } else if (/^https?:\/\//i.test(url)) {
      addReference({
        reference: url,
        label: stripMarkdown(label),
        url,
        description: 'External reference.',
      });
    }
  }

  return [...references.values()];
}

function createLinkId(
  source: string,
  target: string,
  type: KnowledgeGraphLinkType,
) {
  const [left, right] = [source, target].sort();

  return `${type}:${left}->${right}`;
}

export function buildKnowledgeGraph(posts: BlogPost[]): KnowledgeGraphData {
  const nodes = new Map<string, KnowledgeGraphNode>();
  const links = new Map<string, KnowledgeGraphLink>();
  const postsByReference = new Map<string, BlogPost>();

  for (const post of posts) {
    postsByReference.set(normalizeReference(getPostSlug(post)), post);
    postsByReference.set(normalizeReference(post.id), post);
    postsByReference.set(normalizeTitle(post.data.title), post);
  }

  function addNode(node: Omit<KnowledgeGraphNode, 'degree'>) {
    if (!nodes.has(node.id)) {
      nodes.set(node.id, { ...node, degree: 0 });
    }
  }

  function addLink(
    source: string,
    target: string,
    type: KnowledgeGraphLinkType,
    weight: number,
  ) {
    if (source === target) {
      return;
    }

    const id = createLinkId(source, target, type);

    if (!links.has(id)) {
      links.set(id, { id, source, target, type, weight });
    }
  }

  function resolvePost(reference: string) {
    return (
      postsByReference.get(normalizeReference(reference)) ??
      postsByReference.get(normalizeTitle(reference))
    );
  }

  function addReferenceNode(reference: ReferenceCandidate | string) {
    const candidate =
      typeof reference === 'string' ? { reference } : reference;
    const normalizedReference = normalizeReference(candidate.reference);
    const id = `reference:${normalizedReference}`;

    addNode({
      id,
      label: candidate.label || getReferenceLabel(candidate.reference),
      type: 'reference',
      url: candidate.url || `/blog/${normalizedReference}/`,
      description:
        candidate.description || 'Referenced note that does not exist yet.',
    });

    return id;
  }

  for (const post of posts) {
    const postId = `post:${getPostSlug(post)}`;

    addNode({
      id: postId,
      label: post.data.title,
      type: 'post',
      url: getPostUrl(post),
      description: post.data.description,
      date: post.data.pubDate.toISOString(),
    });

    for (const category of post.data.categories) {
      const categoryId = `category:${slugifyCategory(category)}`;

      addNode({
        id: categoryId,
        label: category,
        type: 'category',
        url: getCategoryUrl(category),
        description: 'Category',
      });

      addLink(postId, categoryId, 'category', 1);
    }

    for (const relatedPostReference of post.data.relatedPosts) {
      const relatedPost = resolvePost(relatedPostReference);
      const relatedPostId = relatedPost
        ? `post:${getPostSlug(relatedPost)}`
        : addReferenceNode(relatedPostReference);

      addLink(postId, relatedPostId, 'related', 2);
    }

    for (const reference of extractReferences(post.body ?? '')) {
      const mentionedPost = resolvePost(reference.reference);
      const mentionedPostId = mentionedPost
        ? `post:${getPostSlug(mentionedPost)}`
        : addReferenceNode(reference);

      addLink(postId, mentionedPostId, 'mention', 1.5);
    }
  }

  const degreeByNode = new Map<string, number>();

  for (const link of links.values()) {
    degreeByNode.set(link.source, (degreeByNode.get(link.source) ?? 0) + 1);
    degreeByNode.set(link.target, (degreeByNode.get(link.target) ?? 0) + 1);
  }

  for (const node of nodes.values()) {
    node.degree = degreeByNode.get(node.id) ?? 0;
  }

  const graphNodes = [...nodes.values()].sort((a, b) => {
    if (a.type !== b.type) {
      return a.type.localeCompare(b.type);
    }

    return a.label.localeCompare(b.label);
  });
  const graphLinks = [...links.values()];

  return {
    nodes: graphNodes,
    links: graphLinks,
    stats: {
      postCount: graphNodes.filter((node) => node.type === 'post').length,
      categoryCount: graphNodes.filter((node) => node.type === 'category').length,
      referenceCount: graphNodes.filter((node) => node.type === 'reference')
        .length,
      linkCount: graphLinks.length,
    },
  };
}
