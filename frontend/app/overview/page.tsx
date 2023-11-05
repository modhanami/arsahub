"use client";
import { useAutoAnimate } from "@formkit/auto-animate/react";
import { useState, useEffect } from "react";

import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

export default function App() {
  return (
    <div className="App">
      <MyList />
    </div>
  );
}

function MyList() {
  const [animationLeaderboard] = useAutoAnimate();

  const [user, setUser] = useState([
    { name: "Alice", emoji: "👧", score: 0 },
    { name: "Bob", emoji: "🧑", score: 0 },
    { name: "Charlie", emoji: "👳", score: 0 },
    { name: "David", emoji: "👦", score: 0 },
    { name: "Elizabeth", emoji: "👧", score: 0 },
  ]);

  useEffect(() => {
    const updateLeaderboard = () => {
      // Sort leaderboard entries by score
      const sortedLeaderboard = [...user].sort((a, b) => b.score - a.score);

      // Update scores randomly
      const updatedLeaderboard = sortedLeaderboard.map((entry) => ({
        ...entry,
        score: entry.score + Math.floor(Math.random() * 100),
      }));

      // Update the state with the new leaderboard
      setUser(updatedLeaderboard);
    };

    const interval = setInterval(updateLeaderboard, 800); // Update every 0.8 seconds

    return () => clearInterval(interval); // Clean up the interval on unmount
  }, [user]);

  // Add rank column
  const userWithRank = user.map((user, index) => ({
    ...user,
    rank: index + 1,
  }));

  return (
    <article className="container mx-auto px-4 sm:px-8">
      <div className="py-8">
        <p className="text-2xl font-semibold mb-4 text-center">
          Leaderboard [Animate list re-ordering]
        </p>
        <div className="-mx-4 sm:-mx-8 px-4 sm:px-8 py-4 overflow-x-auto">
          <div className="inline-block min-w-full shadow-md rounded-lg overflow-hidden">
            <Table className="min-w-full leading-normal">
              <TableHeader>
                <TableRow>
                  <TableHead className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Rank
                  </TableHead>
                  <TableHead className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Name
                  </TableHead>
                  <TableHead className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Score
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody ref={animationLeaderboard}>
                {userWithRank.map((user) => (
                  <TableRow key={user.name} className="border-t">
                    <TableCell className="px-6 py-4">{user.rank}</TableCell>
                    <TableCell className="px-6 py-4">
                      {user.emoji} {user.name}
                    </TableCell>
                    <TableCell className="px-6 py-4">{user.score}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
    </article>
  );
}
