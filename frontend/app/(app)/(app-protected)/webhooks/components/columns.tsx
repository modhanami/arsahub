"use client";

import { ColumnDef } from "@tanstack/react-table";

import { WebhookResponse } from "@/types/generated-types";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";
import React from "react";
import { DataTableRowActionsProps } from "@/app/(app)/examples/tasks/components/data-table-row-actions";
import { useDeleteWebhook } from "@/hooks";
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
import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { isApiError } from "@/api";
import { toast } from "@/components/ui/use-toast";

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
    accessorKey: "secretKey",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Secret" />
    ),
    cell: ({ row }) => {
      return <ShowHideReadonlyCell value={row.getValue("secretKey")} />;
    },
  },
  {
    id: "actions",
    cell: ({ row }) => <WebhookRowActions row={row} />,
  },
];

function ShowHideReadonlyCell({ value }: { value: string }) {
  const [show, setShow] = React.useState(false);
  return (
    <div className="flex space-x-2 items-center">
      <span className="w-[290px] truncate">
        {show ? value : "•".repeat(34)}
      </span>
      <Button
        variant="outline"
        onClick={() => setShow((prev) => !prev)}
        className="flex h-8 w-8 p-0"
      >
        {show ? (
          <Icons.eye className="h-4 w-4" />
        ) : (
          <Icons.eyeOff className="h-4 w-4" />
        )}
      </Button>
    </div>
  );
}

function WebhookRowActions({ row }: DataTableRowActionsProps<WebhookResponse>) {
  const [showEditDialog, setShowEditDialog] = React.useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = React.useState(false);
  const deleteWebhook = useDeleteWebhook();

  async function handleDelete() {
    try {
      await deleteWebhook.mutateAsync(row.original.id!);
    } catch (error) {
      if (isApiError(error)) {
        toast({
          description:
            "Failed to delete webhook: " + error.response?.data.message ||
            error.message,
          variant: "destructive",
        });
        return;
      }
    }

    setShowDeleteDialog(false);
    toast({
      description: `Webhook '${row.original.url}' has been deleted.`,
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

      <Dialog open={showEditDialog} onOpenChange={setShowEditDialog}>
        <WebhookEditForm
          webhookId={row.original.id}
          url={row.original.url}
          onUpdated={() => {
            setShowEditDialog(false);
          }}
        />
      </Dialog>

      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This webhook &apos;
              {row.original.url}&apos; will not be recoverable.
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
