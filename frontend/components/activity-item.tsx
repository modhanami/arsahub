import Link from "next/link";

import { Skeleton } from "@/components/ui/skeleton";
import { ActivityResponse } from "../types/generated-types";

interface ActivityItemProps {
  activity: ActivityResponse;
}

export function ActivityItem({ activity }: ActivityItemProps) {
  return (
    <div className="flex items-center justify-between p-4">
      <div className="grid gap-1">
        <Link
          href={`/activities/${activity.id}`}
          className="font-semibold hover:underline"
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
