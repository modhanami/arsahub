/** @type {import('next').NextConfig} */
const nextConfig = {
    async redirects() {
        return [
            {
                source: '/integrations/:id',
                destination: '/integrations/:id/dashboard', // Redirect to /dashboard
                permanent: true, // if you want it to be a 301 redirect
            },
        ]
    },
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
