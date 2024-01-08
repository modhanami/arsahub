import isAlphanumeric from "validator/es/lib/isAlphanumeric";

export function isAlphaNumericExtended(key: string, allowSpaces = false) {
  return isAlphanumeric(key, undefined, {
    ignore: "_-" + (allowSpaces ? " " : ""),
  });
}

export const ALPHA_NUMERIC_EXTENDED_MESSAGE =
  "Must contain only alphanumeric characters, underscores, and dashes";
