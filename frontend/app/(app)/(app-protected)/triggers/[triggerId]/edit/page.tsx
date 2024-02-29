"use client";
import * as React from "react";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
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
import { isApiError, isApiValidationError } from "@/api";
import { useUpdateTrigger } from "@/hooks";
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
import { generateTriggerKeyFromTitle } from "@/app/(app)/(app-protected)/triggers/new/page";

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
  // const trigger = useTrigger(triggerId);
}

interface UpdateTriggerFormProps {
  trigger: TriggerResponse;
}

function UpdateTriggerForm({ trigger }: UpdateTriggerFormProps) {
  const form = useForm<FormData>({
    resolver: zodResolver(triggerUpdateSchema),
    defaultValues: {
      title: trigger.title ?? "",
      description: trigger.description ?? undefined,
    },
  });
  const mutation = useUpdateTrigger();

  async function onSubmit(values: FormData) {
    try {
      const data = await mutation.mutateAsync({
        triggerId: trigger.id!,
        updateRequest: {
          title: values.title ?? null,
          description: values.description ?? null,
        },
      });
      console.log("data", data);
      toast({
        title: "Trigger created",
        description: "Your trigger was created successfully.",
      });
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
    <Card>
      <CardHeader>
        <CardTitle>Update trigger</CardTitle>
      </CardHeader>
      <CardContent>
        {/* <CardDescription>Deploy your new activity in one-click.</CardDescription> */}
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
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
                    <TextareaWithCounter
                      placeholder="Description of your trigger"
                      maxLength={ValidationLengths.DESCRIPTION_MAX_LENGTH}
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

            <FormItem>
              <FormLabel>Auto-generated key</FormLabel>
              <p className="text-gray-500 text-sm">
                This is the key that you will use for sending triggers for your
                app users. It will be auto-generated from the title.
              </p>
              <Input
                value={generateTriggerKeyFromTitle(trigger.title ?? "")}
                placeholder="This will be auto-generated from the title"
                readOnly
                disabled
              />
            </FormItem>

            {/*{form.watch("fields")?.map((fieldDefinition, index) => (*/}
            {/*  <div key={index} className="flex gap-2 flex-auto">*/}
            {/*    <FormField*/}
            {/*      control={form.control}*/}
            {/*      name={`fields.${index}.key`}*/}
            {/*      render={({ field }) => (*/}
            {/*        <FormItem className="w-3/6">*/}
            {/*          <FormControl>*/}
            {/*            <Input placeholder="Key" {...field} />*/}
            {/*          </FormControl>*/}
            {/*          {form.formState.touchedFields["fields"]?.[index]?.key &&*/}
            {/*            form.formState.dirtyFields["fields"]?.[index]?.key && (*/}
            {/*              <FormMessage />*/}
            {/*            )}*/}
            {/*        </FormItem>*/}
            {/*      )}*/}
            {/*    />*/}

            {/*    <FormField*/}
            {/*      control={form.control}*/}
            {/*      name={`fields.${index}.type`}*/}
            {/*      render={({ field }) => (*/}
            {/*        <FormItem>*/}
            {/*          <FormControl>*/}
            {/*            <Select*/}
            {/*              onValueChange={field.onChange}*/}
            {/*              value={field.value}*/}
            {/*            >*/}
            {/*              <SelectTrigger>*/}
            {/*                <SelectValue*/}
            {/*                  placeholder="Select a type"*/}
            {/*                  className="w-1"*/}
            {/*                />*/}
            {/*              </SelectTrigger>*/}
            {/*              <SelectContent className="w-full">*/}
            {/*                {FieldTypeEnum.options.map((type) => (*/}
            {/*                  <SelectItem*/}
            {/*                    key={type}*/}
            {/*                    value={type}*/}
            {/*                    className="flex items-center justify-between w-full"*/}
            {/*                  >*/}
            {/*                    {type}*/}
            {/*                  </SelectItem>*/}
            {/*                ))}*/}
            {/*              </SelectContent>*/}
            {/*            </Select>*/}
            {/*          </FormControl>*/}
            {/*          <FormMessage />*/}
            {/*        </FormItem>*/}
            {/*      )}*/}
            {/*    />*/}

            {/*    <div className="w-1/6">*/}
            {/*      <Button*/}
            {/*        variant="ghost"*/}
            {/*        onClick={() => removeField(index)}*/}
            {/*        size="icon"*/}
            {/*      >*/}
            {/*        <Icons.trash className="h-4 w-4" />*/}
            {/*      </Button>*/}
            {/*    </div>*/}
            {/*  </div>*/}
            {/*))}*/}

            <div className="flex justify-between pt-2">
              <Button type="submit" disabled={mutation.isPending}>
                Create
              </Button>
            </div>
          </form>

          <DevTool control={form.control} />
        </Form>
      </CardContent>
    </Card>
  );
}
