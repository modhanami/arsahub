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
    }
}

module.exports = nextConfig
