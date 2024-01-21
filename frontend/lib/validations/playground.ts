import * as z from "zod";

const textParamSchema = z.object({
  type: z.literal("text"),
  key: z.string(),
  value: z.string(),
});

const integerParamSchema = z.object({
  type: z.literal("integer"),
  key: z.string(),
  value: z.coerce.number().int(),
});

export const playgroundTriggerSchema = z.object({
  userId: z.string().min(1, {
    message: "Please select a user",
  }),
  trigger: z.z.object({
    key: z.string().min(1, {
      message: "Please select a trigger",
    }),
  }),
  params: z.array(
    z.discriminatedUnion("type", [textParamSchema, integerParamSchema]),
  ),
});
