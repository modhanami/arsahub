import * as z from "zod";

export const FieldTypeEnum = z.enum([
  "text",
  "integer",
  "integerSet",
  "textSet",
] as const);

export function getFieldTypeLabel(fieldType: string): string | undefined {
  switch (fieldType) {
    case "text":
      return "Text";
    case "integer":
      return "Integer";
    case "integerSet":
      return "Integer Set";
    case "textSet":
      return "Text Set";
    default:
      return fieldType;
  }
}

export function getFieldTypeExample(fieldType: string): string | undefined {
  switch (fieldType) {
    case "text":
      return "Hello, World!";
    case "integer":
      return "27";
    case "integerSet":
      return "27, 28, 29";
    case "textSet":
      return "Hello, World, !";
    default:
      return fieldType;
  }
}

export function generateTriggerKeyFromTitle(title: string): string | undefined {
  const regex = /[a-zA-Z0-9_-]+/g;
  const matches = title.match(regex);

  if (matches) {
    console.log("Matches", matches);
    return matches.join("_").toLowerCase();
  } else {
    console.log("No matches found");
    return;
  }
}
