"use client";

import { ColumnDef } from "@tanstack/react-table";

import { AppUserResponse } from "@/types/generated-types";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";
import React from "react";
import { DataTableRowActionsProps } from "@/app/(app)/examples/tasks/components/data-table-row-actions";
import { useDeleteAppUser } from "@/hooks";
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

export const columns: ColumnDef<AppUserResponse>[] = [
  {
    accessorKey: "userId",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="User ID" />
    ),
    cell: ({ row }) => {
      return (
        <div
          className="flex space-x-2 max-w-[500px]"
          title={row.getValue("userId")}
        >
          <span className=" truncate">{row.getValue("userId")}</span>
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
        <div
          className="max-w-[500px] flex space-x-2"
          title={row.getValue("displayName")}
        >
          <span className="truncate">{row.getValue("displayName")}</span>
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
        <div className="max-w-[200px]" title={row.getValue("points")}>
          <span className="truncate">{row.getValue("points")}</span>
        </div>
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
        <div
          className="max-w-[100px]"
          title={row.original.achievements?.length}
        >
          <span className="truncate">{row.original.achievements?.length}</span>
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
  //       <div className="text-right" title={formatted}>
  //         {relative}
  //       </div>
  //     );
  //   },
  // },
  {
    id: "actions",
    cell: ({ row }) => <AppUserRowActions row={row} />,
  },
];

function AppUserRowActions({ row }: DataTableRowActionsProps<AppUserResponse>) {
  const [showEditDialog, setShowEditDialog] = React.useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = React.useState(false);
  const deleteAppUser = useDeleteAppUser();

  async function handleDelete() {
    try {
      await deleteAppUser.mutateAsync(row.original.userId!);
    } catch (error) {
      if (isApiError(error)) {
        toast({
          description:
            "Failed to delete app user: " + error.response?.data.message ||
            error.message,
          variant: "destructive",
        });
        return;
      }
    }

    setShowDeleteDialog(false);
    toast({
      description: `App User '${row.original.userId}' has been deleted.`,
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
              This app user &apos;
              {row.original.userId}&apos; will not be recoverable.
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
