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
};

module.exports = nextConfig;
