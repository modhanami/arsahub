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
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "@/components/ui/dialog";
import { toast } from "./ui/use-toast";
import { isApiValidationError } from "@/api";
import { Label } from "@/components/ui/label";
import { Icons } from "@/components/icons";
import { useCreateTrigger } from "@/hooks";
import { DevTool } from "@hookform/devtools";

const FieldTypeEnum = z.enum(["Text", "Integer"] as const);
const triggerCreateSchema = z.object({
  title: z
    .string({
      required_error: "Title is required",
    })
    .trim()
    .min(4, { message: "Must be between 4 and 200 characters" })
    .max(200, { message: "Must be between 4 and 200 characters" }),
  description: z
    .string()
    .max(500, { message: "Must not be more than 500 characters" })
    .optional(),
  fields: z.array(
    z.object({
      key: z
        .string()
        .min(4, { message: "Must be between 4 and 200 characters" })
        .max(200, { message: "Must be between 4 and 200 characters" })
        .refine((value) => /[a-zA-Z0-9_-]/.test(value), {
          message:
            "Must contain only alphanumeric characters, underscores, and dashes",
        }),
      type: FieldTypeEnum,
      label: z.string().optional(),
    }),
  ),
});

function generateKeyFromTitle(title: string): string | undefined {
  const regex = /[a-zA-Z0-9_-]+/g;
  const matches = title.match(regex);

  if (matches) {
    console.log("Matches", matches);
    return matches.join("_");
  } else {
    console.log("No matches found");
    return;
  }
}

type FormData = z.infer<typeof triggerCreateSchema>;

export function TriggerCreateForm() {
  const form = useForm<FormData>({
    resolver: zodResolver(triggerCreateSchema),
    defaultValues: {
      fields: [],
    },
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
    const generatedKey = generateKeyFromTitle(values.title);
    if (!generatedKey) {
      return; // TODO: handle error
    }

    mutation.mutate(
      {
        title: values.title,
        description: values.description || "",
        key: generatedKey,
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
        },
        onError: (error, b, c) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            // TODO: handle validation errors
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
            New Trigger
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create trigger</CardTitle>
            {/* <DialogTitle>Are you sure absolutely sure?</DialogTitle> */}
          </DialogHeader>
          {/* <CardDescription>Deploy your new activity in one-click.</CardDescription> */}
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              {/* <Card className="w-[350px]"> */}
              {/* <CardContent> */}
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Title</FormLabel>
                    <FormControl>
                      <Input placeholder="Name of your trigger" {...field} />
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
                      <Input
                        placeholder="Description of your trigger"
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
                  This is the key that you will use to trigger for users in this
                  activity.
                </p>
                <Input
                  value={
                    form.watch("title") &&
                    generateKeyFromTitle(form.watch("title"))
                  }
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
                        {form.formState.touchedFields["fields"]?.[index]
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

            <DevTool control={form.control} />
          </Form>
        </DialogContent>
      </Dialog>
    </>
  );
}
