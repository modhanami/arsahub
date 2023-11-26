import Link from "next/link";
// import { Post } from "@prisma/client";

import { formatDate } from "@/lib/utils";
import { Skeleton } from "@/components/ui/skeleton";
import { Activity } from "../app/dashboard/page";
// import { PostOperations } from "@/components/post-operations"

interface ActivityItemProps {
  activity: Activity;
}

export function ActivityItem({ activity }: ActivityItemProps) {
  return (
    <div className="flex items-center justify-between px-4 py-8">
      <div className="grid gap-1">
        <Link
          href={`/activity/${activity.id}`}
          className="font-semibold text-xl hover:underline"
        >
          {activity.title}
        </Link>
        <div>
          <p className="text-sm text-muted-foreground">
            {activity.description}
          </p>
        </div>
      </div>
      {/* <PostOperations post={{ id: post.id, title: post.title }} /> */}
    </div>
  );
}

ActivityItem.Skeleton = function ActivityItemSkeleton() {
  return (
    <div className="p-4">
      <div className="space-y-3">
        <Skeleton className="h-5 w-2/5" />
        <Skeleton className="h-4 w-4/5" />
      </div>
    </div>
  );
};
