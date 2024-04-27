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
  getFieldTypeExample,
  getFieldTypeLabel,
} from "@/app/(app)/(app-protected)/triggers/shared";
import { DashboardHeader } from "@/components/header";
import { DashboardShell } from "@/components/shell";
import { useRouter, useSearchParams } from "next/navigation";
import {
  TriggerTemplate,
  triggerTemplates,
} from "@/app/(app)/(app-protected)/triggers/new/templates";
import { resolveBasePath } from "@/lib/base-path";
import { cx } from "class-variance-authority";
import { KeyText } from "../components/columns";
import { Separator } from "@/components/ui/separator";

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

  function getDefaultValues(template: TriggerTemplate | null = null) {
    return (
      {
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
      } || {
        title: "",
        description: "",
        fields: [],
      }
    );
  }

  function getEmptyDefaultValues() {
    return {
      title: "",
      description: "",
      fields: [],
    };
  }

  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(triggerCreateSchema),
    defaultValues: getDefaultValues(template),
  });
  console.log(form.formState.touchedFields);
  const [isOpen, setIsOpen] = React.useState(false);
  const mutation = useCreateTrigger();

  React.useEffect(() => {
    if (template) {
      form.reset(getDefaultValues(template));
    }
  }, [form, template]);

  function addField() {
    const fields = form.getValues("fields");
    console.log("fields", fields);
    form.setValue("fields", [
      ...fields,
      {
        key: "",
        type: "text",
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
          type: field.type,
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
          router.push(resolveBasePath(`/triggers`));
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
        heading="New Trigger"
        text="Create a new trigger, and start triggering for users."
        separator
      ></DashboardHeader>
      {/* <CardDescription>Deploy your new activity in one-click.</CardDescription> */}
      <div className="grid gap-12 grid-cols-1 lg:grid-cols-[1fr_400px] items-start place-self-start">
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="flex flex-col gap-2 max-w-2xl"
            // className="space-y-4 max-w-2xl"
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
              <p className="text-muted-foreground text-sm">
                This is the key that you will use for sending triggers for your
                app users. It will be auto-generated from the title.{" "}
                <strong>Does not change once set.</strong>
              </p>
              <KeyText
                text={generateTriggerKeyFromTitle(form.watch("title") || "-")}
                className="px-3 py-2 m-2 w-fit"
              />
            </FormItem>

            <div className="flex items-center justify-between mt-4">
              <div className="flex flex-col gap-2 pr-4">
                <FormLabel>Fields</FormLabel>
                <p className="text-muted-foreground text-sm">
                  Used to send <strong>additional information</strong> with your
                  trigger. They can be used for defining conditions in your
                  rules
                </p>
              </div>
              <Button
                variant="outline"
                onClick={() => addField()}
                type="button"
                className="flex gap-2"
              >
                <Icons.add className="h-4 w-4" />
                Add field
              </Button>
            </div>

            {form.watch("fields")?.map((fieldDefinition, index) => (
              <div key={index} className="flex gap-2 flex-auto">
                <FormField
                  control={form.control}
                  name={`fields.${index}.key`}
                  render={({ field }) => (
                    <FormItem className="w-3/6 font-mono">
                      <FormControl>
                        <Input placeholder="Key" {...field} />
                      </FormControl>
                      {form.formState.touchedFields["fields"]?.[index]?.key &&
                        form.formState.dirtyFields["fields"]?.[index]?.key && (
                          <FormMessage />
                        )}
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
                          <SelectContent className="w-full" position="popper">
                            {FieldTypeEnum.options.map((type) => (
                              <SelectItem
                                key={type}
                                value={type}
                                className="flex items-center justify-between w-full"
                              >
                                <div className="flex flex-col gap-1">
                                  <div>{getFieldTypeLabel(type)}</div>
                                  <div className="text-xs text-muted-foreground">
                                    Ex. {getFieldTypeExample(type)}
                                  </div>
                                </div>
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
                    type="button"
                  >
                    <Icons.trash className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            ))}

            <Separator className="mt-4" />

            <div className="flex justify-between pt-2">
              <Button type="submit" disabled={mutation.isPending}>
                Create
              </Button>
            </div>
          </form>

          <DevTool control={form.control} />
        </Form>

        <Card className="overflow-auto h-[500px]">
          <CardHeader>
            <CardTitle>Use Template</CardTitle>
            <CardDescription>
              Select a trigger template to pre-fill the form.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex flex-col gap-2">
              {triggerTemplates.map((template) => (
                <Button
                  variant="ghost"
                  type="button"
                  asChild
                  className="w-fit h-fit"
                  key={template.id}
                  onClick={() => {
                    if (
                      form.formState.isDirty &&
                      !confirm(
                        "You have unsaved changes. Are you sure you want to continue?",
                      )
                    ) {
                      return;
                    }

                    toast({
                      title: "Template selected",
                      description: `You have selected the "${template.title}" template.`,
                    });
                    router.push(`?template=${template.id}`);
                  }}
                >
                  <Card
                    className={cx(
                      "w-full relative justify-start p-0 truncate",
                      {
                        "bg-primary/10": template.id === templateId,
                      },
                    )}
                    title={`${template.title} - ${template.description}`}
                  >
                    <CardHeader className="truncate p-4">
                      <CardTitle className="text-sm">
                        {template.title}
                      </CardTitle>
                    </CardHeader>
                  </Card>
                </Button>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardShell>
  );
}
