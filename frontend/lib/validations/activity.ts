import * as z from "zod";

export const activityCreateSchema = z.object({
  title: z.string().min(3).max(128).optional(),
  description: z.string().optional(),
});
