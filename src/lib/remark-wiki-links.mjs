import fs from 'node:fs';
import path from 'node:path';

const WIKI_LINK_PATTERN = /\[\[([^\]|#]+)(#[^\]|]+)?(?:\|([^\]]+))?\]\]/g;
const MARKDOWN_FILE_PATTERN = /\.mdx?$/i;
const SKIPPED_NODE_TYPES = new Set([
  'code',
  'definition',
  'html',
  'inlineCode',
  'link',
  'linkReference',
]);

function safelyDecode(value) {
  try {
    return decodeURIComponent(value);
  } catch {
    return value;
  }
}

function slugify(value) {
  return value
    .toLowerCase()
    .trim()
    .replace(/&/g, 'and')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');
}

function normalizeReference(value) {
  return safelyDecode(value)
    .trim()
    .replace(/^\/+/, '')
    .replace(/^blog\//, '')
    .replace(/\/+$/, '')
    .replace(MARKDOWN_FILE_PATTERN, '')
    .replace(/\/index$/i, '')
    .toLowerCase()
    .replace(/\s+/g, '-');
}

function getReferenceLabel(reference) {
  return safelyDecode(reference)
    .replace(MARKDOWN_FILE_PATTERN, '')
    .replace(/[-_]+/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();
}

function walkMarkdownFiles(directory) {
  if (!fs.existsSync(directory)) {
    return [];
  }

  const entries = fs.readdirSync(directory, { withFileTypes: true });
  const files = [];

  for (const entry of entries) {
    const entryPath = path.join(directory, entry.name);

    if (entry.isDirectory()) {
      files.push(...walkMarkdownFiles(entryPath));
    } else if (MARKDOWN_FILE_PATTERN.test(entry.name)) {
      files.push(entryPath);
    }
  }

  return files;
}

function getFrontmatterTitle(source) {
  const frontmatter = /^---\s*\n([\s\S]*?)\n---/.exec(source)?.[1];

  if (!frontmatter) {
    return undefined;
  }

  const titleLine = frontmatter
    .split('\n')
    .find((line) => line.trim().startsWith('title:'));

  if (!titleLine) {
    return undefined;
  }

  return titleLine
    .replace(/^\s*title:\s*/, '')
    .trim()
    .replace(/^["']|["']$/g, '');
}

function buildBlogIndex(contentDirectory) {
  const postsByReference = new Map();

  for (const filePath of walkMarkdownFiles(contentDirectory)) {
    const source = fs.readFileSync(filePath, 'utf8');
    const relativePath = path.relative(contentDirectory, filePath);
    const slug = relativePath
      .replace(MARKDOWN_FILE_PATTERN, '')
      .replace(/\\/g, '/')
      .replace(/\/index$/i, '');
    const title = getFrontmatterTitle(source) ?? getReferenceLabel(slug);
    const post = {
      slug,
      title,
      url: `/blog/${slug}/`,
    };

    postsByReference.set(normalizeReference(slug), post);
    postsByReference.set(slugify(title), post);
  }

  return postsByReference;
}

function resolvePost(reference, postsByReference) {
  return (
    postsByReference.get(normalizeReference(reference)) ??
    postsByReference.get(slugify(reference))
  );
}

function replaceWikiLinks(value, postsByReference) {
  const nodes = [];
  let lastIndex = 0;

  for (const match of value.matchAll(WIKI_LINK_PATTERN)) {
    const [rawMatch, reference, heading, alias] = match;
    const matchIndex = match.index ?? 0;

    if (matchIndex > lastIndex) {
      nodes.push({
        type: 'text',
        value: value.slice(lastIndex, matchIndex),
      });
    }

    const post = resolvePost(reference, postsByReference);
    const label = alias?.trim() || post?.title || getReferenceLabel(reference);

    if (post) {
      nodes.push({
        type: 'link',
        url: `${post.url}${heading ? `#${slugify(heading.slice(1))}` : ''}`,
        title: null,
        children: [{ type: 'text', value: label }],
      });
    } else {
      nodes.push({ type: 'text', value: label });
    }

    lastIndex = matchIndex + rawMatch.length;
  }

  if (lastIndex < value.length) {
    nodes.push({ type: 'text', value: value.slice(lastIndex) });
  }

  return nodes.length > 0 ? nodes : [{ type: 'text', value }];
}

function transformNode(node, postsByReference) {
  if (!node || typeof node !== 'object' || SKIPPED_NODE_TYPES.has(node.type)) {
    return;
  }

  if (!Array.isArray(node.children)) {
    return;
  }

  const children = [];

  for (const child of node.children) {
    if (child.type === 'text' && child.value.includes('[[')) {
      children.push(...replaceWikiLinks(child.value, postsByReference));
    } else {
      transformNode(child, postsByReference);
      children.push(child);
    }
  }

  node.children = children;
}

export default function remarkWikiLinks(options = {}) {
  const contentDirectory =
    options.contentDirectory ?? path.join(process.cwd(), 'src/content/blog');

  return function transformWikiLinks(tree) {
    transformNode(tree, buildBlogIndex(contentDirectory));
  };
}
