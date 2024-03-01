import * as z from "zod";

export const FieldTypeEnum = z.enum(["Text", "Integer"] as const);

export function getFieldTypeLabel(fieldType: string): string | undefined {
  switch (fieldType.toLowerCase()) {
    case "text":
      return "Text";
    case "integer":
      return "Integer";
    default:
      return undefined;
  }
}

export function generateTriggerKeyFromTitle(title: string): string | undefined {
  const regex = /[a-zA-Z0-9_-]+/g;
  const matches = title.match(regex);

  if (matches) {
    console.log("Matches", matches);
    return matches.join("_");
  } else {
    console.log("No matches found");
    return;
  }
}
