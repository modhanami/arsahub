"use client";
import * as React from "react";

import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useForm } from "react-hook-form";
import { activityCreateSchema } from "../lib/validations/activity";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
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
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "@/components/ui/dialog";
import { toast } from "./ui/use-toast";
import { useRouter } from "next/navigation";
import { ActivityCreateButton } from "./activity-create-button";
import { API_URL, makeAppAuthHeader } from "../hooks/api";
import { useCurrentApp } from "@/lib/current-app";

type FormData = z.infer<typeof activityCreateSchema>;

export function ActivityCreateForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(activityCreateSchema),
  });
  const [isCreating, setIsCreating] = React.useState(false);
  const [isOpen, setIsOpen] = React.useState(false);
  const { currentApp } = useCurrentApp();

  async function onSubmit(values: FormData) {
    console.log(values);

    setIsCreating(true);

    const response = await fetch(`${API_URL}/activities`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...makeAppAuthHeader(currentApp),
      },
      body: JSON.stringify({
        title: values.title,
        description: values.description,
      }),
    });

    setIsCreating(false);
    setIsOpen(false);

    if (!response?.ok) {
      return toast({
        title: "Something went wrong.",
        description: "Your activity was not created. Please try again.",
        variant: "destructive",
      });
    }

    toast({
      title: "Activity created.",
      description: "Your activity was created successfully.",
    });

    router.refresh();
  }

  return (
    <>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <ActivityCreateButton />
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create activity</CardTitle>
            {/* <DialogTitle>Are you sure absolutely sure?</DialogTitle> */}
          </DialogHeader>
          {/* <CardDescription>Deploy your new activity in one-click.</CardDescription> */}
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              {/* <Card className="w-[350px]"> */}
              {/* <CardContent> */}
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Title</FormLabel>
                    <FormControl>
                      <Input placeholder="Name of your activity" {...field} />
                    </FormControl>
                    <FormDescription>
                      {/* This is your public display name. */}
                    </FormDescription>
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
                        placeholder="Description of your activity"
                        {...field}
                      />
                    </FormControl>
                    <FormDescription>
                      {/* This is your public display name. */}
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
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
