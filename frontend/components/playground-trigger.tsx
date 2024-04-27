"use client";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Form,
  FormControl,
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
import { zodResolver } from "@hookform/resolvers/zod";
import * as React from "react";
import { useFieldArray, useForm } from "react-hook-form";
import * as z from "zod";
import {
  parseArrayOfStringIntegers,
  parseArrayOfStrings,
  playgroundTriggerSchema,
} from "../lib/validations/playground";
import {
  useAppUsers,
  useDryTrigger,
  useRules,
  useSendTrigger,
  useTriggers,
} from "@/hooks";
import { useCurrentApp } from "@/lib/current-app";
import { toast } from "@/components/ui/use-toast";
import { Input } from "@/components/ui/input";
import { FieldDefinition, RuleResponse } from "@/types/generated-types";
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { resolveBasePath } from "@/lib/base-path";
import { useDebounceCallback } from "usehooks-ts";
import { KeyText } from "@/app/(app)/(app-protected)/triggers/components/columns";
import { Icons } from "./icons";
import { Separator } from "@/components/ui/separator";
import {
  getFieldTypeExample,
  getFieldTypeLabel,
} from "@/app/(app)/(app-protected)/triggers/shared";
import { RuleAction } from "@/app/(app)/(app-protected)/rules/components/columns";

type FormData = z.infer<typeof playgroundTriggerSchema>;

type SendTriggerParams = Record<string, string | string[] | number | number[]>;

