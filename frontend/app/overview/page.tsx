"use client";
import { ActivityCreateButton } from '@/components/activity-create-button';
import { ActivityItem } from '@/components/activity-item';
import { CardWithForm } from '@/components/create-activity';
import { EmptyPlaceholder } from '@/components/empty-placeholder';
import { DashboardHeader } from '@/components/header';
import { DashboardShell } from '@/components/shell';
import React, { useState, useEffect } from 'react';

type Props = {};

export default function Overview({}: Props) {
  const [activities, setActivities] = useState([]);

  const fetchData = async () => {
    const url = "http://localhost:8080/api/activities";
    const response = await fetch(url);
    const data = await response.json();
    setActivities(data);
  };

  useEffect(() => {
    fetchData();

    // fetch data every 5 seconds
    const intervalId = setInterval(() => {
      fetchData();
    }, 5000);

    return () => clearInterval(intervalId);
  }, []);

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
          {activities.map((activity: any) => (
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
