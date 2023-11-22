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
    .max(500, { message: "Must not be more than 500 characters" })
    .optional(),
  trigger: z.object({
    key: z.string({
      required_error: "Please select a trigger",
    }),
    params: z.object({}).passthrough().optional(),
  }),
  action: z.discriminatedUnion(
    "key",
    [pointsAddSchema, achievementUnlockSchema],
    {
      errorMap: (issue, ctx) => {
        console.log(issue.path);
        if (issue.code === z.ZodIssueCode.invalid_union_discriminator) {
          const path = issue.path.join(".");
          if (path === "action.key") {
            return {
              message: "Please select an action",
            };
          }
        }
        return { message: ctx.defaultError };
      },
    }
  ),
});
