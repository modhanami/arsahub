"use client";
import * as React from "react";

import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useRouter } from "next/navigation";
import { appCreateSchema } from "../lib/validations/app";
import { ActivityCreateButton } from "./activity-create-button";
import { toast } from "./ui/use-toast";
import { API_URL } from "../hooks/api";

type FormData = z.infer<typeof appCreateSchema>;
export function CreateAppForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(appCreateSchema),
  });
  const [isCreating, setIsCreating] = React.useState(false);
  const [isOpen, setIsOpen] = React.useState(false);

  async function onSubmit(values: FormData) {
    console.log(values);

    setIsCreating(true);

    const response = await fetch(`${API_URL}/apps`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: values.name,
        createdBy: 1, // TODO: for testing, replace with the userId from the session
      }),
    });

    setIsCreating(false);
    setIsOpen(false);

    if (!response?.ok) {
      return toast({
        title: "Something went wrong.",
        description: "Your app was not created. Please try again.",
        variant: "destructive",
      });
    }

    toast({
      title: "App created.",
      description: "Your app was created successfully.",
    });

    router.refresh();
  }

  return (
    <>
      {/* <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Create app</DialogTitle>
            <DialogDescription>
              Add a new app to start using gamiication.
            </DialogDescription>
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
                      <Input
                        placeholder="Name of your app"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <div className="flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2 mt-8">
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
      </Dialog> */}
    </>
  );
}
