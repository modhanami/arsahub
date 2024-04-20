"use client";
import { BarList, Card } from "@tremor/react";
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
          <Card>
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
          <Card>
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
          <Card>
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

        <div className="flex gap-4 overflow-y-auto">
          <Card>
            <ErrorMessage error={top10Achievements.error} />

            <h3 className="text-tremor-content-strong dark:text-dark-tremor-content-strong font-semibold mb-4">
              Top 10 Achievements Unlocked
            </h3>

            {/*  bar list with images */}
            {top10Achievements.data?.length ?? 0 > 0 ? (
              <BarList
                data={(top10Achievements.data || []).map((item) => ({
                  name: `${item.achievement.title!}`,
                  value: item.count,
                  icon: () => (
                    <Image
                      src={getImageUrlFromKey(item.achievement.imageKey!)}
                      width={32}
                      alt={`achievement image ${item.achievement.title}`}
                      radius="none"
                      classNames={{
                        wrapper: "mr-4 flex items-center min-w-[32px]",
                      }}
                    />
                  ),
                }))}
                sortOrder="descending"
                className="mx-auto max-w-sm"
              />
            ) : (
              <div className="text-center text-tremor-content dark:text-dark-tremor-content">
                No data available
              </div>
            )}
          </Card>
          {/*  Top 10 triggers */}
          <Card>
            <ErrorMessage error={top10Triggers.error} />

            <h3 className="text-tremor-content-strong dark:text-dark-tremor-content-strong font-semibold mb-4">
              Top 10 Triggers Used
            </h3>
            {top10Triggers.data?.length ?? 0 > 0 ? (
              <BarList
                data={(top10Triggers.data || []).map((item) => ({
                  name: item.trigger.title!,
                  value: item.count,
                }))}
                sortOrder="descending"
                className="mx-auto max-w-sm"
              />
            ) : (
              <div className="text-center text-tremor-content dark:text-dark-tremor-content">
                No data available
              </div>
            )}
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
