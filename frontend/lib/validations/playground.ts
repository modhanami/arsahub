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

const integerSetParamSchema = z.object({
  type: z.literal("integerSet"),
  key: z.string(),
  value: z.string().refine(isArrayOfStringIntegers, {
    message: "Please enter a comma-separated list of integers",
  }),
});

const textSetParamSchema = z.object({
  type: z.literal("textSet"),
  key: z.string(),
  value: z.string().refine(isArrayOfStrings, {
    message: "Please enter a comma-separated list of texts",
  }),
});

export function isArrayOfStringIntegers(value: string): boolean {
  return value.split(",").every((val) => {
    return !isNaN(parseInt(val.trim()));
  });
}

export function parseArrayOfStringIntegers(value: string): number[] {
  return value.split(",").map((val) => {
    return parseInt(val.trim());
  });
}

export function isArrayOfStrings(value: string): boolean {
  return value.split(",").every((val) => {
    return typeof val.trim() === "string";
  });
}

export function parseArrayOfStrings(value: string): string[] {
  return value.split(",").map((val) => {
    return val.trim();
  });
}

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
    z.discriminatedUnion("type", [
      textParamSchema,
      integerParamSchema,
      integerSetParamSchema,
      textSetParamSchema,
    ]),
  ),
});