export function PlaygroundTriggerForm() {
  const form = useForm<FormData>({
    resolver: zodResolver(playgroundTriggerSchema),
    defaultValues: {
      trigger: {
        key: "",
      },
      userId: "",
      params: [],
    },
  });

  const watchTriggerKey = form.watch("trigger.key");
  const watchUserId = form.watch("userId");

  const selectedUserId = watchUserId || null;
  const [isCreating, setIsSending] = React.useState(false);

  const { currentApp } = useCurrentApp();
  const appId = currentApp?.id || 0;
  const { data: triggers } = useTriggers();
  const { data: rules } = useRules();
  const sendTriggerMutation = useSendTrigger();
  const selectedTrigger = triggers?.find(
    (trigger) => trigger.key === watchTriggerKey,
  );
  const triggerFields = selectedTrigger?.fields || [];
  const { data: users } = useAppUsers();

  const triggerParams = useFieldArray({
    control: form.control,
    name: "params",
  });

  const [dryTriggerReferencingRules, setDryTriggerReferencingRules] =
    React.useState<RuleResponse[]>([]);
  const dryTrigger = useDryTrigger();

  function refreshDryTriggerResult() {
    console.log("refreshDryTriggerResult");
    const parseResult = playgroundTriggerSchema.safeParse(form.getValues());

    if (parseResult.success) {
      const { data } = parseResult;
      const dryTriggerRequest = {
        key: data.trigger.key,
        userId: data.userId,
        params: data.params.reduce((acc, param) => {
          const triggerField = triggerFields.find(
            (field) => field.key === param.key,
          );

          if (!triggerField) {
            return acc;
          }

          if (
            triggerField.type === "text" &&
            typeof param.value === "string" &&
            param.value.length !== 0
          ) {
            acc[param.key] = param.value;
          } else if (
            triggerField.type === "textSet" &&
            typeof param.value === "string" &&
            param.value.length !== 0
          ) {
            acc[param.key] = parseArrayOfStrings(param.value);
          } else if (
            triggerField.type === "integer" &&
            typeof param.value !== "string"
          ) {
            acc[param.key] = param.value;
          } else if (
            triggerField.type === "integerSet" &&
            typeof param.value === "string"
          ) {
            acc[param.key] = parseArrayOfStringIntegers(param.value);
          }
          return acc;
        }, {} as SendTriggerParams),
      };

      if (
        dryTriggerRequest.key.length != 0 &&
        dryTriggerRequest.userId.length != 0
      ) {
        console.log("dry trigger request is valid");
        dryTrigger.mutate(dryTriggerRequest, {
          onSuccess: (data) => {
            setDryTriggerReferencingRules(data);
          },
        });
      }
    } else {
      console.log("dry trigger request not valid");
      console.log(parseResult.error.errors);
      setDryTriggerReferencingRules([]);
    }
  }

  const debouncedRefreshDryTriggerResult = useDebounceCallback(
    refreshDryTriggerResult,
    500,
    {
      trailing: true,
    },
  );

  React.useEffect(() => {
    debouncedRefreshDryTriggerResult();
  }, [watchTriggerKey, watchUserId]);

  if (!triggers || !rules || !users) {
    return <div>Loading...</div>;
  }

  function getRequest(values: FormData) {
    return {
      key: values.trigger.key,
      userId: values.userId,
      params: values.params.reduce((acc, param) => {
        const triggerField = triggerFields.find(
          (field) => field.key === param.key,
        );

        if (!triggerField) {
          return acc;
        }

        if (
          triggerField.type === "text" &&
          typeof param.value === "string" &&
          param.value.length !== 0
        ) {
          acc[param.key] = param.value;
        } else if (
          triggerField.type === "textSet" &&
          typeof param.value === "string" &&
          param.value.length !== 0
        ) {
          acc[param.key] = parseArrayOfStrings(param.value);
        } else if (
          triggerField.type === "integer" &&
          typeof param.value !== "string"
        ) {
          acc[param.key] = param.value;
        } else if (
          triggerField.type === "integerSet" &&
          typeof param.value === "string"
        ) {
          acc[param.key] = parseArrayOfStringIntegers(param.value);
        }
        return acc;
      }, {} as SendTriggerParams),
    };
  }

  async function onSubmit(values: FormData) {
    console.log("submit", values);

    const request = getRequest(values);

    sendTriggerMutation.mutate(request, {
      onSuccess: () => {
        toast({
          title: "Trigger sent",
          description: "Trigger sent successfully",
        });
      },
      // TODO: handle error
    });
  }

  function addParam(triggerField: FieldDefinition) {
    if (!triggerField.key) {
      return;
    }

    if (triggerField.type === "text") {
      triggerParams.append({
        key: triggerField.key,
        type: triggerField.type,
        value: "",
      });
    }

    if (triggerField.type === "integer") {
      triggerParams.append({
        key: triggerField.key,
        type: triggerField.type,
        value: 0,
      });
    }

    if (triggerField.type === "integerSet") {
      triggerParams.append({
        key: triggerField.key,
        type: triggerField.type,
        value: "",
      });
    }

    if (triggerField.type === "textSet") {
      triggerParams.append({
        key: triggerField.key,
        type: triggerField.type,
        value: "",
      });
    }
  }

  function changeTrigger(callback: (value: string) => void) {
    return function (value: string) {
      if (form.getValues("params").length > 0) {
        if (
          !confirm(
            "Changing the trigger will reset the params. Are you sure you want to continue?",
          )
        ) {
          return;
        }
      }

      form.resetField("params");
      callback(value);
    };
  }

  function isParamUsed(key: string) {
    return triggerParams.fields.some((field) => field.key === key);
  }

  return (
    <>
      <div className="flex gap-4">
        <Card className="w-2/3 self-start">
          <CardHeader>
            <CardTitle>Send trigger</CardTitle>
          </CardHeader>
          <CardContent>
            <div>
              <div>
                <Form {...form}>
                  <form onSubmit={form.handleSubmit(onSubmit)}>
                    <FormField
                      control={form.control}
                      name="userId"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>User</FormLabel>
                          <FormControl>
                            <Select
                              onValueChange={field.onChange}
                              value={field.value}
                            >
                              <SelectTrigger>
                                <SelectValue
                                  className="flex items-center justify-between w-full"
                                  placeholder="Select a user"
                                />
                              </SelectTrigger>
                              <SelectContent className="w-full">
                                <SelectGroup className="overflow-y-auto max-h-[20rem]">
                                  {users.map((member) => (
                                    <SelectItem
                                      key={member.userId}
                                      value={String(member.userId) || ""}
                                      className="flex items-center justify-between w-full"
                                    >
                                      {member.displayName}
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

                    <FormField
                      control={form.control}
                      name="trigger.key"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Trigger</FormLabel>
                          <FormControl>
                            <Select
                              onValueChange={changeTrigger(field.onChange)}
                              value={field.value}
                            >
                              <SelectTrigger>
                                <SelectValue
                                  className="flex items-center justify-between w-full"
                                  placeholder="Select a trigger"
                                />
                              </SelectTrigger>
                              <SelectContent className="w-full">
                                <SelectGroup className="overflow-y-auto max-h-[20rem]">
                                  {triggers.map((trigger) => (
                                    <SelectItem
                                      key={trigger.id}
                                      value={trigger.key?.toString() || ""}
                                      className="flex items-center justify-between w-full"
                                    >
                                      {trigger.title}
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

                    {/*Params: Select from possible trigger fields (triggerFields)*/}
                    <Separator className="mt-4" />

                    <div className="mt-4">
                      {selectedTrigger && (
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button
                              variant="secondary"
                              className="ml-auto"
                              disabled={
                                triggerFields.length ===
                                form.getValues("params").length
                              }
                            >
                              <Icons.add className="mr-2 h-4 w-4" />
                              Add a param
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            {triggerFields.map((field) => {
                              if (!field.key) {
                                return null;
                              }

                              return (
                                <DropdownMenuCheckboxItem
                                  key={field.key}
                                  checked={isParamUsed(field.key)}
                                  disabled={isParamUsed(field.key)}
                                  onCheckedChange={(value) => {
                                    if (!value) {
                                      return;
                                    }
                                    addParam(field);
                                  }}
                                >
                                  <KeyText variant="outline" text={field.key} />
                                  <div className="text-muted-foreground ml-2">
                                    {getFieldTypeLabel(field.type)} - Ex.{" "}
                                    {getFieldTypeExample(field.type)}
                                  </div>
                                </DropdownMenuCheckboxItem>
                              );
                            })}
                          </DropdownMenuContent>
                        </DropdownMenu>
                      )}
                    </div>

                    {triggerParams.fields.map((param, index) => {
                      const triggerField = triggerFields.find(
                        (field) => field.key === param.key,
                      );

                      return (
                        <div
                          key={param.id}
                          className="mt-4 flex items-end space-x-2"
                        >
                          <FormField
                            control={form.control}
                            name={`params.${index}.value`}
                            render={({ field }) => (
                              <div className="flex space-x-2 items-center">
                                <FormLabel>
                                  <div className="flex flex-col gap-1 min-w-[200px]">
                                    <KeyText
                                      variant="outline"
                                      text={param.key}
                                    />
                                    <div className="text-muted-foreground">
                                      {getFieldTypeLabel(triggerField?.type)} -
                                      Ex.{" "}
                                      {getFieldTypeExample(triggerField?.type)}
                                    </div>
                                  </div>
                                </FormLabel>
                                <FormControl>
                                  <Input
                                    {...field}
                                    type={
                                      triggerField?.type === "integer"
                                        ? "number"
                                        : "text"
                                    }
                                    onChange={(e) => {
                                      field.onChange(e);
                                      // TODO: debouncing does not work for input's onChange event
                                      debouncedRefreshDryTriggerResult();
                                    }}
                                  />
                                </FormControl>
                                <FormMessage />
                              </div>
                            )}
                          />

                          <div className="flex items-center justify-end">
                            <Button
                              variant="outline"
                              onClick={() => triggerParams.remove(index)}
                            >
                              Remove
                            </Button>
                          </div>
                        </div>
                      );
                    })}

                    <Separator className="mt-4" />

                    <div className="mt-4">
                      <Button disabled={isCreating}>Send trigger</Button>
                    </div>
                  </form>
                </Form>

                <div className="my-6  ">
                  <div>
                    <p className="text-muted-foreground mb-2">
                      Will trigger these rules
                    </p>
                    {(dryTriggerReferencingRules?.length > 0 && (
                      <ul className="space-y-1 list-disc list-inside">
                        {dryTriggerReferencingRules.map((rule) => (
                          <li className="text-sm font-medium " key={rule.id}>
                            {rule.title}
                            <RuleAction
                              rule={rule}
                              className="pl-10 pt-1 pb-2 text-sm"
                            />
                          </li>
                        ))}
                      </ul>
                    )) || (
                      <p className="text-sm">No rules will be triggered.</p>
                    )}
                  </div>
                </div>
              </div>
            </div>

            <div>
              <p className="text-muted-foreground mb-2">Raw Request</p>
              <pre className="text-sm bg-primary-foreground p-4 rounded-lg">
                {JSON.stringify(getRequest(form.getValues()), null, 2)}
              </pre>
            </div>
          </CardContent>
        </Card>

        {selectedUserId && (
          <iframe
            src={resolveBasePath(
              `/embed/apps/${appId}/users/${selectedUserId}`,
            )}
            className="overflow-hidden border-none sticky top-0 w-[400px] h-[600px]"
            frameBorder={0}
          />
        )}
      </div>
    </>
  );
}
