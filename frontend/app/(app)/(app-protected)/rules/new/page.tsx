"use client";

import { Separator } from "@/components/ui/separator";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "@/components/ui/card";
import { useTriggers } from "@/hooks"; // import Link from "next/link";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

import { Button } from "@/components/ui/button";
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
import { toast } from "@/components/ui/use-toast";
import { Link as NextUILink } from "@nextui-org/react";
import Link from "next/link";
import React from "react";
import { v4 as uuidv4 } from "uuid";
import { Input } from "@/components/ui/input";
import { FieldDefinition } from "@/types/generated-types";

uuidv4(); // ⇨ '9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d'
const FormSchema = z.object({
  triggerKey: z.string({
    required_error: "Please select a trigger",
  }),
  actionKey: z.string({
    required_error: "Please select an action",
  }),
  //   optional conditions array
  conditions: z
    .map(
      z.string().uuid(),
      z.object({
        field: z.string({
          required_error: "Please select a field",
        }),
        operator: z.string({
          required_error: "Please select an operator",
        }),
        value: z.string({
          required_error: "Please enter a value",
        }),
      }),
    )
    .optional(),
});

type FormData = z.infer<typeof FormSchema>;

type Condition<T> = {
  uuid: string;
  field: string;
  operator: string;
  value: T;
  fieldDefinition?: FieldDefinition;
  inputType?: string;
  inputProps?: any;
};

// const triggerCreateSchema = z.object({
//   title: z
//     .string({
//       required_error: "Title is required",
//     })
//     .trim()
//     .min(4, { message: "Must be between 4 and 200 characters" })
//     .max(200, { message: "Must be between 4 and 200 characters" })
//     .refine((value) => isAlphaNumericExtended(value, true), {
//       message: ALPHA_NUMERIC_EXTENDED_MESSAGE,
//     }),
//   description: z
//     .string()
//     .max(500, { message: "Must not be more than 500 characters" })
//     .optional(),
//   fields: z.array(
//     z.object({
//       key: z
//         .string()
//         .min(4, { message: "Must be between 4 and 200 characters" })
//         .max(200, { message: "Must be between 4 and 200 characters" })
//         .refine((value) => isAlphaNumericExtended(value), {
//           message: ALPHA_NUMERIC_EXTENDED_MESSAGE,
//         }),
//       type: FieldTypeEnum,
//       label: z.string().optional(),
//     }),
//   ),
// });

// type TextCondition = Condition<string>;
// type IntegerCondition = Condition<number>;

const operations = [{ label: "is", value: "is" }];
const actions = [
  {
    label: "Add points",
    key: "add_points",
  },
  {
    label: "Unlock achievement",
    key: "unlock_achievement",
  },
];

function getOperationsForField(field: FieldDefinition) {
  return operations; // TODO: filter based on field type
}

