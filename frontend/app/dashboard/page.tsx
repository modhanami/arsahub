// import { authOptions } from "@/lib/auth"
// import { db } from "@/lib/db"
import { getCurrentUser } from "@/lib/session";
import { EmptyPlaceholder } from "@/components/empty-placeholder";
import { DashboardHeader } from "@/components/header";
import { ActivityCreateButton } from "@/components/activity-create-button";
import { ActivityItem } from "@/components/activity-item";
import { DashboardShell } from "@/components/shell";
import {
  CardWithForm,
  DemoCreateAccount,
} from "../../components/create-activity";
import { toast } from "../../components/ui/use-toast";

export const metadata = {
  title: "Dashboard",
};

export interface Activity {
  id: string;
  title: string;
  description: string;
  members: Member[];
}

interface Member {
  id: string;
  name: string;
}

export default async function DashboardPage() {
  const user = await getCurrentUser();

  if (!user) {
    // redirect(authOptions?.pages?.signIn || "/login")
  }

  const response = await fetch(`http://localhost:8080/api/activities`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    next: {
      tags: ["activities"],
    },
  });

  if (!response?.ok) {
    return toast({
      title: "Something went wrong.",
      description: "Your activity was not created. Please try again.",
      variant: "destructive",
    });
  }

  const activities: Activity[] = await response.json();

  // const activities: Activity[] = [
  // {
  //   id: "1",
  //   title: "Activity 1",
  //   description: "Activity 1 description",
  //   members: [
  //     {
  //       id: "1",
  //       name: "User 1",
  //     },
  //     {
  //       id: "2",
  //       name: "User 2",
  //     },
  //   ],
  // },
  // {
  //   id: "2",
  //   title: "Activity 2",
  //   description: "Activity 2 description",
  //   members: [
  //     {
  //       id: "1",
  //       name: "User 1",
  //     },
  //     {
  //       id: "2",
  //       name: "User 2",
  //     },
  //   ],
  // },
  // ];

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Activities"
        text="Create and manage activities."
      >
        <CardWithForm />
      </DashboardHeader>
      <div>
        {activities?.length ? (
          <div className="divide-y divide-border rounded-md border">
            {activities.map((activity) => (
              <ActivityItem key={activity.id} activity={activity} />
            ))}
          </div>
        ) : (
          <EmptyPlaceholder>
            <EmptyPlaceholder.Icon name="activity" />
            <EmptyPlaceholder.Title>
              No activities created
            </EmptyPlaceholder.Title>
            <EmptyPlaceholder.Description>
              You don&apos;t have any activities yet. Start creating content.
            </EmptyPlaceholder.Description>
            <ActivityCreateButton variant="outline" />
          </EmptyPlaceholder>
        )}
      </div>
    </DashboardShell>
  );
}
