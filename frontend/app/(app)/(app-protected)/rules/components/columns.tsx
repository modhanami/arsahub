"use client";

import { ColumnDef } from "@tanstack/react-table";

import { RuleResponse } from "@/types/generated-types";
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
import { resolveBasePath } from "@/lib/base-path";
import { useRouter } from "next/navigation";
import { KeyText } from "../../triggers/components/columns";
import { cn, numberFormatter } from "@/lib/utils";

export const columns: ColumnDef<RuleResponse>[] = [
  {
    accessorKey: "title",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Title" />
    ),
    cell: ({ row }) => {
      const title = row.getValue("title") as string;
      return (
        <div className="flex flex-col max-w-[500px] gap-2">
          <span className="truncate" title={title}>
            {title}
          </span>

          <span
            className="truncate text-muted-foreground"
            title={row.original.description || undefined}
          >
            {row.original.description}
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
      const formattedConditions = row.original.conditionExpression;

      return (
        <div className="max-w-[300px] flex flex-col space-y-2">
          <span
            className="truncate"
            title={row.original.trigger?.title || undefined}
          >
            {row.original.trigger?.title}
          </span>
          {formattedConditions && (
            <KeyText
              className="text-muted-foreground"
              variant="outline"
              title={formattedConditions}
              text={formattedConditions}
            />
          )}
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
      const isPointsExpression = !!row.original.actionPointsExpression;

      if (isAddPoints) {
        actionLabel = "Add";
        actionParam =
          row.original.actionPoints?.toString() ||
          row.original.actionPointsExpression;
        actionSuffix = row.original.actionPoints === 1 ? "point" : "points";
      }
      if (isUnlockAchievement) {
        actionLabel = "Unlock";
        actionParam = row.original.actionAchievement?.title;
      }

      return (
        <div className="flex max-w-[300px]">
          <span>{actionLabel}</span>
          {/*<span className="text-muted-foreground truncate">{actionParam}</span>*/}
          <KeyText
            className={cn("bg-muted mx-2", {
              "dark:text-amber-300 text-amber-600": isAddPoints,
              "dark:text-green-300 text-green-500": isUnlockAchievement,
              "dark:text-cyan-300 text-cyan-500": isPointsExpression,
            })}
            variant="outline"
            title={actionParam || undefined}
            text={
              isAddPoints && !isPointsExpression
                ? numberFormatter.format(Number(actionParam))
                : actionParam
            }
          />
          <span>{actionSuffix}</span>
        </div>
      );
    },
  },
  // {
  //   accessorKey: "createdAt",
  //   header: ({ column }) => (
  //     <DataTableColumnHeader column={column} title="Created At" />
  //   ),
  //   cell: ({ row }) => {
  //     dayjs.extend(RelativeTime);
  //     const createdAt = dayjs(row.getValue("createdAt"));
  //     const formatted = createdAt.format("YYYY-MM-DD HH:mm:ss");
  //     const relative = createdAt.fromNow();
  //     return (
  //       <div className="text-right " title={formatted}>
  //         {relative}
  //       </div>
  //     );
  //   },
  // },
  // More settings
  {
    accessorKey: "repeatability",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Repeatability" />
    ),
    cell: ({ row }) => {
      return (
        <span
          className="truncate max-w-[100px]"
          title={row.original.repeatability || undefined}
        >
          {row.original.repeatability === "once_per_user"
            ? "Once per user"
            : "Unlimited"}
        </span>
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
  const router = useRouter();

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
          <DropdownMenuItem
            onSelect={() => {
              router.push(resolveBasePath(`/rules/${row.original.id}/edit`));
            }}
          >
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
