"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Select, SelectTrigger, SelectValue } from "@/components/ui/select";
import { toast } from "@/components/ui/use-toast";
import React from "react";
import { Input } from "@/components/ui/input";
import {
  FieldDefinition,
  RuleResponse,
  ValidationLengths,
  ValidationMessages,
} from "@/types/generated-types";
import { isAlphaNumericExtended } from "@/lib/validations";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import { TextareaWithCounter } from "@/components/ui/textarea-with-counter";
import { useRule, useUpdateRule } from "@/hooks";
import { isApiError } from "@/api";
import { HttpStatusCode } from "axios";
import { resolveBasePath } from "@/lib/base-path";
import { useRouter } from "next/navigation";
import { DashboardHeader } from "@/components/header";
import { DashboardShell } from "@/components/shell";

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

interface UpdateRuleFormProps {
  rule: RuleResponse;
}

function getDefaultValues(rule: RuleResponse) {
  return {
    title: rule.title ?? "",
    description: rule.description ?? undefined,
  };
}

function UpdateRuleForm({ rule }: UpdateRuleFormProps) {
  const form = useForm<FormData>({
    resolver: zodResolver(FormSchema),
    defaultValues: getDefaultValues(rule),
  });
  const router = useRouter();
  const selectedTriggerKey = rule.trigger?.key;
  const updateRule = useUpdateRule();

  const isPointsReachedTrigger = selectedTriggerKey === "points_reached";

  // TODO: display conditions
  if (isPointsReachedTrigger) {
  }

  async function onSubmit(data: FormData) {
    const updatedRule = await updateRule.mutateAsync({
      ruleId: rule.id!,
      updateRequest: {
        title: data.title.trim(),
        description: data.description?.trim() ?? null,
      },
    });

    toast({
      title: "Rule updated",
      description: "Your rule was updated successfully.",
    });

    form.reset(getDefaultValues(updatedRule));
    router.push(resolveBasePath(`/rules`));
  }

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
        heading="Update Rule"
        text="Update the rule details"
        separator
      ></DashboardHeader>
      {/*  Config Trigger */}
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="max-w-2xl space-y-6"
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
          <FormItem>
            <FormLabel>Trigger</FormLabel>
            <Input
              className="flex items-center justify-between w-full"
              disabled
              value={rule.trigger?.title ?? "-"}
            />
          </FormItem>

          {/*  Config Condition */}
          <h3 className="text-lg font-semibold">If</h3>
          <div className="space-y-4">TODO: Display conditions</div>

          {/*  Config Action */}
          <h3 className="text-lg font-semibold">Then</h3>
          <FormItem>
            <FormLabel>Action</FormLabel>
            <Select>
              <SelectTrigger disabled>
                <SelectValue placeholder={rule.action ?? "-"} />
              </SelectTrigger>
            </Select>
            <FormMessage />
          </FormItem>

          {rule.action == "add_points" && (
            <FormItem>
              <FormLabel>Points</FormLabel>
              <Input
                placeholder="Points"
                type="number"
                disabled
                value={rule.actionPoints ?? 0}
              />
            </FormItem>
          )}

          {rule.action == "unlock_achievement" && (
            <FormItem>
              <FormLabel>Achievement</FormLabel>
              TODO: Display achievement
            </FormItem>
          )}

          {/*Repeatability*/}
          <FormItem>
            <FormLabel>Repeatability</FormLabel>
            <Select disabled>
              <SelectTrigger>
                <SelectValue
                  className="flex items-center justify-between w-full"
                  placeholder={rule.repeatability ?? "-"}
                />
              </SelectTrigger>
            </Select>
          </FormItem>

          <Button
            type="submit"
            disabled={updateRule.isPending || !form.formState.isDirty}
          >
            Update
          </Button>
        </form>
      </Form>
    </DashboardShell>
  );
}

export default function UpdateRulePage({
  params,
}: {
  params: { ruleId: string };
}) {
  const ruleId = Number(params.ruleId);
  const { data: rule, error } = useRule(ruleId);

  if (
    !rule ||
    (error &&
      isApiError(error) &&
      error.response?.status === HttpStatusCode.NotFound)
  ) {
    console.log("rule not found", rule, error);
    return <div>Rule not found</div>;
  }

  return <UpdateRuleForm rule={rule} />;
}
