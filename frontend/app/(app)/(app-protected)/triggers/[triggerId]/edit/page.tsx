"use client";
import * as React from "react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { isApiError, isApiValidationError } from "@/api";
import { useTrigger, useUpdateTrigger } from "@/hooks";
import { DevTool } from "@hookform/devtools";
import { HttpStatusCode } from "axios";
import { isAlphaNumericExtended } from "@/lib/validations";
import {
  TriggerResponse,
  ValidationLengths,
  ValidationMessages,
} from "@/types/generated-types";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import { TextareaWithCounter } from "@/components/ui/textarea-with-counter";
import { toast } from "@/components/ui/use-toast";
import {
  generateTriggerKeyFromTitle,
  getFieldTypeLabel,
} from "@/app/(app)/(app-protected)/triggers/shared";
import { Label } from "@/components/ui/label";
import { resolveBasePath } from "@/lib/base-path";
import { useRouter } from "next/navigation";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";

const triggerUpdateSchema = z.object({
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

type FormData = z.infer<typeof triggerUpdateSchema>;
export default function UpdateTriggerPage({
  params,
}: {
  params: { triggerId: string };
}) {
  const triggerId = Number(params.triggerId);
  const { data: trigger, isPending, isLoading, error } = useTrigger(triggerId);

  if (
    !trigger ||
    (error &&
      isApiError(error) &&
      error.response?.status === HttpStatusCode.NotFound)
  ) {
    console.log("trigger not found", trigger, error);
    return <div>Trigger not found</div>;
  }

  console.log("trigger", trigger);

  return <UpdateTriggerForm trigger={trigger} />;
}

interface UpdateTriggerFormProps {
  trigger: TriggerResponse;
}

function getDefaultValues(trigger: TriggerResponse) {
  return {
    title: trigger.title ?? "",
    description: trigger.description ?? undefined,
  };
}

function UpdateTriggerForm({ trigger }: UpdateTriggerFormProps) {
  const form = useForm<FormData>({
    resolver: zodResolver(triggerUpdateSchema),
    defaultValues: getDefaultValues(trigger),
  });
  const router = useRouter();
  const mutation = useUpdateTrigger();

  async function onSubmit(values: FormData) {
    try {
      const updatedTrigger = await mutation.mutateAsync({
        triggerId: trigger.id!,
        updateRequest: {
          title: values.title ?? null,
          description: values.description ?? null,
        },
      });
      toast({
        title: "Trigger updated",
        description: "Your trigger was updated successfully.",
      });

      form.reset(getDefaultValues(updatedTrigger));
      router.push(resolveBasePath(`/triggers`));
    } catch (error) {
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
    }
  }

  return (
    <DashboardShell>
      <Button
        type="button"
        onClick={() => router.push(resolveBasePath(`/triggers`))}
        variant="outline"
        className="h-8 self-start px-2 group"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          className="mr-1 h-4 w-4 transition-transform group-hover:-translate-x-1"
        >
          <polyline points="15 18 9 12 15 6" />
        </svg>{" "}
        Back
      </Button>

      <DashboardHeader
        heading="Update Trigger"
        text="Update your trigger details."
        separator
      ></DashboardHeader>
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="space-y-4 max-w-2xl"
        >
          {form.formState.errors.root?.serverError && (
            <div className="bg-red-100 text-red-600 text-sm px-4 py-2 rounded-md">
              {form.formState.errors.root.serverError.message}
            </div>
          )}
          <FormField
            control={form.control}
            name="title"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Title</FormLabel>
                <FormControl>
                  <InputWithCounter
                    placeholder="Title of your trigger"
                    maxLength={ValidationLengths.TITLE_MAX_LENGTH}
                    {...field}
                  />
                </FormControl>
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
                    placeholder="Description of your trigger"
                    maxLength={ValidationLengths.DESCRIPTION_MAX_LENGTH}
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormItem>
            <FormLabel>Auto-generated key</FormLabel>
            <p className="text-gray-500 text-sm">
              This is the key that you will use for sending triggers for your
              app users. It will be auto-generated from the title. Does not
              change once set.
            </p>
            <Input
              value={generateTriggerKeyFromTitle(trigger.title ?? "")}
              placeholder="This will be auto-generated from the title"
              readOnly
              disabled
            />
          </FormItem>

          <div className="flex items-center justify-between pt-4">
            <Label>Fields</Label>
          </div>

          {trigger.fields?.map((fieldDefinition, index) => (
            <div key={index} className="flex gap-2 flex-auto">
              <FormItem className="w-3/6">
                <FormControl>
                  <Input value={fieldDefinition.key ?? "-"} disabled />
                </FormControl>
              </FormItem>

              <FormItem>
                <Input
                  className="flex items-center justify-between w-full"
                  disabled
                  value={getFieldTypeLabel(fieldDefinition.type ?? "") ?? "-"}
                />
              </FormItem>
            </div>
          ))}

          <div className="flex justify-between pt-2">
            <Button
              type="submit"
              disabled={mutation.isPending || !form.formState.isDirty}
            >
              Update
            </Button>
          </div>
        </form>

        <DevTool control={form.control} />
      </Form>
    </DashboardShell>
  );
}
