export const IMAGE_BASE_URL = process.env.NEXT_PUBLIC_IMAGE_BASE_URL;

export function getImageUrlFromKey(key: string) {
  const url = new URL(key, IMAGE_BASE_URL);
  return url.toString();
}
