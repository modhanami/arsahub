"use client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter, useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useCurrentUser } from "../../lib/current-user";
import { Button } from "@/components/ui/button";
import { isApiError, isApiValidationError } from "@/api";
import { HttpStatusCode } from "axios";
import { useEffect } from "react";

const loginSchema = z.object({
  email: z
    .string({
      required_error: "Please enter your email address.",
    })
    .email({
      message: "Please enter a valid email address.",
    }),
  password: z
    .string({
      required_error: "Please enter your password.",
    })
    .min(8, { message: "Password must be between 8 and 50 characters." })
    .max(50, { message: "Password must be between 8 and 50 characters." }),
});

type FormData = z.infer<typeof loginSchema>;

export default function Page() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const form = useForm<FormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "a@a.ab",
      password: "123456789",
    },
  });
  const { control, setError, handleSubmit } = form;

  const { currentUser, isLoading: isAuthLoading, login } = useCurrentUser();

  async function handleLogin(data: FormData) {
    try {
      await login(data.email, data.password);
    } catch (error: any) {
      console.log("Error", error);

      if (isApiValidationError(error) && error.response) {
        if (error.response.data.errors) {
          if (error.response.data.errors.email) {
            setError("email", {
              message: error.response.data.errors.email,
            });
          }
          if (error.response.data.errors.password) {
            setError("password", {
              message: error.response.data.errors.password,
            });
          }
        }
      }

      if (isApiError(error) && error.response) {
        if (error.response.status === HttpStatusCode.Unauthorized) {
          setError("email", {
            message: error.response.data.message,
          });
          setError("password", {
            message: error.response.data.message,
          });
        }
      }
    }
  }

  useEffect(() => {
    if (isAuthLoading) {
      console.log("[LoginPage] Loading user...");
      return;
    }

    if (!currentUser) {
      console.log("[LoginPage] No currentUser");
      return;
    }

    if (searchParams.get("redirect")) {
      const redirectUrl = searchParams.get("redirect");
      console.log("[LoginPage] Redirecting to", redirectUrl);
      if (redirectUrl) {
        router.push(redirectUrl);
      }
    } else {
      console.log("[LoginPage] No redirect URL, redirecting to /");
      router.push("/");
    }
  }, [currentUser, isAuthLoading, router, searchParams]);

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
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Input
                      type="text"
                      value={field.value || ""}
                      onChange={field.onChange}
                      placeholder="Email"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Input
                      type="password"
                      value={field.value || ""}
                      onChange={field.onChange}
                      placeholder="Password"
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
