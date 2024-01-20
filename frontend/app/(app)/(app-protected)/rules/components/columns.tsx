"use client";

import { ColumnDef } from "@tanstack/react-table";

import { RuleResponse } from "@/types/generated-types";
import dayjs from "dayjs";
import RelativeTime from "dayjs/plugin/relativeTime";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";

export const columns: ColumnDef<RuleResponse>[] = [
  {
    accessorKey: "title",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Title" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate font-medium">
            {row.getValue("title")}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "description",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Description" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate font-medium">
            {row.getValue("description")}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "trigger.title",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Trigger" />
    ),
    cell: ({ row }) => {
      const conditions = row.original.conditions || {};
      const formattedConditions = Object.entries(conditions)
        .map(([key, value]) => {
          return `${key} = ${value}`;
        })
        .join(", ");

      return (
        <div className="flex flex-col space-y-2">
          <span className="max-w-[500px] truncate">
            {row.original.trigger?.title}
          </span>
          {formattedConditions.length > 0 ? (
            <span className="max-w-[500px] truncate font-medium text-muted-foreground">
              {formattedConditions}
            </span>
          ) : null}
        </div>
      );
    },
  },
  {
    accessorKey: "action.title",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Action" />
    ),
    cell: ({ row }) => {
      let actionLabel;
      let actionParam;
      let actionSuffix;
      const isAddPoints = row.original.action === "add_points";
      const isUnlockAchievement = row.original.action === "unlock_achievement";

      if (isAddPoints) {
        actionLabel = "Add";
        actionParam = row.original.actionPoints;
        actionSuffix = `point${row.original.actionPoints ?? 0 > 1 ? "s" : ""}`;
      }
      if (isUnlockAchievement) {
        actionLabel = "Unlock";
        actionParam = row.original.actionAchievement?.title;
      }

      return (
        <div className="flex space-x-1 max-w-[500px] truncate">
          <span>{actionLabel}</span>
          <span className="text-muted-foreground font-medium">
            {actionParam}
          </span>
          <span>{actionSuffix}</span>
        </div>
      );
    },
  },
  {
    accessorKey: "createdAt",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Created At" />
    ),
    cell: ({ row }) => {
      dayjs.extend(RelativeTime);
      const createdAt = dayjs(row.getValue("createdAt"));
      const formatted = createdAt.format("YYYY-MM-DD HH:mm:ss");
      const relative = createdAt.fromNow();
      return (
        <div className="text-right " title={formatted}>
          {relative}
        </div>
      );
    },
  },
  // {
  //   id: "actions",
  //   cell: ({ row }) => <DataTableRowActions row={row} />,
  // },
];
