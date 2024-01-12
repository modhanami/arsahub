"use client";
import * as React from "react";
import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
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
import { Icons } from "@/components/icons";
import { useCreateAppUser } from "@/hooks";
import { ValidationLengths, ValidationMessages } from "@/types/generated-types";
import { isApiError, isApiValidationError } from "@/api";
import { HttpStatusCode } from "axios";
import { InputWithCounter } from "@/components/ui/input-with-counter";

export const userCreateSchema = z.object({
  uniqueId: z
    .string({
      required_error: ValidationMessages.APP_USER_UID_REQUIRED,
    })
    .min(4, { message: ValidationMessages.APP_USER_UID_LENGTH })
    .max(200, { message: ValidationMessages.APP_USER_UID_LENGTH }),
  displayName: z
    .string({
      required_error: ValidationMessages.APP_USER_DISPLAY_NAME_REQUIRED,
    })
    .min(4, { message: ValidationMessages.APP_USER_DISPLAY_NAME_LENGTH })
    .max(200, { message: ValidationMessages.APP_USER_DISPLAY_NAME_LENGTH }),
});

type FormData = z.infer<typeof userCreateSchema>;

export function UserCreateForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(userCreateSchema),
  });
  const [isOpen, setIsOpen] = React.useState(false);
  const mutation = useCreateAppUser();

  async function onSubmit(values: FormData) {
    mutation.mutate(
      {
        uniqueId: values.uniqueId,
        displayName: values.displayName,
      },
      {
        onSuccess: () => {
          toast({
            title: "User created",
            description: "User created successfully",
          });
          setIsOpen(false);
        },
        onError: (error, b, c) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            // TODO: handle validation errors
          }
          if (isApiError(error)) {
            // root error
            if (error.response?.status === HttpStatusCode.Conflict) {
              form.setError("uniqueId", {
                message: error.response.data.message,
              });
            }
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
                      <InputWithCounter
                        placeholder="Unique ID of your user"
                        maxLength={ValidationLengths.APP_USER_UID_MAX_LENGTH}
                        {...field}
                      />
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
                      <InputWithCounter
                        placeholder="Display name of your user"
                        maxLength={
                          ValidationLengths.APP_USER_DISPLAY_NAME_MAX_LENGTH
                        }
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
