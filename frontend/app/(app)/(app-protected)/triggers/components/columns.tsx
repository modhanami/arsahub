"use client";

import { ColumnDef } from "@tanstack/react-table";
import { TriggerResponse } from "@/types/generated-types";
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
import { useDeleteTrigger } from "@/hooks";
import { isApiError } from "@/api";
import { DataTableRowActionsProps } from "@/app/(app)/examples/tasks/components/data-table-row-actions";
import { resolveBasePath } from "@/lib/base-path";
import { useRouter } from "next/navigation";
import {
  Dialog,
  DialogContent,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { cn } from "@/lib/utils";

interface KeyTextProps {
  text: string | undefined | null;
  variant?: "outline" | "solid";
}

export function KeyText({ text, variant }: KeyTextProps) {
  const variantClass =
    variant === "outline"
      ? "border border-muted-foreground/50 text-primary/80"
      : "bg-muted border border-muted-foreground/50 text-muted-foreground";
  return (
    <span
      className={cn(
        " truncate font-medium text-sm font-mono px-2 py-0.5  rounded-md",
        variantClass,
      )}
    >
      {text}
    </span>
  );
}

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
    cell: ({ row }) => {
      const key = (row.getValue("key") as TriggerResponse["key"])!;
      return (
        //   Monospace
        <div className="flex max-w-[400px] gap-2 justify-start" title={key}>
          <KeyText text={key} />
        </div>
      );
    },
    // enableSorting: false,
    // enableHiding: false,
  },
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
    accessorKey: "fields",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Fields" />
    ),
    cell: ({ row }) => {
      const fields = row.getValue("fields") as TriggerResponse["fields"];
      if (!fields) return null;

      const firstTwoFields = fields.slice(0, 2);
      const moreFields = fields.slice(2);
      const showMoreFields = moreFields.length > 0;
      return (
        <div className="flex flex-col max-w-[300px] gap-2">
          {/*  Should group of field name to field type */}
          {/* Should first two fields. If more than that, show eye icon to show full fields using dialog */}
          {firstTwoFields.map((field) => (
            <div key={field.key} className="flex gap-2">
              <KeyText text={field.key} variant="outline" />
              <span className="truncate text-muted-foreground">
                {field.type}
              </span>
            </div>
          ))}
          {showMoreFields && (
            <Dialog>
              <DialogTrigger className="text-left" asChild>
                {/*<span className="text-left">+{moreFields.length}</span>*/}
                <Button
                  variant="outline"
                  className="text-left h-4 self-start p-3"
                  type="button"
                >
                  +{moreFields.length}
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-[500px]">
                <DialogTitle>{row.original.title}&apos; Fields</DialogTitle>
                <div className="flex flex-col gap-2">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Field</TableHead>
                        <TableHead>Type</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {fields.map((field) => (
                        <TableRow key={field.key}>
                          <TableCell className="font-medium p-2">
                            {field.key}
                          </TableCell>
                          <TableCell className="p-2 text-muted-foreground">
                            {field.type}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              </DialogContent>
            </Dialog>
          )}
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
  //       <div className="text-right font-medium" title={formatted}>
  //         {relative}
  //       </div>
  //     );
  //   },
  // },
  {
    id: "actions",
    cell: ({ row }) => <TriggerRowActions row={row} />,
  },
];

export function TriggerRowActions({
  row,
}: DataTableRowActionsProps<TriggerResponse>) {
  const [showEditDialog, setShowEditDialog] = React.useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = React.useState(false);
  const deleteTrigger = useDeleteTrigger();
  const router = useRouter();

  async function handleDelete() {
    try {
      await deleteTrigger.mutateAsync(row.original.id!);
    } catch (error) {
      if (isApiError(error)) {
        toast({
          description:
            "Failed to delete trigger: " + error.response?.data.message ||
            error.message,
          variant: "destructive",
        });
        return;
      }
    }

    setShowDeleteDialog(false);
    toast({
      description: `Trigger '${row.original.title}' has been deleted.`,
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
              router.push(resolveBasePath(`/triggers/${row.original.id}/edit`));
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
              This trigger &apos;
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
