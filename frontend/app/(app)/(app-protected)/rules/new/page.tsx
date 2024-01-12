"use client";

import { Separator } from "@/components/ui/separator";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useAchievements, useCreateRule, useTriggers } from "@/hooks"; // import Link from "next/link";
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
import {
  FieldDefinition,
  RuleCreateRequest,
  ValidationLengths,
  ValidationMessages,
} from "@/types/generated-types";
import { Icons } from "@/components/icons";
import { isAlphaNumericExtended } from "@/lib/validations";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import { TextareaWithCounter } from "@/components/ui/textarea-with-counter";

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

const repeatability = [
  {
    label: "Once per user",
    value: "once_per_user",
  },
  {
    label: "Unlimited",
    value: "unlimited",
  },
];

const addPointsSchema = z.object({
  key: z.literal("add_points"),
  params: z.object({
    points: z.coerce.number(),
  }),
});

const unlockAchievementSchema = z.object({
  key: z.literal("unlock_achievement"),
  params: z.object({
    achievementId: z.coerce.number(),
  }),
});

const FormSchema = z.object({
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
  trigger: z.object({
    key: z.string({
      required_error: "Please select a trigger",
    }),
  }),
  action: z.discriminatedUnion(
    "key",
    [addPointsSchema, unlockAchievementSchema],
    {
      required_error: "Please select an action",
    },
  ),
  repeatability: z.string({
    required_error: "Please select a repeatability",
  }), // TODO: add validation, or change to enum
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

export default function Page() {
  const form = useForm<FormData>({
    resolver: zodResolver(FormSchema),
  });
  const { data: triggers } = useTriggers();
  const { data: achievements } = useAchievements({
    enabled: form.watch("action.key") === "unlock_achievement",
  });
  const selectedTriggerKey = form.watch("trigger.key");
  const selectedTrigger = React.useMemo(() => {
    return triggers?.find((trigger) => trigger.key === selectedTriggerKey);
  }, [triggers, selectedTriggerKey]);
  const createRule = useCreateRule();

  const [conditions, setConditions] = React.useState<Condition<any>[]>([]);
  const [conditionErrors, setConditionErrors] = React.useState<
    Record<string, string>
  >({}); // UUID -> error message
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

  function removeCondition(uuid: string) {
    setConditions((prev) =>
      prev.filter((condition) => condition.uuid !== uuid),
    );
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
    for (const condition of conditions) {
      try {
        console.log("condition", condition);
        conditionErrors[condition.uuid] = "";

        if (condition.field === "") {
          conditionErrors[condition.uuid] = "Please select a field";
        } else if (condition.operator === "") {
          conditionErrors[condition.uuid] = "Please select an operator";
        } else if (condition.value.trim() === "") {
          conditionErrors[condition.uuid] = "Please enter a value";
        }

        if (condition.fieldDefinition?.type === "integer") {
          condition.value = parseInt(condition.value.trim());
        }
      } catch (e) {
        console.error(e);
        conditionErrors[condition.uuid] = "Please enter a valid value";

        toast({
          title: "Error",
          description: "FIXME: error message",
          variant: "destructive",
        });

        return;
      }
    }

    const finalConditions = conditions.reduce(
      (acc, condition) => {
        acc[condition.field] = condition.value;
        return acc;
      },
      {} as Record<string, any>,
    );

    const payload: RuleCreateRequest = {
      ...data,
      trigger: {
        key: data.trigger.key,
        params: null, // TODO: support trigger params or remove
      },
      title: data.title.trim(),
      description: data.description?.trim() || null,
      conditions: finalConditions,
    };

    createRule.mutate(payload);
    console.log("payload", payload);

    toast({
      title: "You submitted the following values:",
      description: (
        <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
          <code className="text-white">{JSON.stringify(payload, null, 2)}</code>
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
        <CardTitle>Create Rule</CardTitle>
        {/*<CardDescription>Create Rule</CardDescription>*/}
      </CardHeader>
      <CardContent>
        {/*  Config Trigger */}
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="w-2/3 space-y-6"
          >
            {/*Title*/}
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Title</FormLabel>
                  <FormControl>
                    <InputWithCounter
                      placeholder="Title"
                      maxLength={ValidationLengths.TITLE_MAX_LENGTH}
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/*Description*/}
            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <TextareaWithCounter
                      placeholder="Description"
                      maxLength={ValidationLengths.DESCRIPTION_MAX_LENGTH}
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <h3 className="text-lg font-semibold">When</h3>
            <FormField
              control={form.control}
              name="trigger.key"
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
                type="button"
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

                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => removeCondition(condition.uuid)}
                  >
                    <Icons.trash className="h-4 w-4" />
                  </Button>

                  <FormMessage>{conditionErrors[condition.uuid]}</FormMessage>
                </div>
              ))}
            </div>

            {/*  Config Action */}
            <h3 className="text-lg font-semibold">Then</h3>
            <FormField
              control={form.control}
              name="action.key"
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

            {/*Action params*/}
            {form.watch("action.key") == "add_points" && (
              <FormField
                control={form.control}
                name="action.params.points"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Points</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Points"
                        type="number"
                        {...field}
                        step={1}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}

            {form.watch("action.key") == "unlock_achievement" && (
              <FormField
                control={form.control}
                name="action.params.achievementId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Achievement</FormLabel>
                    <FormControl>
                      <Select
                        onValueChange={field.onChange}
                        value={field.value?.toString()}
                      >
                        <SelectTrigger>
                          <SelectValue
                            className="flex items-center justify-between w-full"
                            placeholder="Select an achievement"
                          />
                        </SelectTrigger>
                        <SelectContent className="w-full">
                          {achievements?.map((achievement) => (
                            <SelectItem
                              key={achievement.achievementId}
                              value={achievement.achievementId?.toString()}
                              className="flex items-center justify-between w-full"
                            >
                              {achievement.title}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}

            {/*Repeatability*/}
            <FormField
              control={form.control}
              name="repeatability"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Repeatability</FormLabel>
                  <FormControl>
                    <Select onValueChange={field.onChange} value={field.value}>
                      <SelectTrigger>
                        <SelectValue
                          className="flex items-center justify-between w-full"
                          placeholder="Select a repeatability"
                        />
                      </SelectTrigger>
                      <SelectContent className="w-full">
                        {repeatability.map((repeatability) => (
                          <SelectItem
                            key={repeatability.value}
                            value={repeatability.value}
                            className="flex items-center justify-between w-full"
                          >
                            {repeatability.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </FormControl>
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
