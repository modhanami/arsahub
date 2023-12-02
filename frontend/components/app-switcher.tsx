"use client";

import * as React from "react";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from "@/components/ui/command";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { cn } from "@/lib/utils";
import { DevTool } from "@hookform/devtools";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckIcon, ChevronsUpDown, PlusCircleIcon } from "lucide-react";
import { useParams, useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { API_URL, App, useAppTemplates, useApps } from "../hooks/api";
import { appCreateSchema } from "../lib/validations/app";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "./ui/form";
import { toast } from "./ui/use-toast";

type PopoverTriggerProps = React.ComponentPropsWithoutRef<
  typeof PopoverTrigger
>;

interface AppSwitcherProps extends PopoverTriggerProps {}

type FormData = z.infer<typeof appCreateSchema>;

export default function AppsSwitcher({ className }: AppSwitcherProps) {
  const { data: apps, loading, refetch } = useApps(1); // TODO: for testing, replace with the userId from the session
  const appTemplates = useAppTemplates();

  const { appId }: { id: string; appId: string } = useParams();

  console.log("🚀 ~ file: app-switcher.tsx:68 ~ apps:", apps);
  const [open, setOpen] = React.useState(false);
  const router = useRouter();
  const [showNewAppDialog, setShowNewAppsDialog] = React.useState(false);
  const [selectedApp, setSelectedApp] = React.useState<App | undefined>(
    undefined
  );

  const form = useForm<FormData>({
    resolver: zodResolver(appCreateSchema),
  });
  const [isCreating, setIsCreating] = React.useState(false);

  React.useEffect(() => {
    if (loading) {
      return;
    }
    if (!selectedApp) {
      const app = apps?.find((app) => app.id === Number(appId));
      if (app) {
        setSelectedApp(app);
      }
    }
  }, [loading, apps, selectedApp, appId]);

  console.log("apps", apps);
  console.log("selectedApp", selectedApp);

  async function onSubmit(values: FormData) {
    console.log(values);

    setIsCreating(true);

    const templateId = values.templateId;
    console.log(
      "🚀 ~ file: app-switcher.tsx:114 ~ onSubmit ~ templateId:",
      templateId
    );
    const response = await fetch(`${API_URL}/apps`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: values.name,
        createdBy: 1, // TODO: for testing, replace with the userId from the session
        templateId: templateId === 0 ? null : templateId,
      }),
    });

    setIsCreating(false);

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

    refetch();
    setShowNewAppsDialog(false);
  }

  return (
    <Dialog open={showNewAppDialog} onOpenChange={setShowNewAppsDialog}>
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            aria-label="Select an app"
            className={cn("w-[200px] justify-between", className)}
          >
            <Avatar className="mr-2 h-5 w-5">
              <AvatarImage
                src={`https://avatar.vercel.sh/${selectedApp?.name}.png`}
                alt={selectedApp?.name}
              />
              <AvatarFallback>AS</AvatarFallback>
            </Avatar>
            {selectedApp?.name}
            <ChevronsUpDown className="ml-auto h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[200px] p-0">
          <Command>
            <CommandList>
              <CommandInput placeholder="Search app..." />
              <CommandEmpty>No apps found.</CommandEmpty>
              <CommandGroup>
                {apps.map((app) => (
                  <CommandItem
                    key={app.name}
                    onSelect={() => {
                      setSelectedApp(app);
                      setOpen(false);
                      router.push(`/apps/${app.id}`);
                    }}
                    className="text-sm"
                  >
                    <Avatar className="mr-2 h-5 w-5">
                      <AvatarImage
                        src={`https://avatar.vercel.sh/${app.name}.png`}
                        alt={app.name}
                        className="grayscale"
                      />
                      <AvatarFallback>SC</AvatarFallback>
                    </Avatar>
                    {app.name}
                    <CheckIcon
                      className={cn(
                        "ml-auto h-4 w-4",
                        selectedApp?.name === app.name
                          ? "opacity-100"
                          : "opacity-0"
                      )}
                    />
                  </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
            <CommandSeparator />
            <CommandList>
              <CommandGroup>
                <DialogTrigger asChild>
                  <CommandItem
                    onSelect={() => {
                      setOpen(false);
                      setShowNewAppsDialog(true);
                    }}
                  >
                    <PlusCircleIcon className="mr-2 h-5 w-5" />
                    Create App
                  </CommandItem>
                </DialogTrigger>
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Create app</DialogTitle>
          <DialogDescription>
            Add a new app to start using gamiication.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="grid gap-2">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input placeholder="Name of your app" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="templateId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Template</FormLabel>
                  <FormControl>
                    <RadioGroup onValueChange={field.onChange}>
                      <div className="flex items-center space-x-2">
                        <RadioGroupItem value="0" id="none" />
                        <div>
                          <Label htmlFor="none">None</Label>
                          <p className="text-sm text-muted-foreground">
                            Start from scratch
                          </p>
                        </div>
                      </div>
                      {appTemplates?.map((template) => {
                        const templateIdString = template.id.toString();
                        return (
                          <div
                            className="flex items-center space-x-2"
                            key={templateIdString}
                          >
                            <RadioGroupItem
                              value={templateIdString}
                              id={templateIdString}
                            />
                            <div>
                              <Label htmlFor={templateIdString}>
                                {template.name}
                              </Label>
                              <p className="text-sm text-muted-foreground">
                                {template.description}
                              </p>
                            </div>
                          </div>
                        );
                      })}
                    </RadioGroup>
                  </FormControl>
                </FormItem>
              )}
            />

            {/* show what's included in the template */}
            {form.watch("templateId") &&
              Number(form.watch("templateId")) !== 0 && (
                <div className="border border-border rounded-md p-4">
                  <h3 className="text-lg font-semibold">
                    What&apos;s included
                  </h3>
                  <div className="mt-4 text-md">
                    <ul className="list-disc list-inside">
                      <li>
                        Triggers:
                        <ul className="list-disc list-inside mt-2 mb-4">
                          {appTemplates
                            ?.find(
                              (template) =>
                                template.id === Number(form.watch("templateId"))
                            )
                            ?.triggerTemplates.map((triggerTemplate) => (
                              <li
                                key={triggerTemplate.id}
                                className="ml-4 text-sm text-muted-foreground leading-6"
                              >
                                {triggerTemplate.title} ({triggerTemplate.key})
                              </li>
                            ))}
                        </ul>
                      </li>
                      <li>Rules</li>
                    </ul>
                  </div>
                </div>
              )}

            <div className="flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2 mt-8">
              <Button
                variant="outline"
                type="button"
                onClick={() => setShowNewAppsDialog(false)}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={isCreating}>
                Create
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
