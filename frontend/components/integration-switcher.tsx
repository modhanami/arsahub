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
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { cn } from "@/lib/utils";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckIcon, ChevronsUpDown, PlusCircleIcon } from "lucide-react";
import { useParams, useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Integration, useIntegrations } from "../hooks/api";
import { integrationCreateSchema } from "../lib/validations/integration";
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

interface IntegrationSwitcherProps extends PopoverTriggerProps {}

type FormData = z.infer<typeof integrationCreateSchema>;

export default function IntegrationsSwitcher({
  className,
}: IntegrationSwitcherProps) {
  const { data: integrations, loading, refetch } = useIntegrations(1); // TODO: for testing, replace with the userId from the session
  const { integrationId }: { id: string; integrationId: string } = useParams();

  console.log(
    "🚀 ~ file: integration-switcher.tsx:68 ~ integrations:",
    integrations
  );
  const [open, setOpen] = React.useState(false);
  const router = useRouter();
  const [showNewIntegrationDialog, setShowNewIntegrationsDialog] =
    React.useState(false);
  const [selectedIntegration, setSelectedIntegration] = React.useState<
    Integration | undefined
  >(undefined);

  const form = useForm<FormData>({
    resolver: zodResolver(integrationCreateSchema),
  });
  const [isCreating, setIsCreating] = React.useState(false);

  React.useEffect(() => {
    if (loading) {
      return;
    }
    if (!selectedIntegration) {
      const integration = integrations?.find(
        (integration) => integration.id === Number(integrationId)
      );
      if (integration) {
        setSelectedIntegration(integration);
      }
    }
  }, [loading, integrations, selectedIntegration, integrationId]);

  console.log("integrations", integrations);
  console.log("selectedIntegration", selectedIntegration);

  async function onSubmit(values: FormData) {
    console.log(values);

    setIsCreating(true);

    const response = await fetch(`http://localhost:8080/api/integrations`, {
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

    if (!response?.ok) {
      return toast({
        title: "Something went wrong.",
        description: "Your integration was not created. Please try again.",
        variant: "destructive",
      });
    }

    toast({
      title: "Integration created.",
      description: "Your integration was created successfully.",
    });

    refetch();
    setShowNewIntegrationsDialog(false);
  }

  return (
    <Dialog
      open={showNewIntegrationDialog}
      onOpenChange={setShowNewIntegrationsDialog}
    >
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            aria-label="Select an integration"
            className={cn("w-[200px] justify-between", className)}
          >
            <Avatar className="mr-2 h-5 w-5">
              <AvatarImage
                src={`https://avatar.vercel.sh/${selectedIntegration?.name}.png`}
                alt={selectedIntegration?.name}
              />
              <AvatarFallback>AS</AvatarFallback>
            </Avatar>
            {selectedIntegration?.name}
            <ChevronsUpDown className="ml-auto h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[200px] p-0">
          <Command>
            <CommandList>
              <CommandInput placeholder="Search integration..." />
              <CommandEmpty>No integrations found.</CommandEmpty>
              <CommandGroup>
                {integrations.map((integration) => (
                  <CommandItem
                    key={integration.name}
                    onSelect={() => {
                      setSelectedIntegration(integration);
                      setOpen(false);
                      router.push(`/integrations/${integration.id}`);
                    }}
                    className="text-sm"
                  >
                    <Avatar className="mr-2 h-5 w-5">
                      <AvatarImage
                        src={`https://avatar.vercel.sh/${integration.name}.png`}
                        alt={integration.name}
                        className="grayscale"
                      />
                      <AvatarFallback>SC</AvatarFallback>
                    </Avatar>
                    {integration.name}
                    <CheckIcon
                      className={cn(
                        "ml-auto h-4 w-4",
                        selectedIntegration?.name === integration.name
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
                      setShowNewIntegrationsDialog(true);
                    }}
                  >
                    <PlusCircleIcon className="mr-2 h-5 w-5" />
                    Create Integration
                  </CommandItem>
                </DialogTrigger>
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Create integration</DialogTitle>
          <DialogDescription>
            Add a new integration to start using gamiication.
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
                    <Input placeholder="Name of your integration" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2 mt-8">
              <Button
                variant="outline"
                type="button"
                onClick={() => setShowNewIntegrationsDialog(false)}
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
