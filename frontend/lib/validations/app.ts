import * as z from "zod";

export const appCreateSchema = z.object({
  name: z
    .string()
    .min(4, { message: "Must be between 4 and 200 characters" })
    .max(200, { message: "Must be between 4 and 200 characters" }),
  templateId: z.coerce.number().optional(),
});
