"use client";
import * as React from "react";
import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useForm } from "react-hook-form";
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
import { API_URL, makeAppAuthHeader } from "@/hooks/api";
import { Icons } from "@/components/icons";
import { useCurrentApp } from "@/lib/current-app";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  AchievementCreateRequest,
  AchievementResponse,
} from "@/types/generated-types";
import { ApiError } from "@/types";

export const achievementCreateSchema = z.object({
  title: z
    .string({
      required_error: "Title is required",
    })
    .min(4, { message: "Must be between 4 and 200 characters" })
    .max(200, { message: "Must be between 4 and 200 characters" }),
  description: z
    .string()
    .max(500, { message: "Must not be more than 500 characters" })
    .optional(),
});

type FormData = z.infer<typeof achievementCreateSchema>;

type Props = {
  activityId: number;
};

export function AchievementCreateForm({ activityId }: Props) {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(achievementCreateSchema),
  });
  const [isOpen, setIsOpen] = React.useState(false);
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();

  type MutationData = {
    activityId: number;
    newAchievement: AchievementCreateRequest;
  };

  async function createAchievement(
    activityId: number,
    newAchievement: AchievementCreateRequest,
  ): Promise<AchievementResponse> {
    const response = await fetch(`${API_URL}/apps/achievements`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...makeAppAuthHeader(currentApp),
      },
      body: JSON.stringify(newAchievement),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    return response.json();
  }

  const mutation = useMutation<AchievementResponse, ApiError, MutationData>({
    mutationFn: ({ activityId, newAchievement }: MutationData) =>
      createAchievement(activityId, newAchievement),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["achievements", activityId] });

      toast({
        title: "Achievement created",
        description: "Your achievement was created successfully.",
      });
    },
    onError: () => {
      toast({
        title: "Something went wrong.",
        description: "Your achievement was not created. Please try again.",
        variant: "destructive",
      });
    },
  });

  async function onSubmit(values: FormData) {
    await mutation.mutateAsync({
      activityId,
      newAchievement: {
        title: values.title,
        description: values.description || null,
      },
    });

    setIsOpen(false);
    router.refresh();
  }

  return (
    <>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <Button>
            <Icons.add className="mr-2 h-4 w-4" />
            New Achievement
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create activity</CardTitle>
          </DialogHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Title</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Name of your achievement"
                        {...field}
                      />
                    </FormControl>
                    <FormDescription />
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
                        placeholder="Description of your achievement"
                        {...field}
                      />
                    </FormControl>
                    <FormDescription />
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
                <Button type="submit" disabled={mutation.isPending}>
                  Create
                </Button>
              </div>
            </form>
          </Form>
        </DialogContent>
      </Dialog>
    </>
  );
}
