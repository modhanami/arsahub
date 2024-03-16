"use client";

import { ColumnDef } from "@tanstack/react-table";

import { WebhookResponse } from "@/types/generated-types";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";
import React from "react";
import { DataTableRowActionsProps } from "@/app/(app)/examples/tasks/components/data-table-row-actions";
import { useDeleteAppUser } from "@/hooks";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { DotsHorizontalIcon } from "@radix-ui/react-icons";
import { Icons } from "@/components/icons";
import { Dialog } from "@/components/ui/dialog";
import { WebhookEditForm } from "@/app/(app)/(app-protected)/webhooks/webhook-edit-form";

export const columns: ColumnDef<WebhookResponse>[] = [
  {
    accessorKey: "id",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="ID" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate">{row.getValue("id")}</span>
        </div>
      );
    },
  },
  {
    accessorKey: "url",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="URL" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <span className="max-w-[500px] truncate">{row.getValue("url")}</span>
        </div>
      );
    },
  },
  {
    id: "actions",
    cell: ({ row }) => <WebhookRowActions row={row} />,
  },
];

function WebhookRowActions({ row }: DataTableRowActionsProps<WebhookResponse>) {
  const [showEditDialog, setShowEditDialog] = React.useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = React.useState(false);
  const deleteAppUser = useDeleteAppUser();

  // async function handleDelete() {
  //   try {
  //     await deleteAppUser.mutateAsync(row.original.userId!);
  //   } catch (error) {
  //     if (isApiError(error)) {
  //       toast({
  //         description:
  //           "Failed to delete app user: " + error.response?.data.message ||
  //           error.message,
  //         variant: "destructive",
  //       });
  //       return;
  //     }
  //   }
  //
  //   setShowDeleteDialog(false);
  //   toast({
  //     description: `App User '${row.original.userId}' has been deleted.`,
  //   });
  // }

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

      <Dialog open={showEditDialog} onOpenChange={setShowEditDialog}>
        <WebhookEditForm
          webhookId={row.original.id}
          url={row.original.url}
          onUpdated={() => {
            setShowEditDialog(false);
          }}
        />
      </Dialog>
    </>
  );
}
