import { useEffect, useState } from "react";
import { io, Socket } from "socket.io-client";

export const SOCKET_IO_URL =
  process.env.NEXT_PUBLIC_SOCKET_IO_URL || "http://localhost:9097";

export function useSocket() {
  const [socket, setSocket] = useState<Socket | null>(null);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const socket = io(`${SOCKET_IO_URL}/default`, {
      // forceNew: true,
      timestampRequests: true,
    });

    setSocket(socket);

    async function onConnect() {
      setIsConnected(true);
    }

    function onDisconnect() {
      setIsConnected(false);
    }

    socket.on("connect", onConnect);
    socket.on("disconnect", onDisconnect);
    socket.connect();

    return () => {
      socket.off("connect", onConnect);
      socket.off("disconnect", onDisconnect);
      socket.disconnect();
    };
  }, []);

  return {
    socket,
    isConnected,
  };
}
