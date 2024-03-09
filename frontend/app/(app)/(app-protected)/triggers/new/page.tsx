"use client";
import * as React from "react";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
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
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { isApiError, isApiValidationError } from "@/api";
import { Label } from "@/components/ui/label";
import { Icons } from "@/components/icons";
import { useCreateTrigger } from "@/hooks";
import { DevTool } from "@hookform/devtools";
import { HttpStatusCode } from "axios";
import { isAlphaNumericExtended } from "@/lib/validations";
import { ValidationLengths, ValidationMessages } from "@/types/generated-types";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import { TextareaWithCounter } from "@/components/ui/textarea-with-counter";
import { toast } from "@/components/ui/use-toast";
import {
  FieldTypeEnum,
  generateTriggerKeyFromTitle,
} from "@/app/(app)/(app-protected)/triggers/shared";
import { useRouter, useSearchParams } from "next/navigation";
import { triggerTemplates } from "@/app/(app)/(app-protected)/triggers/new/templates";

const triggerCreateSchema = z.object({
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
  fields: z.array(
    z.object({
      key: z
        .string()
        .min(4, { message: ValidationMessages.KEY_LENGTH })
        .max(200, { message: ValidationMessages.KEY_LENGTH })
        .refine((value) => isAlphaNumericExtended(value), {
          message: ValidationMessages.KEY_PATTERN,
        }),
      type: FieldTypeEnum,
      label: z
        .string()
        .min(4, { message: ValidationMessages.LABEL_LENGTH })
        .max(200, { message: ValidationMessages.LABEL_LENGTH })
        .optional()
        .or(z.literal("")),
    }),
  ),
});

type FormData = z.infer<typeof triggerCreateSchema>;

export default function Page({}) {
  const searchParams = useSearchParams();

  const templateId = searchParams.get("template");
  console.log("templateId", templateId);
  const template = templateId
    ? triggerTemplates.find((t) => t.id === templateId)
    : null;

  function getDefaultValues() {
    return {
      title: template?.title || "",
      description: template?.description || "",
      fields:
        template?.fields?.map((field) => {
          return {
            key: field.key!,
            type: field.type,
            label: field.label || "",
          };
        }) || [],
    };
  }

  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(triggerCreateSchema),
    defaultValues: getDefaultValues(),
  });
  console.log(form.formState.touchedFields);
  const [isOpen, setIsOpen] = React.useState(false);
  const mutation = useCreateTrigger();

  function addField() {
    const fields = form.getValues("fields");
    console.log("fields", fields);
    form.setValue("fields", [
      ...fields,
      {
        key: "",
        type: "Text",
        label: "",
      },
    ]);
  }

  function removeField(index: number) {
    const fields = form.getValues("fields");
    form.setValue(
      "fields",
      fields.filter((_, i) => i !== index),
    );
  }

  async function onSubmit(values: FormData) {
    const generatedKey = generateTriggerKeyFromTitle(values.title);
    if (!generatedKey) {
      form.setError("title", {
        message: "Invalid title",
      });
    }

    mutation.mutate(
      {
        title: values.title,
        description: values.description || "",
        fields: values.fields.map((field) => ({
          key: field.key,
          type: field.type.toLowerCase(),
          label: field.label || null,
        })),
      },
      {
        onSuccess: (data) => {
          console.log("data", data);
          toast({
            title: "Trigger created",
            description: "Your trigger was created successfully.",
          });
          setIsOpen(false);
          form.reset();
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
    <div className="grid gap-12 md:grid-cols-[1fr_300px]">
      <Card>
        <CardHeader>
          <CardTitle>Create trigger</CardTitle>
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
                  This is the key that you will use for sending triggers for
                  your app users. It will be auto-generated from the title.
                </p>
                <Input
                  value={
                    form.watch("title") &&
                    generateTriggerKeyFromTitle(form.watch("title"))
                  }
                  placeholder="This will be auto-generated from the title"
                  readOnly
                  disabled
                />
              </FormItem>

              <div className="flex items-center justify-between">
                <Label>Fields</Label>
                <Button variant="outline" onClick={() => addField()}>
                  Add field
                </Button>
              </div>

              {form.watch("fields")?.map((fieldDefinition, index) => (
                <div key={index} className="flex gap-2 flex-auto">
                  <FormField
                    control={form.control}
                    name={`fields.${index}.key`}
                    render={({ field }) => (
                      <FormItem className="w-3/6">
                        <FormControl>
                          <Input placeholder="Key" {...field} />
                        </FormControl>
                        {form.formState.touchedFields["fields"]?.[index]?.key &&
                          form.formState.dirtyFields["fields"]?.[index]
                            ?.key && <FormMessage />}
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name={`fields.${index}.type`}
                    render={({ field }) => (
                      <FormItem>
                        <FormControl>
                          <Select
                            onValueChange={field.onChange}
                            value={field.value}
                          >
                            <SelectTrigger>
                              <SelectValue
                                placeholder="Select a type"
                                className="w-1"
                              />
                            </SelectTrigger>
                            <SelectContent className="w-full">
                              {FieldTypeEnum.options.map((type) => (
                                <SelectItem
                                  key={type}
                                  value={type}
                                  className="flex items-center justify-between w-full"
                                >
                                  {type}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <div className="w-1/6">
                    <Button
                      variant="ghost"
                      onClick={() => removeField(index)}
                      size="icon"
                    >
                      <Icons.trash className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              ))}

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

      <Card>
        <CardHeader>
          <CardTitle>Use Template</CardTitle>
          <CardDescription>
            Select a template to pre-fill the form with the template details.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-4">
            <Select
              onValueChange={(value) => {
                if (
                  form.formState.isDirty &&
                  !confirm(
                    "You have unsaved changes. Are you sure you want to continue?",
                  )
                ) {
                  return;
                }

                router.push(`?template=${value}`);
                form.reset(getDefaultValues());
              }}
              value={templateId || ""}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select a template" />
              </SelectTrigger>
              <SelectContent>
                {triggerTemplates.map((template) => (
                  <SelectItem key={template.id} value={template.id}>
                    {template.title}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
