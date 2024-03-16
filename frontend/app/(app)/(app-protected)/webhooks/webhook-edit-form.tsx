import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as React from "react";
import { useUpdateWebhook } from "@/hooks";
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

export const webhookCreateSchema = z.object({
  url: z
    .string({
      // required_error: ValidationMessages.WEBHOOK_URL_REQUIRED,
    })
    .url({
      // message: ValidationMessages.WEBHOOK_URL_INVALID
    }),
});

type FormData = z.infer<typeof webhookCreateSchema>;

interface UpdateWebhookFormProps {
  webhookId: number;
  url: string;
  onUpdated?: () => void;
}

export function WebhookEditForm({
  webhookId,
  url,
  onUpdated,
}: UpdateWebhookFormProps) {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(webhookCreateSchema),
    defaultValues: {
      url: url,
    },
  });
  const mutation = useUpdateWebhook();

  async function onSubmit(values: FormData) {
    mutation.mutate(
      {
        webhookId: webhookId,
        updateRequest: {
          url: values.url,
        },
      },
      {
        onSuccess: (updatedWebhook) => {
          toast({
            title: "Webhook updated",
            description: "Webhook updated successfully",
          });
          onUpdated?.();
          form.reset({
            url: updatedWebhook.url,
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
              name="url"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>URL</FormLabel>
                  <FormDescription>The URL of the webhook</FormDescription>
                  <FormControl>
                    <InputWithCounter
                      placeholder="URL of the webhook"
                      maxLength={9999} // TODO: handle max length
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
