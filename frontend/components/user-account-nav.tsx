"use client";

// import { signOut } from "next-auth/react"

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { UserAvatar } from "@/components/user-avatar";
import { useRouter } from "next/navigation";
import { useCurrentUser } from "../lib/current-user";
import { Button } from "./ui/button";

export function UserAccountNav() {
  const { currentUser, logoutCurrentUser } = useCurrentUser();
  const router = useRouter();

  if (!currentUser) {
    return (
      <Button
        variant="outline"
        className="text-sm"
        onClick={() => router.push("/login")}
      >
        Login
      </Button>
    );
  }

  function handleLogout() {
    logoutCurrentUser();
    router.push("/login");
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger>
        <UserAvatar user={currentUser} className="h-8 w-8" />
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <div className="flex items-center justify-start gap-2 p-2">
          <div className="flex flex-col space-y-1 leading-none">
            {currentUser?.name && (
              <p className="font-medium">{currentUser.name}</p>
            )}
            {/* {user.name && (
              <p className="w-[200px] truncate text-sm text-muted-foreground">
                {user.name}
              </p>
            )} */}
          </div>
        </div>
        {/* <DropdownMenuSeparator /> */}
        {/* <DropdownMenuItem asChild>
          <Link href="/dashboard">Dashboard</Link>
        </DropdownMenuItem>
        <DropdownMenuItem asChild>
          <Link href="/dashboard/billing">Billing</Link>
        </DropdownMenuItem>
        <DropdownMenuItem asChild>
          <Link href="/dashboard/settings">Settings</Link>
        </DropdownMenuItem> */}
        <DropdownMenuSeparator />
        <DropdownMenuItem
          className="cursor-pointer"
          onSelect={() => handleLogout()}
        >
          Logout
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
