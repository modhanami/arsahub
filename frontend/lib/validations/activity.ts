import * as z from "zod";

export const activityCreateSchema = z.object({
  title: z.string().min(4).max(200).optional(),
  description: z.string().optional(),
});
