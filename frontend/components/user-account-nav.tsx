"use client";

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { UserAvatar } from "@/components/user-avatar";
import { Button } from "./ui/button";
import { useCurrentUser } from "@/lib/current-user";

export function UserAccountNav() {
  const { currentUser, isLoading, startLoginFlow, startLogoutFlow } =
    useCurrentUser();

  if (isLoading || !currentUser) {
    return (
      <Button
        variant="outline"
        className="text-sm"
        onClick={() => startLoginFlow({ returnTo: window.location.href })}
      >
        Login
      </Button>
    );
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger>
        <UserAvatar user={currentUser} className="h-8 w-8" />
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <div className="flex items-center justify-start gap-2 p-2">
          <div className="flex flex-col space-y-1 leading-none">
            {currentUser && (
              <div className="gap-2 flex flex-col">
                <p className="font-medium">{currentUser.name}</p>
                <p className="w-[200px] truncate text-sm text-muted-foreground">
                  {currentUser.email}
                </p>
              </div>
            )}
          </div>
        </div>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          className="cursor-pointer"
          onClick={() =>
            startLogoutFlow({
              returnTo: window.location.href,
            })
          }
        >
          Logout
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
