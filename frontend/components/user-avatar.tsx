// import { User } from "@prisma/client"
import { AvatarProps } from "@radix-ui/react-avatar";

import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { ArsahubUser } from "@/types";

interface UserAvatarProps extends AvatarProps {
  user: ArsahubUser;
}

export function UserAvatar({ user, ...props }: UserAvatarProps) {
  return (
    <Avatar {...props}>
      <AvatarImage
        alt={`${user.id} avatar`}
        src={`https://avatar.vercel.sh/${user.id}.png`}
      />
      {/* <AvatarFallback>
          <Icons.user className="h-4 w-4" />
        </AvatarFallback> */}
    </Avatar>
  );
}
