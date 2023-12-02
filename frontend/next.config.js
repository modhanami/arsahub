/** @type {import('next').NextConfig} */
const nextConfig = {
    async redirects() {
        return [
            {
                source: '/apps/:id',
                destination: '/apps/:id/dashboard', // Redirect to /dashboard
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
