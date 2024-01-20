"use client";

import { ColumnDef } from "@tanstack/react-table";

import { TriggerResponse } from "@/types/generated-types";
import dayjs from "dayjs";
import RelativeTime from "dayjs/plugin/relativeTime";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";

export const columns: ColumnDef<TriggerResponse>[] = [
  // {
  //   id: "select",
  //   header: ({ table }) => (
  //     <Checkbox
  //       checked={
  //         table.getIsAllPageRowsSelected() ||
  //         (table.getIsSomePageRowsSelected() && "indeterminate")
  //       }
  //       onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
  //       aria-label="Select all"
  //       className="translate-y-[2px]"
  //     />
  //   ),
  //   cell: ({ row }) => (
  //     <Checkbox
  //       checked={row.getIsSelected()}
  //       onCheckedChange={(value) => row.toggleSelected(!!value)}
  //       aria-label="Select row"
  //       className="translate-y-[2px]"
  //     />
  //   ),
  //   enableSorting: false,
  //   enableHiding: false,
  // },
  {
    accessorKey: "key",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Key" />
    ),
    cell: ({ row }) => <div className="w-[80px]">{row.getValue("key")}</div>,
    // enableSorting: false,
    // enableHiding: false,
  },
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
        <div className="text-right font-medium" title={formatted}>
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
