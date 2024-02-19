const basePath = process.env.NEXT_PUBLIC_BASE_PATH || "";

// Specifically created to resolve the base path issue for https://capstone23.sit.kmutt.ac.th/<base-path>,
// which does not work with the default `basePath` from Next.js somehow.
export function resolveBasePath(path: string) {
  return `${basePath}${path}`;
}
