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
  RuleGroupTypeAny,
} from "react-querybuilder";
import "react-querybuilder/dist/query-builder.css";
import { QueryBuilderDnD } from "@react-querybuilder/dnd";
import * as ReactDnD from "react-dnd";
import * as ReactDndHtml5Backend from "react-dnd-html5-backend";
import { customRuleProcessorCEL } from "@/app/(app)/(app-protected)/rules/new/querybuilder/customRuleProcessorCEL";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";

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

  // TODO: disallow duplicate operators for a given field
  // TODO: disable field selection when no operators are available

  const isPointsReachedTrigger = selectedTriggerKey === "points_reached";

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

  React.useEffect(() => {
    // if select points_reached, force repeatability as once_per_user
    // TODO: handle built-in triggers more gracefully and maybe migrate to useFieldArray for conditions
    if (isPointsReachedTrigger) {
      console.log("Force once_per_user");
      form.setValue("repeatability", "once_per_user");
      // TODO: fix blurring of input when setting query
    } else {
      setQuery({ combinator: "and", rules: [] });
    }
  }, [form, isPointsReachedTrigger, selectedTrigger]);

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
      conditionExpression:
        query.rules.length === 0
          ? null
          : getFormattedCELExpression(query, fields),
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

  const rulesModificationDisabled =
    isPointsReachedTrigger && query.rules.length === 1;
  return (
    <Card>
      <CardHeader>
        <CardTitle>Create Rule</CardTitle>
      </CardHeader>
      <CardContent>
        {/*  Config Trigger */}
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="w-2/3 space-y-8"
          >
            <div className="space-y-4">
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
            </div>
            <Separator />

            <div className="space-y-4">
              <div className="space-y-4 my-">
                <SectionTitle number={1} title="When" />
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
              </div>
            </div>
            <Separator />

            <div
              className={cn("space-y-4", {
                "opacity-50 cursor-not-allowed":
                  !selectedTrigger || selectedTrigger.fields?.length === 0,
              })}
            >
              {/*  Config Condition */}
              <SectionTitle number={2} title="If" isOptional />

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
                    rulesModificationDisabled
                      ? {
                          addRuleAction: () => null,
                          addGroupAction: () => null,
                          combinatorSelector: () => null,
                          removeRuleAction: () => null,
                          removeGroupAction: () => null,
                        }
                      : isPointsReachedTrigger
                        ? {
                            addGroupAction: () => null,
                            combinatorSelector: () => null,
                            removeRuleAction: () => null,
                            removeGroupAction: () => null,
                          }
                        : undefined
                  }
                  controlClassnames={
                    rulesModificationDisabled
                      ? undefined
                      : { queryBuilder: "queryBuilder-branches" }
                  }
                />
              </QueryBuilderDnD>
              {query.rules.length !== 0 && (
                <>
                  <h4>Condition Expression</h4>
                  <pre>
                    <code>{getFormattedCELExpression(query, fields)}</code>
                  </pre>
                </>
              )}
            </div>
            <Separator />

            <div className="space-y-4">
              {/*  Config Action */}
              <SectionTitle number={3} title="Then" />

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
            </div>
            <Separator />

            <div className="space-y-4">
              <SectionTitle title="More settings" />
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
            </div>

            <Button type="submit">Submit</Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}

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

function getFormattedCELExpression(query: RuleGroupTypeAny, fields: Field[]) {
  return formatQuery(query, {
    format: "cel",
    fields,
    parseNumbers: true,
    ruleProcessor: customRuleProcessorCEL,
  });
}

interface SectionTitleProps {
  title: string;
  number?: number;
  isOptional?: boolean;
}

function SectionTitle({ number, title, isOptional }: SectionTitleProps) {
  return (
    <div className="flex items-center space-x-2">
      {number && (
        <span className="h-8 w-8 bg-secondary rounded-full inline-flex items-center justify-center text-sm font-semibold">
          {number}
        </span>
      )}
      <h3 className="text-lg font-semibold">{title}</h3>
      {isOptional && <span className="text-muted-foreground">(Optional)</span>}
    </div>
  );
}
