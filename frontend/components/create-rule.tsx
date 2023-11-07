"use client";
import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import * as React from "react";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { ruleCreateSchema } from "../lib/validations/rule";

import { useParams } from "next/navigation";
import { RuleCreateButton } from "./rule-create-button";
import { DialogHeader } from "./ui/dialog";
import { toast } from "./ui/use-toast";

interface Trigger {
  title: string;
  description: string;
  key: string;
  id: number;
}

interface Action {
  title: string;
  description: string;
  key: string;
  id: number;
  jsonSchema: Record<string, unknown>;
}

type FormData = z.infer<typeof ruleCreateSchema>;
export function CreateRuleForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(ruleCreateSchema),
    defaultValues: {
      name: "New rule",
      description: "New rule",
      trigger: {
        key: "share_activity",
      },
    },
  });
  const [isCreating, setIsCreating] = React.useState(false);
  const [isOpen, setIsOpen] = React.useState(false);
  const triggers = useTriggers();
  const actions = useActions();
  // console.log(triggers);
  const { id }: { id: string } = useParams();

  function useTriggers() {
    const [triggers, setTriggers] = React.useState<Trigger[]>([]);

    React.useEffect(() => {
      async function fetchTriggers() {
        const response = await fetch(
          `http://localhost:8080/api/activities/${id}/triggers`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
            },
            next: {
              tags: ["triggers"],
            },
          }
        );

        if (!response?.ok) {
          return toast({
            title: "Something went wrong.",
            description: "Your activity was not created. Please try again.",
            variant: "destructive",
          });
        }

        const triggers: Trigger[] = await response.json();
        setTriggers(triggers);
      }

      fetchTriggers();
    }, []);

    return triggers;
  }

  function useActions() {
    const [actions, setActions] = React.useState<Action[]>([]);

    React.useEffect(() => {
      async function fetchActions() {
        const response = await fetch(
          `http://localhost:8080/api/activities/actions`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
            },
            next: {
              tags: ["actions"],
            },
          }
        );

        if (!response?.ok) {
          return toast({
            title: "Something went wrong.",
            description: "Your activity was not created. Please try again.",
            variant: "destructive",
          });
        }

        const actions: Action[] = await response.json();
        setActions(actions);
      }

      fetchActions();
    }, []);

    return actions;
  }

  async function onSubmit(values: FormData) {
    console.log("submit", values);
    setIsCreating(true);

    const response = await fetch(
      `http://localhost:8080/api/activities/${id}/rules`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(values),
      }
    );

    setIsCreating(false);
    setIsOpen(false);

    if (!response?.ok) {
      return toast({
        title: "Something went wrong.",
        description: "Your rule was not created. Please try again.",
        variant: "destructive",
      });
    }

    toast({
      title: "Activity created.",
      description: "Your rule was created successfully.",
    });

    router.refresh();
  }

  return (
    <>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <RuleCreateButton />
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create rule</CardTitle>
          </DialogHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Name</FormLabel>
                    <FormControl>
                      <Input placeholder="Name of your rule" {...field} />
                    </FormControl>{" "}
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
                        placeholder="Description of your rule"
                        {...field}
                      />
                    </FormControl>{" "}
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
                              value={trigger.key}
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

              <FormField
                control={form.control}
                name="action.key"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Action</FormLabel>
                    <FormControl>
                      <Select
                        onValueChange={field.onChange}
                        value={field.value}
                      >
                        <SelectTrigger>
                          <SelectValue
                            className="flex items-center justify-between w-full"
                            placeholder="Select an action"
                          />
                        </SelectTrigger>
                        <SelectContent className="w-full">
                          {actions.map((action) => (
                            <SelectItem
                              key={action.id}
                              value={action.key}
                              className="flex items-center justify-between w-full"
                            >
                              {action.title}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {form.watch("action.key") == "add_points" && (
                <FormField
                  control={form.control}
                  name="action.params.value"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Value</FormLabel>
                      <FormControl>
                        <Input placeholder="Value" type="number" {...field} />
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
                        <Input
                          placeholder="Achievement ID"
                          type="number"
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              )}

              <div className="flex justify-between mt-8">
                <DialogClose asChild>
                  <Button variant="outline" type="button">
                    Cancel
                  </Button>
                </DialogClose>
                <Button type="submit">Create</Button>
              </div>
            </form>
          </Form>
        </DialogContent>
      </Dialog>
    </>
  );
}
