/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        remotePatterns: [
            {
                hostname: 'cdn.7tv.app',
            }
        ],
    },
    typescript: {
        ignoreBuildErrors: true, // TODO: Remove this when all errors are fixed
    }
}

module.exports = nextConfig
