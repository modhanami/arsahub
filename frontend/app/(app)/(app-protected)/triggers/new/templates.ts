import { FieldDefinition } from "@/types/generated-types";
import { FieldTypeEnum } from "@/app/(app)/(app-protected)/triggers/shared";
import * as z from "zod";

interface TriggerTemplate {
  id: string;
  title: string;
  description?: string;
  fields?: (FieldDefinition & {
    type: z.infer<typeof FieldTypeEnum>;
  })[];
}

export const triggerTemplates: TriggerTemplate[] = [
  {
    id: "workshop-registered",
    title: "Workshop Registered",
    description: "Triggered when a user registers for a workshop",
    fields: [
      {
        key: "workshopId",
        label: "Workshop ID",
        type: "Integer",
      },
    ],
  },
  {
    id: "workshop-completed",
    title: "Workshop Completed",
    description: "Triggered when a user completes a workshop",
    fields: [
      {
        key: "workshopId",
        label: "Workshop ID",
        type: "Integer",
      },
      {
        key: "notes",
        label: "Notes",
        type: "Text",
      },
    ],
  },
  {
    id: "product-purchased",
    title: "Product Purchased",
    description: "Triggered when a user purchases a product",
    fields: [
      {
        key: "productId",
        label: "Product ID",
        type: "Integer",
      },
    ],
  },
];
