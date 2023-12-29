// "use client";
// import { Button } from "@/components/ui/button";
// import { CardTitle } from "@/components/ui/card";
// import {
//   Dialog,
//   DialogClose,
//   DialogContent,
//   DialogTrigger,
// } from "@/components/ui/dialog";
// import {
//   Form,
//   FormControl,
//   FormField,
//   FormItem,
//   FormLabel,
//   FormMessage,
// } from "@/components/ui/form";
// import { Input } from "@/components/ui/input";
// import {
//   Select,
//   SelectContent,
//   SelectItem,
//   SelectTrigger,
//   SelectValue,
// } from "@/components/ui/select";
// import { zodResolver } from "@hookform/resolvers/zod";
// import { useRouter } from "next/navigation";
// import * as React from "react";
// import { useForm } from "react-hook-form";
// import * as z from "zod";
// import { ruleCreateSchema } from "../lib/validations/rule";
// import {
//   API_URL,
//   makeAppAuthHeader,
//   useActions,
//   useTriggers,
// } from "../hooks/api";
// import { RuleCreateButton } from "./rule-create-button";
// import { DialogHeader } from "./ui/dialog";
// import { toast } from "./ui/use-toast";
// import { useCurrentApp } from "@/lib/current-app";
//
// type FormData = z.infer<typeof ruleCreateSchema>;
//
// export function CreateRuleForm({ activityId }: { activityId: number }) {
//   const router = useRouter();
//   const form = useForm<FormData>({
//     resolver: zodResolver(ruleCreateSchema),
//     defaultValues: {
//       name: "New rule",
//       description: "New rule",
//     },
//   });
//   const [isCreating, setIsCreating] = React.useState(false);
//   const [isOpen, setIsOpen] = React.useState(false);
//   const triggers = useTriggers();
//   const actions = useActions();
//   const [selectedTrigger, setSelectedTrigger] = React.useState(null);
//   const { currentApp } = useCurrentApp();
//
//   async function onSubmit(values: FormData) {
//     console.log("submit", values);
//     setIsCreating(true);
//
//     const response = await fetch(`${API_URL}/apps/rules`, {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/json",
//         ...makeAppAuthHeader(currentApp),
//       },
//       body: JSON.stringify(values),
//     });
//
//     setIsCreating(false);
//     setIsOpen(false);
//
//     if (!response?.ok) {
//       return toast({
//         title: "Something went wrong.",
//         description: "Your rule was not created. Please try again.",
//         variant: "destructive",
//       });
//     }
//
//     toast({
//       title: "Rule created successfully.",
//       description: "Your rule was created successfully.",
//     });
//
//     router.refresh();
//   }
//
//   function renderTriggerParamsFields(triggerSchema: Record<string, any>) {
//     return Object.keys(triggerSchema?.properties || {}).map((paramName) => {
//       const paramSchema = triggerSchema.properties[paramName];
//       const paramKey = `trigger.params.${paramName}` as const;
//       const paramValue = form.watch(paramKey) as number;
//       const paramNameCapitalized =
//         paramName.charAt(0).toUpperCase() + paramName.slice(1);
//
//       // Render form fields based on schema type (e.g., 'number', 'string', 'boolean', etc.)
//       switch (paramSchema.type) {
//         case "number":
//           return (
//             <FormField
//               control={form.control}
//               name={paramKey}
//               key={paramName}
//               render={({ field }) => (
//                 <FormItem>
//                   <FormLabel>{paramNameCapitalized}</FormLabel>
//                   <FormControl>
//                     <Input
//                       {...field}
//                       placeholder={paramNameCapitalized}
//                       type="number"
//                       value={paramValue}
//                     />
//                   </FormControl>
//                   <FormMessage />
//                 </FormItem>
//               )}
//             />
//           );
//
//         // Add cases for other types as needed
//         default:
//           return null;
//       }
//     });
//   }
//
//   const triggerSchema = triggers.find(
//     (trigger) => trigger.key === form.watch("trigger.key"),
//   )?.jsonSchema;
//
//   return (
//     <>
//       <Dialog open={isOpen} onOpenChange={setIsOpen}>
//         <DialogTrigger asChild>
//           <RuleCreateButton />
//         </DialogTrigger>
//         <DialogContent className="sm:max-w-[425px]">
//           <DialogHeader>
//             <CardTitle>Create rule</CardTitle>
//           </DialogHeader>
//           <Form {...form}>
//             <form onSubmit={form.handleSubmit(onSubmit)} className="grid gap-2">
//               <FormField
//                 control={form.control}
//                 name="name"
//                 render={({ field }) => (
//                   <FormItem>
//                     <FormLabel>Name</FormLabel>
//                     <FormControl>
//                       <Input placeholder="Name of your rule" {...field} />
//                     </FormControl>{" "}
//                     <FormMessage />
//                   </FormItem>
//                 )}
//               />
//               <FormField
//                 control={form.control}
//                 name="description"
//                 render={({ field }) => (
//                   <FormItem>
//                     <FormLabel>Description</FormLabel>
//                     <FormControl>
//                       <Input
//                         placeholder="Description of your rule"
//                         {...field}
//                       />
//                     </FormControl>{" "}
//                     <FormMessage />
//                   </FormItem>
//                 )}
//               />
//
//               <FormField
//                 control={form.control}
//                 name="trigger.key"
//                 render={({ field }) => (
//                   <FormItem className="mt-4">
//                     <FormLabel>Trigger</FormLabel>
//                     <FormControl>
//                       <Select
//                         onValueChange={field.onChange}
//                         value={field.value}
//                       >
//                         <SelectTrigger>
//                           <SelectValue
//                             className="flex items-center justify-between w-full"
//                             placeholder="Select a trigger"
//                           />
//                         </SelectTrigger>
//                         <SelectContent className="w-full">
//                           {triggers.map((trigger) => (
//                             <SelectItem
//                               key={trigger.id}
//                               value={trigger.key!!}
//                               className="flex items-center justify-between w-full"
//                             >
//                               {trigger.title}
//                             </SelectItem>
//                           ))}
//                         </SelectContent>
//                       </Select>
//                     </FormControl>
//                     <FormMessage />
//                   </FormItem>
//                 )}
//               />
//
//               {triggerSchema &&
//                 // Render trigger params fields conditionally when trigger is selected
//                 renderTriggerParamsFields(triggerSchema)}
//
//               <FormField
//                 control={form.control}
//                 name="action.key"
//                 render={({ field }) => (
//                   <FormItem className="mt-4">
//                     <FormLabel>Action</FormLabel>
//                     <FormControl>
//                       <Select
//                         onValueChange={field.onChange}
//                         value={field.value}
//                       >
//                         <SelectTrigger>
//                           <SelectValue
//                             className="flex items-center justify-between w-full"
//                             placeholder="Select an action"
//                           />
//                         </SelectTrigger>
//                         <SelectContent className="w-full">
//                           {actions.map((action) => (
//                             <SelectItem
//                               key={action.id}
//                               value={action.key}
//                               className="flex items-center justify-between w-full"
//                             >
//                               {action.title}
//                             </SelectItem>
//                           ))}
//                         </SelectContent>
//                       </Select>
//                     </FormControl>
//                     <FormMessage />
//                   </FormItem>
//                 )}
//               />
//
//               {form.watch("action.key") == "add_points" && (
//                 <FormField
//                   control={form.control}
//                   name="action.params.value"
//                   render={({ field }) => (
//                     <FormItem>
//                       <FormLabel>Value</FormLabel>
//                       <FormControl>
//                         <Input placeholder="Value" type="number" {...field} />
//                       </FormControl>
//                       <FormMessage />
//                     </FormItem>
//                   )}
//                 />
//               )}
//
//               {form.watch("action.key") == "unlock_achievement" && (
//                 <FormField
//                   control={form.control}
//                   name="action.params.achievementId"
//                   render={({ field }) => (
//                     <FormItem>
//                       <FormLabel>Achievement</FormLabel>
//                       <FormControl>
//                         <Input
//                           placeholder="Achievement ID"
//                           type="number"
//                           {...field}
//                         />
//                       </FormControl>
//                       <FormMessage />
//                     </FormItem>
//                   )}
//                 />
//               )}
//
//               <div className="flex justify-between mt-8">
//                 <DialogClose asChild>
//                   <Button variant="outline" type="button">
//                     Cancel
//                   </Button>
//                 </DialogClose>
//                 <Button type="submit">Create</Button>
//               </div>
//             </form>
//           </Form>
//         </DialogContent>
//       </Dialog>
//     </>
//   );
// }