export default function Page() {
  const form = useForm<FormData>({
    resolver: zodResolver(FormSchema),
  });
  const { data: triggers } = useTriggers();
  const selectedTrigger = React.useMemo(
    () => triggers?.find((trigger) => trigger.key === form.watch("triggerKey")),
    [triggers, form.watch("triggerKey")],
  );

  const [conditions, setConditions] = React.useState<Condition<any>[]>([]);
  // TODO: disallow duplicate operators for a given field
  // TODO: disable field selection when no operators are available

  React.useEffect(() => {
    if (selectedTrigger) {
      setConditions([]);
    }
  }, [selectedTrigger]);

  React.useEffect(() => {
    console.log("conditions", conditions);
  }, [conditions]);

  function addCondition() {
    setConditions((prev) => [
      ...prev,
      {
        uuid: uuidv4(),
        field: "",
        operator: "",
        value: "",
      },
    ]);
  }

  function setConditionField(uuid: string, field: string) {
    const fieldDefinition = selectedTrigger?.fields?.find(
      (f) => f.key === field,
    )!;
    const inputType = fieldDefinition.type === "integer" ? "number" : "text";
    const inputProps = fieldDefinition.type === "integer" ? { step: 1 } : {};
    setConditions((prev) =>
      prev.map((prevCondition) =>
        prevCondition.uuid === uuid
          ? {
              uuid,
              operator: "",
              value: "", // TODO: evaluate whether we should clear the value when the field changes
              field,
              fieldDefinition,
              inputType,
              inputProps,
            }
          : prevCondition,
      ),
    );
  }

  function setConditionOperator(uuid: string, operator: string) {
    setConditions((prev) =>
      prev.map((prevCondition) =>
        prevCondition.uuid === uuid
          ? { ...prevCondition, operator }
          : prevCondition,
      ),
    );
  }

  function setConditionValue(uuid: string, value: string) {
    setConditions((prev) =>
      prev.map((prevCondition) =>
        prevCondition.uuid === uuid
          ? { ...prevCondition, value }
          : prevCondition,
      ),
    );
  }

  function onSubmit(data: FormData) {
    toast({
      title: "You submitted the following values:",
      description: (
        <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
          <code className="text-white">{JSON.stringify(data, null, 2)}</code>
        </pre>
      ),
    });
  }

  if (!triggers) {
    return <div>Loading...</div>;
  }

  console.log("selectedTrigger", selectedTrigger);

  return (
    <Card>
      <CardHeader>
        {/*<CardTitle>Create Rule</CardTitle>*/}
        {/*<CardDescription>Create Rule</CardDescription>*/}
      </CardHeader>
      <CardContent>
        {/*  Config Trigger */}
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="w-2/3 space-y-6"
          >
            <h3 className="text-lg font-semibold">When</h3>
            <FormField
              control={form.control}
              name="triggerKey"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Trigger</FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Select a trigger" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {triggers?.map((trigger) => (
                        <SelectItem
                          key={trigger.id}
                          value={trigger.key!!}
                          className="flex items-center justify-between w-full"
                        >
                          {trigger.title}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormDescription>
                    You can manage triggers in{" "}
                    <Link href="/triggers">
                      <NextUILink size="sm" color={"primary"}>
                        Triggers
                      </NextUILink>
                    </Link>
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/*  Config Condition */}
            <h3 className="text-lg font-semibold">If</h3>
            <div className="flex items-center space-x-2">
              <Button
                onClick={addCondition}
                disabled={
                  !selectedTrigger || selectedTrigger.fields?.length === 0
                }
              >
                Add condition
              </Button>
              <Separator />
            </div>
            <div className="space-y-4">
              {conditions.map((condition) => (
                <div
                  key={condition.uuid}
                  className="flex items-center space-x-2"
                >
                  <Select
                    value={condition.field}
                    onValueChange={(value) =>
                      setConditionField(condition.uuid, value)
                    }
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select a field" />
                    </SelectTrigger>
                    <SelectContent>
                      {selectedTrigger?.fields?.map((field) => {
                        return (
                          <SelectItem
                            key={field.key}
                            value={field.key!!}
                            className="flex items-center justify-between w-full"
                          >
                            {field.label || field.key}
                          </SelectItem>
                        );
                      })}
                    </SelectContent>
                  </Select>

                  <Select
                    onValueChange={(value) =>
                      setConditionOperator(condition.uuid, value)
                    }
                    value={condition.operator}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select an operator" />
                    </SelectTrigger>
                    <SelectContent>
                      {operations.map((operation) => (
                        <SelectItem
                          key={operation.value}
                          value={operation.value}
                          className="flex items-center justify-between w-full"
                        >
                          {operation.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>

                  <Input
                    value={condition.value}
                    onChange={(e) =>
                      setConditionValue(condition.uuid, e.target.value)
                    }
                    type={condition.inputType}
                    {...condition.inputProps}
                  />
                </div>
              ))}
            </div>

            {/*  Config Action */}
            <h3 className="text-lg font-semibold">Then</h3>
            <FormField
              control={form.control}
              name="actionKey"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Action</FormLabel>
                  <Select onValueChange={field.onChange} value={field.value}>
                    <SelectTrigger>
                      <SelectValue
                        className="flex items-center justify-between w-full"
                        placeholder="Select an action"
                      />
                    </SelectTrigger>
                    <SelectContent className="w-full">
                      {actions.map((action) => (
                        <SelectItem
                          key={action.key}
                          value={action.key}
                          className="flex items-center justify-between w-full"
                        >
                          {action.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit">Submit</Button>
          </form>
        </Form>
      </CardContent>
      <CardFooter>
        <p>Card Footer</p>
      </CardFooter>
    </Card>
  );
}
