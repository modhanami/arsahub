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
import { API_URL, makeAppAuthHeader } from "@/hooks/api";
import { Icons } from "@/components/icons";
import { useCurrentApp } from "@/lib/current-app";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AppUserCreateRequest, AppUserResponse } from "@/types/generated-types";

export const userCreateSchema = z.object({
  uniqueId: z
    .string({
      required_error: "Unique ID is required",
    })
    .min(4, { message: "Must be between 4 and 200 characters" })
    .max(200, { message: "Must be between 4 and 200 characters" }),
  displayName: z
    .string({
      required_error: "Display name is required",
    })
    .min(4, { message: "Must be between 4 and 200 characters" })
    .max(500, { message: "Must not be more than 500 characters" }),
});

type FormData = z.infer<typeof userCreateSchema>;

type Props = {
  activityId: number;
};

export function UserCreateForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(userCreateSchema),
  });
  const [isOpen, setIsOpen] = React.useState(false);
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();

  async function createUser(
    newUser: AppUserCreateRequest,
  ): Promise<AppUserResponse> {
    const response = await fetch(`${API_URL}/apps/users`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...makeAppAuthHeader(currentApp),
      },
      body: JSON.stringify(newUser),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    return response.json();
  }

  const mutation = useMutation({
    mutationFn: (newUser: AppUserCreateRequest) => createUser(newUser),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      toast({
        title: "User created",
        description: "Your app user was created successfully.",
      });
    },
    onError: () => {
      toast({
        title: "Something went wrong.",
        description: "Your app user was not created. Please try again.",
        variant: "destructive",
      });
    },
  });

  async function onSubmit(values: FormData) {
    await mutation.mutateAsync(values);

    setIsOpen(false);
    router.refresh();
  }

  return (
    <>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <Button>
            <Icons.add className="mr-2 h-4 w-4" />
            New User
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create user</CardTitle>
          </DialogHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="uniqueId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Unique ID</FormLabel>
                    <FormDescription>
                      This must be unique across all users in your app.
                      <br />
                      Used for referencing your user in the API.
                    </FormDescription>
                    <FormControl>
                      <Input placeholder="Unique ID of your user" {...field} />
                    </FormControl>
                    <FormDescription />
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="displayName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Display Name</FormLabel>
                    <FormDescription>
                      The display name of your user.
                    </FormDescription>
                    <FormControl>
                      <Input
                        placeholder="Display name of your user"
                        {...field}
                      />
                    </FormControl>
                    <FormDescription />
                    <FormMessage />
                  </FormItem>
                )}
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
