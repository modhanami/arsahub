import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as React from "react";
import { toast } from "@/components/ui/use-toast";
import { isApiError, isApiValidationError } from "@/api";
import { HttpStatusCode } from "axios";
import {
  DialogClose,
  DialogContent,
  DialogHeader,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import * as z from "zod";
import { ValidationLengths, ValidationMessages } from "@/types/generated-types";
import { useUpdateAppUser } from "@/hooks";

export const appUserUpdateSchema = z.object({
  displayName: z
    .string({
      required_error: ValidationMessages.APP_USER_DISPLAY_NAME_REQUIRED,
    })
    .min(4, { message: ValidationMessages.APP_USER_DISPLAY_NAME_LENGTH })
    .max(200, { message: ValidationMessages.APP_USER_DISPLAY_NAME_LENGTH }),
});

type FormData = z.infer<typeof appUserUpdateSchema>;

interface UpdateAppUserFormProps {
  userId: string;
  displayName: string;
  onUpdated?: () => void;
}

export function AppUserEditForm({
  userId,
  displayName,
  onUpdated,
}: UpdateAppUserFormProps) {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(appUserUpdateSchema),
    defaultValues: {
      displayName: displayName,
    },
  });
  const mutation = useUpdateAppUser();

  async function onSubmit(values: FormData) {
    mutation.mutate(
      {
        userId: userId,
        updateRequest: {
          displayName: values.displayName,
        },
      },
      {
        onSuccess: (updatedAppUser) => {
          toast({
            title: "App user updated",
            description: "App user updated successfully",
          });
          onUpdated?.();
          form.reset({
            displayName: updatedAppUser.displayName,
          });
        },
        onError: (error, b, c) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            // TODO: handle validation errors
          }
          if (isApiError(error)) {
            // root error
            if (error.response?.status === HttpStatusCode.Conflict) {
              // TODO: handle conflict error (URL already exists)
            }
          }
        },
      },
    );
  }

  return (
    <>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <CardTitle>Edit Webhook</CardTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
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
                <Button
                  variant="outline"
                  type="button"
                  onClick={() => form.reset()}
                >
                  Cancel
                </Button>
              </DialogClose>
              <Button
                type="submit"
                disabled={mutation.isPending || !form.formState.isDirty}
              >
                Save
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </>
  );
}
