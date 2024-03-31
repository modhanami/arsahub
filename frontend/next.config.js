// TODO: use .mts and import resolveBasePath
/** @type {import('next').NextConfig} */
const basePath = process.env.NEXT_PUBLIC_BASE_PATH || "";
const nextConfig = {
  assetPrefix: basePath,
  async redirects() {
    return [
      {
        source: "/",
        destination: `${basePath}/overview`,
        permanent: false,
      },
    ];
  },
  images: {
    remotePatterns: [
      {
        hostname: "cdn.7tv.app",
      },
    ],
  },
  typescript: {
    ignoreBuildErrors: true, // TODO: Remove this when all errors are fixed
  },
  output: "standalone",
  async rewrites() {
    if (process.env.NODE_ENV === "development") {
      return [
        {
          source: "/api/:path*",
          destination: "http://localhost:8080/api/:path*", // Proxy to Backend
        },
      ];
    } else {
      return [];
    }
  },
  webpack: (config) => {
    config.resolve.fallback = { fs: false, tls: false };
    return config;
  },
};

module.exports = nextConfig;
