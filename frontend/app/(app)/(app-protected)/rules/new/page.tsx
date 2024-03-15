"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
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
import React, { useMemo } from "react";
import { Input } from "@/components/ui/input";
import {
  RuleCreateRequest,
  ValidationLengths,
  ValidationMessages,
} from "@/types/generated-types";
import { isAlphaNumericExtended } from "@/lib/validations";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import { TextareaWithCounter } from "@/components/ui/textarea-with-counter";
import { resolveBasePath } from "@/lib/base-path";
import {
  defaultOperators,
  Field,
  FlexibleOptionList,
  formatQuery,
  FullOperator,
  QueryBuilder,
  RuleGroupType,
  RuleType,
} from "react-querybuilder";
import "react-querybuilder/dist/query-builder.css";
import { QueryBuilderDnD } from "@react-querybuilder/dnd";
import * as ReactDnD from "react-dnd";
import * as ReactDndHtml5Backend from "react-dnd-html5-backend";
import { customRuleProcessorCEL } from "@/app/(app)/(app-protected)/rules/new/querybuilder/customRuleProcessorCEL";

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

export default function Page() {
  const form = useForm<FormData>({
    resolver: zodResolver(FormSchema),
  });
  const { data: triggers } = useTriggers({ withBuiltIn: true });
  const { data: achievements } = useAchievements({
    enabled: form.watch("action.key") === "unlock_achievement",
  });
  const selectedTriggerKey = form.watch("trigger.key");
  const selectedTrigger = React.useMemo(() => {
    return triggers?.find((trigger) => trigger.key === selectedTriggerKey);
  }, [triggers, selectedTriggerKey]);
  const createRule = useCreateRule();

  const [query, setQuery] = React.useState<RuleGroupType>({
    combinator: "and",
    rules: [],
  });
  const [conditionExpression, setConditionExpression] =
    React.useState<string>("");

  // TODO: disallow duplicate operators for a given field
  // TODO: disable field selection when no operators are available

  const isPointsReachedTrigger = selectedTriggerKey === "points_reached";

  React.useEffect(() => {
    // if select points_reached, force repeatability as once_per_user
    // TODO: handle built-in triggers more gracefully and maybe migrate to useFieldArray for conditions
    if (isPointsReachedTrigger) {
      console.log("Force once_per_user");
      form.setValue("repeatability", "once_per_user");
      // set to having one condition of 'points' is 'is' 'value'
      setQuery({
        combinator: "and",
        rules: [
          {
            field: "points",
            operator: "=",
            value: "",
          },
        ],
      });
    } else {
      setQuery({ combinator: "and", rules: [] });
    }
  }, [selectedTrigger]);

  const isRepeatabilityDisabled = isPointsReachedTrigger;

  const fields: Field[] = useMemo(() => {
    return (
      selectedTrigger?.fields?.map((field) => ({
        name: field.key!,
        label: field.label || field.key!,
        dataType: field.type!,
        inputType: field.type === "integer" ? "number" : "text",
      })) || []
    );
  }, [selectedTrigger]);

  const onlyValueMode = isPointsReachedTrigger;

  const operatorFactory = isPointsReachedTrigger
    ? () => defaultOperators.filter((op) => ["="].includes(op.name))
    : getOperators;

  function onSubmit(data: FormData) {
    const payload: RuleCreateRequest = {
      ...data,
      trigger: {
        key: data.trigger.key,
        params: null, // TODO: support trigger params or remove
      },
      title: data.title.trim(),
      description: data.description?.trim() || null,
      conditionExpression: conditionExpression,
      conditions: null,
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
                    <NextUILink
                      size="sm"
                      color={"primary"}
                      href={resolveBasePath("/triggers")}
                    >
                      Triggers
                    </NextUILink>
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/*  Config Condition */}
            <h3 className="text-lg font-semibold">If</h3>
            <QueryBuilderDnD dnd={{ ...ReactDnD, ...ReactDndHtml5Backend }}>
              <QueryBuilder
                disabled={
                  !selectedTrigger || selectedTrigger.fields?.length === 0
                }
                fields={fields}
                query={query}
                getOperators={operatorFactory}
                onQueryChange={setQuery}
                resetOnFieldChange={false}
                controlElements={
                  onlyValueMode
                    ? {
                        addRuleAction: () => null,
                        addGroupAction: () => null,
                        combinatorSelector: () => null,
                        removeRuleAction: () => null,
                        removeGroupAction: () => null,
                      }
                    : undefined
                }
                controlClassnames={
                  onlyValueMode
                    ? undefined
                    : { queryBuilder: "queryBuilder-branches" }
                }
              />
            </QueryBuilderDnD>
            <h4>Query</h4>
            <pre>
              <code>{getFormattedCELExpression(query, fields)}</code>
            </pre>

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
                    <Select
                      onValueChange={field.onChange}
                      value={field.value}
                      disabled={isRepeatabilityDisabled}
                    >
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
    </Card>
  );
}

// const fields: Field[] = [
//   { name: "firstName", label: "First Name" },
//   { name: "lastName", label: "Last Name" },
// ];
//
// const initialQuery: RuleGroupType = {
//   combinator: "and",
//   rules: [
//     {
//       field: "firstName",
//       operator: "beginsWith",
//       value: "Stev",
//     },
//     {
//       field: "lastName",
//       operator: "in",
//       value: "Vai,Vaughan",
//     },
//   ],
// };

function getOperators(
  fieldName: string,
  { fieldData }: { fieldData: Field },
): FlexibleOptionList<FullOperator> {
  switch (fieldData.dataType) {
    case "text":
      return [
        { name: "=", label: "equals" },
        ...defaultOperators.filter((op) =>
          ["contains", "beginsWith", "endsWith"].includes(op.name),
        ),
      ];
    case "integer":
      return [
        ...defaultOperators.filter((op) =>
          ["=", ">", ">=", "<", "<="].includes(op.name),
        ),
      ];
  }
  return [];
}

function getFormattedCELExpression(
  query: RuleGroupType<RuleType, string>,
  fields: Field[],
) {
  return formatQuery(query, {
    format: "cel",
    fields,
    parseNumbers: true,
    ruleProcessor: customRuleProcessorCEL,
  });
}
