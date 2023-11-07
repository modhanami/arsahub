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
  name: z.string(),
  description: z.string().optional(),
  trigger: z.object({
    key: z.string(),
  }),
  action: z.discriminatedUnion("key", [
    pointsAddSchema,
    achievementUnlockSchema,
  ]),
});
