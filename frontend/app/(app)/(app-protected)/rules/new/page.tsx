"use client";

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
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { toast } from "@/components/ui/use-toast";
import { Link as NextUILink } from "@nextui-org/react";
import React from "react";
import { Input } from "@/components/ui/input";
import {
  RuleCreateRequest,
  TriggerResponse,
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
  RuleType,
} from "react-querybuilder";
import "react-querybuilder/dist/query-builder.css";
import { QueryBuilderDnD } from "@react-querybuilder/dnd";
import * as ReactDnD from "react-dnd";
import * as ReactDndHtml5Backend from "react-dnd-html5-backend";
import { customRuleProcessorCEL } from "@/app/(app)/(app-protected)/rules/new/querybuilder/customRuleProcessorCEL";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";
import { useRouter } from "next/navigation";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { SectionTitle } from "@/app/(app)/(app-protected)/rules/shared";

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
  accumulatedFields: z.array(z.string()).optional(),
});

type FormData = z.infer<typeof FormSchema>;

export default function Page() {
  const form = useForm<FormData>({
    resolver: zodResolver(FormSchema),
  });
  const router = useRouter();
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

  const fields: Field[] = React.useMemo(() => {
    return (
      selectedTrigger?.fields?.map((field) => {
        const _field = {
          name: field.key!,
          label: field.key!,
          dataType: field.type!,
          inputType: field.type === "integer" ? "number" : "text",
          operators: getOperators(field.key!, field.type!, {
            isPointsReachedTrigger,
          }),
          defaultOperator: getDefaultOperator(field.type!),
        };
        console.log("_field", _field);
        return _field;
      }) || []
    );
  }, [isPointsReachedTrigger, selectedTrigger?.fields]);

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
      accumulatedFields:
        data.accumulatedFields === undefined ||
        data.accumulatedFields.length === 0
          ? null
          : data.accumulatedFields,
    };

    createRule.mutate(payload, {
      onSuccess: () => {
        router.push(resolveBasePath(`/rules`));
      },
    });

    toast({
      title: "Rule created",
      description: "Your rule was created successfully.",
    });
  }

  if (!triggers) {
    return <div>Loading...</div>;
  }

  const rulesModificationDisabled =
    isPointsReachedTrigger && query.rules.length === 1;

  const accumulatableFields = (
    (selectedTrigger && getAccumulatableFields(selectedTrigger)) ||
    []
  ).filter((field) => {
    return query.rules.some((rule) => {
      return (rule as RuleType).field === field.key;
    });
  });
  return (
    <DashboardShell>
      <Button
        type="button"
        onClick={() => router.push(resolveBasePath(`/rules`))}
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
        heading="New Rule"
        text="Create a new rule with a trigger, optional conditions, and action."
        separator
      ></DashboardHeader>
      {/*  Config Trigger */}
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="max-w-2xl space-y-8"
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
            <div className="space-y-4">
              <SectionTitle number={1} title="When" />
              <FormField
                control={form.control}
                name="trigger.key"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Trigger</FormLabel>
                    <Select
                      onValueChange={(value) => {
                        if (
                          query.rules.length > 0 &&
                          !confirm("You have unsaved changes. Continue?")
                        ) {
                          return;
                        }
                        field.onChange(value);
                      }}
                      defaultValue={field.value}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a trigger" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectGroup className="overflow-y-auto max-h-[20rem]">
                          {triggers?.map((trigger) => (
                            <SelectItem
                              key={trigger.id}
                              value={trigger.key!!}
                              className="flex items-center justify-between w-full"
                            >
                              {trigger.title}
                            </SelectItem>
                          ))}
                        </SelectGroup>
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
                resetOnOperatorChange
                resetOnFieldChange
                debugMode
                disabled={
                  !selectedTrigger || selectedTrigger.fields?.length === 0
                }
                fields={fields}
                query={query}
                onQueryChange={setQuery}
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
            {/*{accumulatableFields.length > 0 && (*/}
            {/*  <FormField*/}
            {/*    control={form.control}*/}
            {/*    name="accumulatedFields"*/}
            {/*    render={() => (*/}
            {/*      <FormItem>*/}
            {/*        <div className="m-4">*/}
            {/*          <FormLabel className="text-base">*/}
            {/*            Accumulated Fields*/}
            {/*          </FormLabel>*/}
            {/*          <FormDescription>*/}
            {/*            Select the fields you want to accumulate when this*/}
            {/*            rule is triggered.*/}
            {/*          </FormDescription>*/}
            {/*          {accumulatableFields.map((item) => (*/}
            {/*            <FormField*/}
            {/*              key={item.key}*/}
            {/*              control={form.control}*/}
            {/*              name="accumulatedFields"*/}
            {/*              render={({ field }) => {*/}
            {/*                return (*/}
            {/*                  <FormItem*/}
            {/*                    key={item.key}*/}
            {/*                    className="flex flex-row items-start space-x-3 space-y-0 my-2"*/}
            {/*                  >*/}
            {/*                    <FormControl>*/}
            {/*                      <Checkbox*/}
            {/*                        checked={field.value?.includes(item.key!)}*/}
            {/*                        onCheckedChange={(checked) => {*/}
            {/*                          return checked*/}
            {/*                            ? field.onChange([*/}
            {/*                                ...(field.value || []),*/}
            {/*                                item.key!,*/}
            {/*                              ])*/}
            {/*                            : field.onChange(*/}
            {/*                                field.value?.filter(*/}
            {/*                                  (value) => value !== item.key,*/}
            {/*                                ),*/}
            {/*                              );*/}
            {/*                        }}*/}
            {/*                      />*/}
            {/*                    </FormControl>*/}
            {/*                    <FormLabel className="font-normal">*/}
            {/*                      {item.key}*/}
            {/*                    </FormLabel>*/}
            {/*                  </FormItem>*/}
            {/*                );*/}
            {/*              }}*/}
            {/*            />*/}
            {/*          ))}*/}
            {/*        </div>*/}

            {/*        <FormMessage />*/}
            {/*      </FormItem>*/}
            {/*    )}*/}
            {/*  />*/}
            {/*)}*/}
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
                          <SelectGroup className="overflow-y-auto max-h-[20rem]">
                            {achievements?.map((achievement) => (
                              <SelectItem
                                key={achievement.achievementId}
                                value={achievement.achievementId?.toString()}
                                className="flex items-center justify-between w-full"
                              >
                                {achievement.title}
                              </SelectItem>
                            ))}
                          </SelectGroup>
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
    </DashboardShell>
  );
}

