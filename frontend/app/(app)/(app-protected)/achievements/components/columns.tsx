"use client";
import { Image } from "@nextui-org/react";
import { ColumnDef } from "@tanstack/react-table";
import { AchievementResponse } from "@/types/generated-types";
import { DataTableColumnHeader } from "@/app/(app)/(app-protected)/triggers/components/data-table-column-header";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { DotsHorizontalIcon } from "@radix-ui/react-icons";
import { Icons } from "@/components/icons";
import React from "react";
import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { toast } from "@/components/ui/use-toast";
import { useDeleteAchievement } from "@/hooks";
import { isApiError } from "@/api";
import { DataTableRowActionsProps } from "@/app/(app)/examples/tasks/components/data-table-row-actions";
import { getImageUrlFromKey } from "@/lib/image";

export const columns: ColumnDef<AchievementResponse>[] = [
  {
    accessorKey: "title",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Title" />
    ),
    cell: ({ row }) => {
      const achievement = row.original;
      return (
        <div className="flex gap-4">
          {achievement.imageKey && (
            <Image
              src={getImageUrlFromKey(achievement.imageKey)}
              width={72}
              height={72}
              alt={`achievement image ${achievement.title}`}
              radius="none"
            />
          )}
          <span className="max-w-[400px] truncate font-medium">
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
          <span className="max-w-[400px] truncate font-medium">
            {row.getValue("description")
              ? row.getValue("description")
              : "No Description."}
          </span>
        </div>
      );
    },
  },
  {
    id: "actions",
    cell: ({ row }) => <AchievementRowActions row={row} />,
  },
];

export function AchievementRowActions({
  row,
}: DataTableRowActionsProps<AchievementResponse>) {
  const [showEditDialog, setShowEditDialog] = React.useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = React.useState(false);
  const deleteAchievement = useDeleteAchievement();

  async function handleDelete() {
    try {
      await deleteAchievement.mutateAsync(row.original.achievementId!);
    } catch (error) {
      if (isApiError(error)) {
        toast({
          description:
            "Failed to delete achievement: " + error.response?.data.message ||
            error.message,
          variant: "destructive",
        });
        return;
      }
    }

    setShowDeleteDialog(false);
    toast({
      description: `Achievement '${row.original.title}' has been deleted.`,
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
              This achievement &apos;
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
