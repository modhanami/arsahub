import * as z from "zod";
// export const ruleCreateSchema = z.object({
//   name: z.string(),
//   description: z.string().optional(),
//   trigger: z.object({
//     key: z.string(),
//   }),
//   action: z.discriminatedUnion("key", [
//     pointsAddSchema,
//     achievementUnlockSchema,
//   ]),
// });

// send trigger for a user in an activity

export const playgroundTriggerSchema = z.object({
  userId: z.string(),
  trigger: z.z.object({
    key: z.string(),
  }),
});
