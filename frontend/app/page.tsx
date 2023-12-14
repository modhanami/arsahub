import { ArrowRight, Crown } from "lucide-react";
import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function Home() {
  return (
    <div className="max-w-screen-lg mx-auto px-3 pt-20 pb-32">
      <header className="text-center">
        <h1 className="text-6xl font-bold whitespace-pre-line leading-hero">
          Gamification <br />
          <span className="text-blue-500">Platform</span>
        </h1>
        <div className="text-2xl mt-8 mb-16">
          Design, develop, and control your own Gamification strategies.
        </div>
        <div className="mt-8 mb-16">
          <Link href="/" className="px-2">
            <Button>
              Get Started <ArrowRight />
            </Button>
          </Link>
          <Link href="/leaderboard" className="px-2">
            <Button>
              Leaderboard <Crown />
            </Button>
          </Link>
        </div>
      </header>
    </div>
  );
}
