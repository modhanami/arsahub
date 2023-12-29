"use client";
import * as React from "react";

import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "@/components/ui/dialog";
import { toast } from "./ui/use-toast";
import { useRouter } from "next/navigation";
import { isApiValidationError } from "@/api";
import { Label } from "@/components/ui/label";
import { Icons } from "@/components/icons";
import { useCreateTrigger } from "@/hooks";

export const triggerCreateSchema = z.object({
  title: z
    .string({
      required_error: "Title is required",
    })
    .min(4, { message: "Must be between 4 and 200 characters" })
    .max(200, { message: "Must be between 4 and 200 characters" }),
  description: z
    .string()
    .max(500, { message: "Must not be more than 500 characters" })
    .optional(),
});

function generateKeyFromTitle(title: string): string | undefined {
  const regex = /[a-zA-Z0-9_-]/g;
  const matches = title.match(regex);

  if (matches) {
    console.log("Matches", matches);
    return matches.join("_");
  } else {
    console.log("No matches found");
    return;
  }
}

type FormData = z.infer<typeof triggerCreateSchema>;

export function TriggerCreateForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(triggerCreateSchema),
  });
  const [isOpen, setIsOpen] = React.useState(false);
  const mutation = useCreateTrigger();

  async function onSubmit(values: FormData) {
    const generatedKey = generateKeyFromTitle(values.title);
    if (!generatedKey) {
      return; // TODO: handle error
    }

    mutation.mutate(
      {
        title: values.title,
        description: values.description || "",
        key: generatedKey,
        fields: [],
      },
      {
        onSuccess: (data) => {
          console.log("data", data);
          toast({
            title: "Trigger created",
            description: "Your trigger was created successfully.",
          });
          setIsOpen(false);
        },
        onError: (error, b, c) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            // TODO: handle validation errors
          }
        },
      },
    );
  }

  return (
    <>
      <Dialog open={isOpen || mutation.isPending} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <Button>
            <Icons.add className="mr-2 h-4 w-4" />
            New Trigger
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create trigger</CardTitle>
            {/* <DialogTitle>Are you sure absolutely sure?</DialogTitle> */}
          </DialogHeader>
          {/* <CardDescription>Deploy your new activity in one-click.</CardDescription> */}
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              {/* <Card className="w-[350px]"> */}
              {/* <CardContent> */}
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Title</FormLabel>
                    <FormControl>
                      <Input placeholder="Name of your trigger" {...field} />
                    </FormControl>
                    <FormDescription>
                      {/* This is your public display name. */}
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Description</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Description of your trigger"
                        {...field}
                      />
                    </FormControl>
                    <FormDescription>
                      {/* This is your public display name. */}
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Label>Auto-generated key</Label>
              <p className="text-gray-500 text-sm">
                This is the key that you will use to trigger for users in this
                activity.
              </p>
              <Input
                value={
                  form.watch("title") &&
                  generateKeyFromTitle(form.watch("title"))
                }
                readOnly
                disabled
              />

              <div className="flex justify-between mt-8">
                <DialogClose asChild>
                  <Button variant="outline" type="button">
                    Cancel
                  </Button>
                </DialogClose>
                <Button type="submit" disabled={mutation.isPending}>
                  Create
                </Button>
              </div>
            </form>
          </Form>
        </DialogContent>
      </Dialog>
    </>
  );
}
