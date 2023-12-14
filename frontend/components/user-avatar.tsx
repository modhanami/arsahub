// import { User } from "@prisma/client"
import { AvatarProps } from "@radix-ui/react-avatar";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Icons } from "@/components/icons";
import { User } from "lucide-react";
import { UserResponse } from "../types/generated-types";

interface UserAvatarProps extends AvatarProps {
  user: UserResponse;
}

export function UserAvatar({ user, ...props }: UserAvatarProps) {
  return (
    <Avatar {...props}>
      <AvatarImage
        alt={`${user.name} avatar`}
        src={`https://avatar.vercel.sh/${user.name}.png`}
      />
      {/* <AvatarFallback>
          <Icons.user className="h-4 w-4" />
        </AvatarFallback> */}
    </Avatar>
  );
}
