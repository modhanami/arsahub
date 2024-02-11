/** @type {import('next').NextConfig} */
const nextConfig = {
  async redirects() {
    return [
      {
        source: "/",
        destination: "/overview",
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
    return [
      {
        source: "/api/:path*",
        destination: "http://localhost:8080/api/:path*", // Proxy to Backend
      },
    ];
  },
};

module.exports = nextConfig;
