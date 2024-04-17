"use client";
import {
  Card,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeaderCell,
  TableRow,
} from "@tremor/react";
import {
  AchievementWithUnlockCountResponse,
  AnalyticsConstants,
  TriggerWithTriggerCountResponse,
} from "@/types/generated-types";
import { useAnalytics } from "@/hooks";
import React, { useState } from "react";
import dayjs, { Dayjs } from "dayjs";
import { DateTimePicker } from "@mui/x-date-pickers";
import { getImageUrlFromKey } from "@/lib/image";
import { Image } from "@nextui-org/react";
import { useDebounceCallback } from "usehooks-ts";
import { Icons } from "@/components/icons";
import { DashboardHeader } from "@/components/header";
import { DashboardShell } from "@/components/shell";
import { useIsFetching } from "@tanstack/react-query";
import { isApiError } from "@/api";

const numberFormatter = new Intl.NumberFormat("en-US");

export default function Page() {
  const [startTime, _setStartTime] = useState<Dayjs | null>(
    dayjs().subtract(1, "month"),
  );
  const [endTime, _setEndTime] = useState<Dayjs | null>(dayjs());

  const setStartTime = useDebounceCallback((v) => {
    if (v.isAfter(endTime)) return;
    _setStartTime(v);
  }, 500);

  const setEndTime = useDebounceCallback((v) => {
    if (v.isBefore(startTime)) return;
    _setEndTime(v);
  }, 500);

  const totalUnlockedAchievements = useAnalytics<number>({
    type: AnalyticsConstants.TOTAL_UNLOCKED_ACHIEVEMENTS,
    start: startTime?.toDate(),
    end: endTime?.toDate(),
  });

  const top10Achievements = useAnalytics<AchievementWithUnlockCountResponse[]>({
    type: AnalyticsConstants.TOP_10_ACHIEVEMENTS,
    start: startTime?.toDate(),
    end: endTime?.toDate(),
  });

  const top10Triggers = useAnalytics<TriggerWithTriggerCountResponse[]>({
    type: AnalyticsConstants.TOP_10_TRIGGERS,
    start: startTime?.toDate(),
    end: endTime?.toDate(),
  });

  const totalAppUsers = useAnalytics<number>({
    type: AnalyticsConstants.TOTAL_APP_USERS,
    start: startTime?.toDate(),
    end: endTime?.toDate(),
  });

  const totalPointsEarned = useAnalytics<number>({
    type: AnalyticsConstants.TOTAL_POINTS_EARNED,
    start: startTime?.toDate(),
    end: endTime?.toDate(),
  });

  const isSomeQueryLoading = !!useIsFetching({
    queryKey: ["analytics"],
  });

  return (
    <DashboardShell>
      <DashboardHeader heading="Analytics" text="View your statistics here.">
        <div className="flex gap-2 items-center">
          {isSomeQueryLoading && (
            <Icons.spinner className="mr-4 h-6 w-6 animate-spin" />
          )}

          <DateTimePicker
            label="Start"
            slotProps={{
              textField: {
                variant: "filled",
                required: true,
                size: "small",
                margin: "dense",
              },
            }}
            value={startTime}
            onChange={setStartTime}
            maxDate={endTime}
          />
          <DateTimePicker
            label="End"
            slotProps={{
              textField: {
                variant: "filled",
                required: true,
                size: "small",
                margin: "dense",
              },
            }}
            value={endTime}
            onChange={setEndTime}
            minDate={startTime}
          />
        </div>
      </DashboardHeader>

      <div>
        <div className="sm:flex flex-row gap-4 my-4">
          <Card className="mx-auto max-w-md">
            <ErrorMessage error={totalPointsEarned.error} />

            <h4 className="text-tremor-default text-tremor-content dark:text-dark-tremor-content">
              Total Points Earned
            </h4>
            <p className="text-tremor-metric font-semibold text-tremor-content-strong dark:text-dark-tremor-content-strong">
              {totalPointsEarned.data
                ? numberFormatter.format(totalPointsEarned.data)
                : 0}
            </p>
          </Card>
          <Card className="mx-auto max-w-md">
            <ErrorMessage error={totalUnlockedAchievements.error} />

            <h4 className="text-tremor-default text-tremor-content dark:text-dark-tremor-content">
              Total Achievement Unlocks
            </h4>
            <p className="text-tremor-metric font-semibold text-tremor-content-strong dark:text-dark-tremor-content-strong">
              {totalUnlockedAchievements.data
                ? numberFormatter.format(totalUnlockedAchievements.data)
                : 0}
            </p>
          </Card>
          <Card className="mx-auto max-w-md">
            <ErrorMessage error={totalAppUsers.error} />

            <h4 className="text-tremor-default text-tremor-content dark:text-dark-tremor-content">
              Total App Users
            </h4>
            <p className="text-tremor-metric font-semibold text-tremor-content-strong dark:text-dark-tremor-content-strong">
              {totalAppUsers.data
                ? numberFormatter.format(totalAppUsers.data)
                : 0}
            </p>
          </Card>
        </div>

        <div className="flex flex-col gap-4">
          <Card>
            <ErrorMessage error={top10Achievements.error} />

            <h3 className="text-tremor-content-strong dark:text-dark-tremor-content-strong font-semibold">
              Top 10 Achievements
            </h3>
            <Table className="mt-5">
              <TableHead>
                <TableRow>
                  <TableHeaderCell>ID</TableHeaderCell>
                  <TableHeaderCell>Name</TableHeaderCell>
                  <TableHeaderCell>Unlocks</TableHeaderCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {top10Achievements.data?.map((item) => (
                  <TableRow key={item.achievement.achievementId}>
                    <TableCell>{item.achievement.achievementId}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2 truncate w-[300px]">
                        {item.achievement.imageKey && (
                          <Image
                            src={getImageUrlFromKey(item.achievement.imageKey)}
                            width={64}
                            height={64}
                            alt={`achievement image ${item.achievement.title}`}
                            radius="none"
                          />
                        )}
                        <span
                          className="truncate"
                          title={item.achievement.title}
                        >
                          {item.achievement.title}
                        </span>
                      </div>
                    </TableCell>
                    <TableCell>{item.count}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>
          {/*  Top 10 triggers */}
          <Card>
            <ErrorMessage error={top10Triggers.error} />

            <h3 className="text-tremor-content-strong dark:text-dark-tremor-content-strong font-semibold">
              Top 10 Triggers
            </h3>
            <Table className="mt-5">
              <TableHead>
                <TableRow>
                  <TableHeaderCell>ID</TableHeaderCell>
                  <TableHeaderCell>Name</TableHeaderCell>
                  <TableHeaderCell>Triggers</TableHeaderCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {top10Triggers.data?.map((item) => (
                  <TableRow key={item.trigger.id}>
                    <TableCell>{item.trigger.id}</TableCell>
                    <TableCell>{item.trigger.title}</TableCell>
                    <TableCell>{item.count}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>
        </div>
      </div>
    </DashboardShell>
  );
}

function ErrorMessage({ error }: { error: Error | null }) {
  if (!error) return null;

  const message = isApiError(error)
    ? error.response?.data.message
    : error.message;

  return (
    <div className="text-red-500 text-sm py-1 gap-1 flex items-center">
      <Icons.warning className="h-4 w-4 inline-block" />
      {message}
    </div>
  );
}