interface GetOperatorsOptions {
  isPointsReachedTrigger: boolean;
}

function getOperators(
  fieldName: string,
  fieldType: string,
  options: GetOperatorsOptions,
): FlexibleOptionList<FullOperator> {
  console.log("getOperators", fieldName, fieldType, options);
  const { isPointsReachedTrigger } = options;
  if (isPointsReachedTrigger) {
    return [{ name: "=", label: "equals" }];
  }

  switch (fieldType) {
    case "text":
      return [
        { name: "=", label: "equals" },
        ...defaultOperators.filter((op) =>
          ["contains", "beginsWith", "endsWith", "in"].includes(op.name),
        ),
      ];
    case "integer":
      return [
        ...defaultOperators.filter((op) =>
          ["=", ">", ">=", "<", "<=", "in"].includes(op.name),
        ),
      ];
    case "integerSet":
      return [
        { name: "containsAll", value: "containsAll", label: "contains all" },
      ];
    case "textSet":
      return [
        { name: "containsAll", value: "containsAll", label: "contains all" },
      ];
  }
  return [];
}

function getDefaultOperator(fieldType: string): string {
  switch (fieldType) {
    case "integerSet":
      return "containsAll";
    case "textSet":
      return "containsAll";
  }
  return "";
}

function getFormattedCELExpression(query: RuleGroupTypeAny, fields: Field[]) {
  return formatQuery(query, {
    format: "cel",
    fields,
    parseNumbers: true,
    ruleProcessor: customRuleProcessorCEL,
  });
}

function getAccumulatableFields(trigger: TriggerResponse) {
  return trigger.fields?.filter((field) => {
    return ["integerSet"].includes(field.type!);
  });
}
