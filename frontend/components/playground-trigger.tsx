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
import { useForm } from "react-hook-form";
import * as z from "zod";
import { playgroundTriggerSchema } from "../lib/validations/playground";
import { useAppUsers, useRules, useSendTrigger, useTriggers } from "@/hooks";
import { useCurrentApp } from "@/lib/current-app";
import { toast } from "@/components/ui/use-toast";

type FormData = z.infer<typeof playgroundTriggerSchema>;

export function PlaygroundTriggerForm() {
  const form = useForm<FormData>({
    resolver: zodResolver(playgroundTriggerSchema),
  });
  const selectedUserId = form.watch("userId") || null;
  const [isCreating, setIsSending] = React.useState(false);

  const { currentApp } = useCurrentApp();
  const appId = currentApp?.id || 0;
  const { data: triggers } = useTriggers();
  const { data: rules } = useRules();
  const sendTriggerMutation = useSendTrigger();

  const { data: users } = useAppUsers();

  if (!triggers || !rules || !users) {
    return <div>Loading...</div>;
  }

  async function onSubmit(values: FormData) {
    console.log("submit", values);

    sendTriggerMutation.mutate(
      {
        key: values.trigger.key,
        userId: values.userId,
        params: {},
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

  // filter the rules based on the selected trigger
  console.log("rules", rules);

  const filteredRules = rules.filter((rule) => {
    const triggerKey = form.watch("trigger.key");
    return rule.trigger?.key === triggerKey;
  });

  console.log("filteredRules", filteredRules);

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
                              onValueChange={field.onChange}
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
                      {filteredRules?.length > 0
                        ? filteredRules.map((rule) => (
                            <li className="text-sm font-medium " key={rule.id}>
                              {rule.title}
                            </li>
                          ))
                        : "No matching rules :("}
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
