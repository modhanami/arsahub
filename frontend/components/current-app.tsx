"use client";
import React, { useState } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "../components/ui/avatar";
import { Input } from "../components/ui/input";
import { Button } from "./ui/button";
import { useAppApiKey, useCurrentApp } from "../lib/current-app";
import Link from "next/link";

export function CurrentAppForm() {
  const { currentApp } = useCurrentApp();
  const { updateApiKey } = useAppApiKey();

  const [key, setToken] = useState(currentApp?.apiKey || "");
  const [previousKey, setPreviousKey] = useState(key);
  const [isEditing, setIsEditing] = useState(false);

  const handleTokenChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setToken(event.target.value);
  };

  const handleSaveToken = () => {
    updateApiKey(key);
    setIsEditing(false);
    setPreviousKey(key);
  };

  const handleCancelEdit = () => {
    setToken(previousKey);
    setIsEditing(false);
  };

  return (
    <div className="gap-4 grid">
      {isEditing ? (
        <>
          <div>
            <Input
              value={key}
              onChange={handleTokenChange}
              className="max-w-sm"
              placeholder="Enter your app's API key"
              type="password"
            />
            <Link
              href={{
                pathname: "/secrets",
              }}
              className="text-xs text-blue-500 hover:underline"
            >
              Get your API key
            </Link>
          </div>
          <div className="grid grid-cols-2 gap-2">
            <Button onClick={handleSaveToken}>Save</Button>
            <Button onClick={handleCancelEdit} variant="outline">
              Cancel
            </Button>
          </div>
        </>
      ) : (
        <>
          <div className="flex gap-2 items-center">
            {currentApp?.name && (
              <Avatar className="h-5 w-5">
                <AvatarImage
                  src={`https://avatar.vercel.sh/${currentApp?.name}.png`}
                  alt={currentApp?.name}
                />
                <AvatarFallback>AS</AvatarFallback>
              </Avatar>
            )}
            {currentApp?.name || null}
          </div>
          <Button onClick={() => setIsEditing(true)} variant="outline">
            Set App API Key
          </Button>
        </>
      )}
    </div>
  );
}
