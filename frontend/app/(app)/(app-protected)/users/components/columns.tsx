"use client";

import { ColumnDef } from "@tanstack/react-table";

import { AppUserResponse } from "@/types/generated-types";
import dayjs from "dayjs";
import RelativeTime from "dayjs/plugin/relativeTime";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";

export const columns: ColumnDef<AppUserResponse>[] = [
  {
    accessorKey: "userId",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="User ID" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate">
            {row.getValue("userId")}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "displayName",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Display Name" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate">
            {row.getValue("displayName")}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "points",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Points" />
    ),
    cell: ({ row }) => {
      return (
        <div className="text-right max-w-[100px]">{row.getValue("points")}</div>
      );
    },
  },
  {
    accessorKey: "achievements",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="# Achievements" />
    ),
    cell: ({ row }) => {
      return (
        <div className="text-right max-w-[100px]">
          {row.original.achievements?.length}
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
        <div className="text-right" title={formatted}>
          {relative}
        </div>
      );
    },
  },
];
