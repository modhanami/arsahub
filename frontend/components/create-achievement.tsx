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
import { useCreateAchievement } from "@/hooks";
import { ValidationLengths, ValidationMessages } from "@/types/generated-types";
import { isAlphaNumericExtended } from "@/lib/validations";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import { TextareaWithCounter } from "@/components/ui/textarea-with-counter";
import { isApiError, isApiValidationError } from "@/api";
import { HttpStatusCode } from "axios";

export const achievementCreateSchema = z.object({
  title: z
    .string({
      required_error: ValidationMessages.TITLE_REQUIRED,
    })
    .trim()
    .min(4, { message: ValidationMessages.TITLE_LENGTH })
    .max(200, { message: ValidationMessages.TITLE_LENGTH })
    .refine((value) => isAlphaNumericExtended(value, true), {
      message: ValidationMessages.TITLE_PATTERN,
    }),
  description: z
    .string()
    .max(500, { message: ValidationMessages.DESCRIPTION_LENGTH })
    .optional(),
});

type FormData = z.infer<typeof achievementCreateSchema>;

export function AchievementCreateForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(achievementCreateSchema),
  });
  const [isOpen, setIsOpen] = React.useState(false);
  const mutation = useCreateAchievement();

  async function onSubmit(values: FormData) {
    mutation.mutate(
      {
        title: values.title,
        description: values.description || null,
      },
      {
        onSuccess: () => {
          toast({
            title: "Achievement created",
            description: "Achievement created successfully",
          });
          setIsOpen(false);
          router.refresh();
        },
        onError: (error, b, c) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            // TODO: handle validation errors
          }
          if (isApiError(error)) {
            // root error
            if (error.response?.status === HttpStatusCode.Conflict) {
              form.setError("title", {
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
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <Button>
            <Icons.add className="mr-2 h-4 w-4" />
            New Achievement
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create achievement</CardTitle>
          </DialogHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Title</FormLabel>
                    <FormControl>
                      <InputWithCounter
                        placeholder="Name of your achievement"
                        maxLength={ValidationLengths.TITLE_MAX_LENGTH}
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
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Description</FormLabel>
                    <FormControl>
                      <TextareaWithCounter
                        placeholder="Description of your achievement"
                        maxLength={ValidationLengths.DESCRIPTION_MAX_LENGTH}
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
