import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as React from "react";
import { useCreateWebhook } from "@/hooks";
import { toast } from "@/components/ui/use-toast";
import { isApiError, isApiValidationError } from "@/api";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Icons } from "@/components/icons";
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
      message: "Invalid URL",
    }),
});

type FormData = z.infer<typeof webhookCreateSchema>;

export function WebhookCreateForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(webhookCreateSchema),
  });
  const [isOpen, setIsOpen] = React.useState(false);
  const mutation = useCreateWebhook();

  async function onSubmit(values: FormData) {
    mutation.mutate(
      {
        url: values.url,
      },
      {
        onSuccess: () => {
          toast({
            title: "Webhook created",
            description: "Webhook created successfully",
          });
          setIsOpen(false);
          form.reset();
        },
        onError: (error, b, c) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            toast({
              title: "Failed to create webhook",
              description:
                error.response?.data.errors["url"] ||
                error.response?.data.message,
              variant: "destructive",
            });
          }
          if (isApiError(error)) {
            // root error
            toast({
              title: "Failed to create webhook",
              description: error.response?.data.message || error.message,
              variant: "destructive",
            });
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
            New Webhook
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create webhook</CardTitle>
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
