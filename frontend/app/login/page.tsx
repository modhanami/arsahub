"use client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter, useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Button } from "../../components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "../../components/ui/form";
import { Input } from "../../components/ui/input";
import { useCurrentUser, useUserUuid } from "../../lib/current-user";
import { useEffect } from "react";

export default function Page() {
  const defaultRedirect = "/";
  const router = useRouter();
  const loginSchema = z.object({
    uuid: z
      .string({
        required_error: "Please enter a valid UUID",
      })
      .uuid(),
  });
  type FormData = z.infer<typeof loginSchema>;

  const { currentUser } = useCurrentUser();
  const { updateUuid } = useUserUuid();

  const form = useForm<FormData>({
    resolver: zodResolver(loginSchema),
  });

  const { control, handleSubmit, setError } = form;

  const searchParams = useSearchParams();

  useEffect(() => {
    if (currentUser) {
      router.push(defaultRedirect);
    }
  }, [currentUser, router]);

  if (currentUser) {
    return null;
  }

  async function handleLogin(data: { uuid: string }) {
    try {
      updateUuid(data.uuid);
      router.push(searchParams.get("redirect") || defaultRedirect);
    } catch (error: any) {
      console.log("Error", error);

      if (error instanceof Error) {
        setError("uuid", {
          type: "manual",
          message: error.message,
        });
      }
    }
  }

  return (
    <div className="flex justify-center w-full h-screen">
      <div className="sm:w-[350px] flex flex-col space-y-2 text-center m-auto pb-40">
        <h1 className="text-2xl font-semibold tracking-tight">
          Login to your account
        </h1>
        <p className="text-sm text-muted-foreground">
          Enter your UUID below to login to your account.
        </p>
        <Form {...form}>
          <form
            onSubmit={handleSubmit(handleLogin)}
            className="grid gap-2 pt-4"
          >
            <FormField
              control={control}
              name="uuid"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Input
                      type="text"
                      value={field.value || ""}
                      onChange={field.onChange}
                      placeholder="00000000-0000-0000-0000-000000000000"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit">Login</Button>
          </form>
        </Form>
      </div>
    </div>
  );
}
