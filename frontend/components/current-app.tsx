"use client";
import React, { useState } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "../components/ui/avatar";
import { Input } from "../components/ui/input";
import { getCurrentApp, useCurrentApp } from "../lib/current-app";
import { Button } from "./ui/button";

export function CurrentAppForm() {
  const { currentApp, setCurrentAppWithApiKey } = useCurrentApp();

  const [key, setToken] = useState(getCurrentApp().apiKey || "");
  const [previousKey, setPreviousKey] = useState(key);
  const [isEditing, setIsEditing] = useState(false);

  const handleTokenChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setToken(event.target.value);
  };

  const handleSaveToken = () => {
    setCurrentAppWithApiKey(key);
    setIsEditing(false);
    setPreviousKey(key);
  };

  const handleCancelEdit = () => {
    setToken(previousKey);
    setIsEditing(false);
  };

  return (
    <div className="gap-4 flex items-center">
      {isEditing ? (
        <>
          <Input
            value={key}
            onChange={handleTokenChange}
            className="max-w-sm"
            placeholder="Enter your app's API key"
            type="password"
          />
          <div className="flex gap-2">
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
            {currentApp?.name || "No app selected"}
          </div>
          <Button onClick={() => setIsEditing(true)}>Edit</Button>
        </>
      )}
    </div>
  );
}
