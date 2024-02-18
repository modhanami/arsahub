"use client";

import { ColumnDef } from "@tanstack/react-table";

import { RuleResponse } from "@/types/generated-types";
import dayjs from "dayjs";
import RelativeTime from "dayjs/plugin/relativeTime";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";
import { DataTableRowActionsProps } from "@/app/(app)/examples/tasks/components/data-table-row-actions";
import React from "react";
import { useDeleteRule } from "@/hooks";
import { isApiError } from "@/api";
import { toast } from "@/components/ui/use-toast";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { DotsHorizontalIcon } from "@radix-ui/react-icons";
import { Icons } from "@/components/icons";
import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";

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
  {
    id: "actions",
    cell: ({ row }) => <RuleRowActions row={row} />,
  },
];

export function RuleRowActions({
  row,
}: DataTableRowActionsProps<RuleResponse>) {
  const [showEditDialog, setShowEditDialog] = React.useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = React.useState(false);
  const deleteRule = useDeleteRule();

  async function handleDelete() {
    try {
      await deleteRule.mutateAsync(row.original.id!);
    } catch (error) {
      if (isApiError(error)) {
        toast({
          description:
            "Failed to delete rule: " + error.response?.data.message ||
            error.message,
          variant: "destructive",
        });
        return;
      }
    }

    setShowDeleteDialog(false);
    toast({
      description: `Rule '${row.original.title}' has been deleted.`,
    });
  }

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button
            variant="ghost"
            className="flex h-8 w-8 p-0 data-[state=open]:bg-muted"
          >
            <DotsHorizontalIcon className="h-4 w-4" />
            <span className="sr-only">Open menu</span>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-[160px]">
          <DropdownMenuItem onSelect={() => setShowEditDialog(true)}>
            <Icons.edit className="mr-3 h-4 w-4" />
            Edit
          </DropdownMenuItem>
          <DropdownMenuItem
            onSelect={() => setShowDeleteDialog(true)}
            className="text-destructive"
          >
            <Icons.trash className="mr-3 h-4 w-4" />
            Delete
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      {/*<Dialog open={showEditDialog} onOpenChange={setShowEditDialog}></Dialog>*/}

      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This rule &apos;
              {row.original.title}&apos; will not be recoverable.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <Button variant="destructive" onClick={handleDelete}>
              Delete
            </Button>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}
