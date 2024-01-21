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
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { zodResolver } from "@hookform/resolvers/zod";
import * as React from "react";
import { useFieldArray, useForm } from "react-hook-form";
import * as z from "zod";
import { playgroundTriggerSchema } from "../lib/validations/playground";
import { useAppUsers, useRules, useSendTrigger, useTriggers } from "@/hooks";
import { useCurrentApp } from "@/lib/current-app";
import { toast } from "@/components/ui/use-toast";
import { Input } from "@/components/ui/input";
import { FieldDefinition } from "@/types/generated-types";
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

type FormData = z.infer<typeof playgroundTriggerSchema>;

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
  const selectedUserId = form.watch("userId") || null;
  const [isCreating, setIsSending] = React.useState(false);

  const { currentApp } = useCurrentApp();
  const appId = currentApp?.id || 0;
  const { data: triggers } = useTriggers();
  const { data: rules } = useRules();
  const sendTriggerMutation = useSendTrigger();
  const selectedTrigger = triggers?.find(
    (trigger) => trigger.key === form.watch("trigger.key"),
  );
  const triggerFields = selectedTrigger?.fields || [];
  const { data: users } = useAppUsers();

  const triggerParams = useFieldArray({
    control: form.control,
    name: "params",
  });

  if (!triggers || !rules || !users) {
    return <div>Loading...</div>;
  }

  async function onSubmit(values: FormData) {
    console.log("submit", values);

    sendTriggerMutation.mutate(
      {
        key: values.trigger.key,
        userId: values.userId,
        params: values.params.reduce(
          (acc, param) => {
            acc[param.key] = param.value;
            return acc;
          },
          {} as Record<string, string | number>,
        ),
      },
      {
        onSuccess: () => {
          toast({
            title: "Trigger sent",
            description: "Trigger sent successfully",
          });
        },
        // TODO: handle error
      },
    );
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

  // filter the rules based on the selected trigger
  // TODO: this should be done on the server
  const filteredRules = rules.filter((rule) => {
    // if no trigger is selected, show all rules
    if (!selectedTrigger) {
      return true;
    }

    // all params must match to the rule's conditions
    return Object.entries(rule.conditions ?? {}).every(([key, value]) => {
      const triggerParam = form
        .watch("params")
        .find((param) => param.key === key);

      if (!triggerParam) {
        return false;
      }

      return Number(triggerParam.value) === value;
    });
  });

  return (
    <>
      <div className="flex gap-4">
        <Card className="w-1/2 self-start">
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
                                {users.map((member) => (
                                  <SelectItem
                                    key={member.userId}
                                    value={String(member.userId) || ""}
                                    className="flex items-center justify-between w-full"
                                  >
                                    {member.displayName}
                                  </SelectItem>
                                ))}
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
                                {triggers.map((trigger) => (
                                  <SelectItem
                                    key={trigger.id}
                                    value={trigger.key?.toString() || ""}
                                    className="flex items-center justify-between w-full"
                                  >
                                    {trigger.title}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    {/*Params: Select from possible trigger fields (triggerFields)*/}
                    <div className="mt-4">
                      {selectedTrigger && (
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button
                              variant="outline"
                              className="ml-auto"
                              disabled={
                                triggerFields.length ===
                                form.getValues("params").length
                              }
                            >
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
                                  {field.key}
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
                                <FormLabel>{param.key}</FormLabel>
                                <FormControl>
                                  <Input
                                    {...field}
                                    type={
                                      triggerField?.type === "text"
                                        ? "text"
                                        : "number"
                                    }
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

                    <div className="mt-4">
                      <Button disabled={isCreating}>Send trigger</Button>
                    </div>
                  </form>
                </Form>

                <div className="my-6  ">
                  <div>
                    <p className="font-semibold mb-2">
                      Will trigger these rules
                    </p>
                    <ul className="space-y-1 list-disc list-inside text-muted-foreground">
                      {filteredRules?.length > 0 &&
                        filteredRules.map((rule) => (
                          <li className="text-sm font-medium " key={rule.id}>
                            {rule.title}
                          </li>
                        ))}
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <div className="w-1/2 h-[500px]">
          {selectedUserId && (
            <iframe
              src={`/embed/apps/${appId}/users/${selectedUserId}`}
              width="100%"
              height="100%"
              allowFullScreen={true}
              className="overflow-hidden border-none sticky top-0"
            />
          )}
        </div>
      </div>
    </>
  );
}
