"use client";
import { useState, useEffect } from "react";
import { io } from "socket.io-client";

const socket = io("http://localhost:9097/default");
export function UserPoints() {
  const [userPoints, setUserPoints] = useState({});
  const [leaderboard, setLeaderboard] = useState({});

  useEffect(() => {
    socket.emit("subscribe-activity", 1, (data) => {
      console.log("subscribe-activity", data);
    });

    socket.on("activity-update", (data) => {
      console.log("activity-update", data);
      if (data.type === "points-update") {
        setUserPoints(data.data);
      }
      if (data.type === "leaderboard-update") {
        setLeaderboard(data.data.leaderboard);
      }
    });
  }, []);

  const a = leaderboard?.entries?.map(({ rank, memberId, score }) => (
    <li key={memberId}>
      Rank: {rank} Member ID: {memberId} Score: {score}
    </li>
  ));

  return (
    <div>
      <div>User ID: {userPoints.userId}</div>
      <div>Points {userPoints.points}</div>
      <div>Leaderboard: {leaderboard.leaderboard}</div>
      <ol>{a}</ol>
    </div>
  );
}
