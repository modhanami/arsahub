import * as z from "zod";

const pointsAddSchema = z.object({
  key: z.literal("add_points"),
  params: z.object({
    value: z.coerce.number(),
  }),
});

const achievementUnlockSchema = z.object({
  key: z.literal("unlock_achievement"),
  params: z.object({
    achievementId: z.coerce.number(),
  }),
});

export const ruleCreateSchema = z.object({
  name: z
    .string()
    .min(4, { message: "Must be between 4 and 200 characters" })
    .max(200, { message: "Must be between 4 and 200 characters" }),
  description: z
    .string()
    .max(200, { message: "Must not be more than 500 characters" })
    .optional(),
  trigger: z.object({
    key: z.string(),
    params: z.object({}).passthrough().optional(),
  }),
  action: z.discriminatedUnion("key", [
    pointsAddSchema,
    achievementUnlockSchema,
  ]),
});
