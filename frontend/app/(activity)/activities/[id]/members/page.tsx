"use client";
import { useRouter } from "next/navigation";
import { DashboardHeader } from "../../../../../components/header";
import { DashboardShell } from "../../../../../components/shell";
import { toast } from "../../../../../components/ui/use-toast";
import { useMembers } from "../../../../../hooks/api";
import { ContextProps } from "../../../../../types";
import { MemberResponse } from "../../../../../types/generated-types";

export type Props = {
  searchParams: {
    userId?: string;
  };
} & ContextProps;

export default function Page({ params, searchParams }: Props) {
  const members = [
    ...useMembers(Number(params.id)),
    ...Array.from({ length: 20 }).map<MemberResponse>((_, i) => {
      const id = 1_000_000 + i;
      return {
        memberId: id,
        name: `NOT USER ${id}`,
        username: `NOT USER ${id}`,
        points: 0,
        userId: id,
      };
    }),
  ];
  const router = useRouter();
  console.log(searchParams);

  function handleClick(member: MemberResponse) {
    console.log(member);

    router.push(`/activities/${params.id}/members?userId=${member.userId}`);

    toast({
      title: `User ${member.username} selected.`,
      description: "You can now view their profile.",
    });
  }

  return (
    <DashboardShell>
      <DashboardHeader heading="Members"></DashboardHeader>
      <div className="flex gap-4">
        <div className="divide-y divide-border rounded-md border w-1/2 p-4 overflow-y-auto">
          {members.map((member) => (
            <div
              className="items-center justify-between p-4 cursor-pointer"
              key={member.memberId}
              onClick={() => handleClick(member)}
            >
              <div className="grid gap-1 max-w-lg">{member.name}</div>
            </div>
          ))}
        </div>
        <div className="w-1/2">
          {searchParams.userId && (
            <iframe
              src={`/embed/activities/${params.id}/profile?userId=${searchParams.userId}`}
              width="100%"
              height="100%"
              allowFullScreen={true}
              className="overflow-hidden border-none sticky top-0"
            />
          )}
        </div>
      </div>
    </DashboardShell>
  );
}
