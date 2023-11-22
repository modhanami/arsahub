import Link from "next/link";

import { Skeleton } from "@/components/ui/skeleton";
import { Rule } from "../app/activity/[id]/rules/page";

interface RuleItemProps {
  rule: Rule;
}

export function RuleItem({ rule: rule }: RuleItemProps) {
  return (
    <div className="flex items-center justify-between p-4 ">
      <div className="grid gap-1 max-w-lg">
        <Link href={`/`} className="font-semibold hover:underline break-words ">
          {rule.title}
        </Link>
        <p>
          <p className="text-sm  text-muted-foreground break-words">
            {rule.description}
          </p>
        </p>
      </div>
    </div>
  );
}

RuleItem.Skeleton = function RuleItemSkeleton() {
  return (
    <div className="p-4">
      <div className="space-y-3">
        <Skeleton className="h-5 w-2/5" />
        <Skeleton className="h-4 w-4/5" />
      </div>
    </div>
  );
};
