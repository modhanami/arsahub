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

export default function Page() {
  const [startTime, _setStartTime] = useState<Dayjs | null>(
    dayjs().subtract(1, "month"),
  );
  const [endTime, _setEndTime] = useState<Dayjs | null>(dayjs());

  const setStartTime = useDebounceCallback(_setStartTime, 500);
  const setEndTime = useDebounceCallback(_setEndTime, 500);

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

  return (
    <div>
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
      />
      <Card className="mx-auto max-w-md">
        <h4 className="text-tremor-default text-tremor-content dark:text-dark-tremor-content">
          Total Achievement Unlocks
        </h4>
        <p className="text-tremor-metric font-semibold text-tremor-content-strong dark:text-dark-tremor-content-strong">
          {totalUnlockedAchievements.data}
        </p>
      </Card>

      <Card>
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
                    <span className="truncate" title={item.achievement.title}>
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
  );
}
