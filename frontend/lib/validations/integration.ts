import * as z from "zod";

export const integrationCreateSchema = z.object({
  name: z.string().min(4).max(200),
});
