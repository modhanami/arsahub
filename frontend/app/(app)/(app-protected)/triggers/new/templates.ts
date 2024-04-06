import { FieldDefinition } from "@/types/generated-types";
import { FieldTypeEnum } from "@/app/(app)/(app-protected)/triggers/shared";
import * as z from "zod";

export interface TriggerTemplate {
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
        type: "integer",
        label: null,
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
        type: "integer",
        label: null,
      },
      {
        key: "notes",
        type: "text",
        label: null,
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
        type: "integer",
        label: null,
      },
    ],
  },
];
