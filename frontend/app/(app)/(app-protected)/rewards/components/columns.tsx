"use client";

import { ColumnDef } from "@tanstack/react-table";

import { RewardResponse } from "@/types/generated-types";
import dayjs from "dayjs";
import RelativeTime from "dayjs/plugin/relativeTime";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";
import { Image } from "@nextui-org/react";
import { getImageUrlFromKey } from "@/lib/image";

export const columns: ColumnDef<RewardResponse>[] = [
  {
    accessorKey: "name",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Name" />
    ),
    cell: ({ row }) => {
      const reward = row.original;

      return (
        <div className="flex space-x-2 items-center gap-2">
          {reward.imageKey && (
            <Image
              src={getImageUrlFromKey(reward.imageKey)}
              width={48}
              alt={`achievement image ${reward.name}`}
            />
          )}
          <span className="max-w-[500px] truncate font-medium">
            {row.getValue("name")}
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
            {row.getValue("description")
              ? row.getValue("description")
              : "No Description."}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "price",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Price" />
    ),
    cell: ({ row }) => {
      const amount = parseFloat(row.getValue("price"));
      const formatted = new Intl.NumberFormat("en-US").format(amount);

      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate font-medium">
            {formatted}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "quantity",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Quantity" />
    ),
    cell: ({ row }) => {
      const amount = parseFloat(row.getValue("quantity"));
      const formatted = Number.isNaN(amount)
        ? "Unlimited"
        : new Intl.NumberFormat("en-US").format(amount);

      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate font-medium">
            {formatted}
          </span>
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
      return <div title={formatted}>{relative}</div>;
    },
  },
  // {
  //   id: "actions",
  //   cell: ({ row }) => <DataTableRowActions row={row} />,
  // },
];
