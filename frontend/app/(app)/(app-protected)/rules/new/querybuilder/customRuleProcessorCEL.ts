import {
  FullField,
  parseNumber,
  RuleProcessor,
  toArray,
  trimIfString,
} from "react-querybuilder";

const shouldNegate = (op: string) => /^(does)?not/i.test(op);

const escapeDoubleQuotes = (
  v: string | number | boolean | object | null,
  escapeQuotes?: boolean,
) => (typeof v !== "string" || !escapeQuotes ? v : v.replaceAll(`"`, `\\"`));

/**
 * Custom version of `defaultRuleProcessorCEL` from react-querybuilder
 * @see [defaultRuleProcessorCEL](https://github.com/react-querybuilder/react-querybuilder/blob/d6619bc7139ca06e5e1cae9bd2631d9655b1b90e/packages/react-querybuilder/src/utils/formatQuery/defaultRuleProcessorCEL.ts)
 */
export const customRuleProcessorCEL: RuleProcessor = (
  rule,
  // istanbul ignore next
  options = {},
) => {
  const { field, operator, value, valueSource } = rule;
  const { escapeQuotes, parseNumbers, fieldData } = options;
  const valueIsField = valueSource === "field";
  const operatorTL = operator.replace(/^=$/, "==");
  const useBareValue =
    typeof value === "number" ||
    typeof value === "boolean" ||
    typeof value === "bigint" ||
    shouldRenderAsNumber(parseNumbers, fieldData);

  console.log("[customRuleProcessorCEL] inputs", {
    rule,
    options,
    valueIsField,
    operatorTL,
    useBareValue,
  });

  switch (operatorTL) {
    case "<":
    case "<=":
    case "==":
    case "!=":
    case ">":
    case ">=":
      return `${field} ${operatorTL} ${
        valueIsField || useBareValue
          ? trimIfString(value)
          : `"${escapeDoubleQuotes(value, escapeQuotes)}"`
      }`;

    case "contains":
    case "doesNotContain": {
      const negate = shouldNegate(operatorTL) ? "!" : "";
      return `${negate}${field}.contains(${
        valueIsField
          ? trimIfString(value)
          : `"${escapeDoubleQuotes(value, escapeQuotes)}"`
      })`;
    }

    case "containsAll": {
      return `${field}.containsAll([${toArray(value)
        .map((val) => {
          const shouldParseNumbers = shouldRenderAsNumber(
            parseNumbers,
            fieldData,
          );
          const valNum = shouldParseNumbers
            ? parseNumber(val, { parseNumbers: true })
            : NaN;

          // return isNaN(valNum) ? "" : valNum;
          return !isNaN(valNum)
            ? valNum
            : shouldParseNumbers
              ? ""
              : `"${escapeDoubleQuotes(val, escapeQuotes)}"`;
        })
        .filter((val) => val !== "")
        .join(", ")}])`;
    }

    case "beginsWith":
    case "doesNotBeginWith": {
      const negate = shouldNegate(operatorTL) ? "!" : "";
      return `${negate}${field}.startsWith(${
        valueIsField
          ? trimIfString(value)
          : `"${escapeDoubleQuotes(value, escapeQuotes)}"`
      })`;
    }

    case "endsWith":
    case "doesNotEndWith": {
      const negate = shouldNegate(operatorTL) ? "!" : "";
      return `${negate}${field}.endsWith(${
        valueIsField
          ? trimIfString(value)
          : `"${escapeDoubleQuotes(value, escapeQuotes)}"`
      })`;
    }

    case "null":
      return `${field} == null`;

    case "notNull":
      return `${field} != null`;

    case "in":
    case "notIn": {
      const negate = shouldNegate(operatorTL);
      const valueAsArray = toArray(value);
      if (valueAsArray.length > 0) {
        return `${negate ? "!(" : ""}${field} in [${valueAsArray
          .map((val) =>
            valueIsField || shouldRenderAsNumber(parseNumbers)
              ? `${trimIfString(val)}`
              : `"${escapeDoubleQuotes(val, escapeQuotes)}"`,
          )
          .join(", ")}]${negate ? ")" : ""}`;
      } else {
        return "";
      }
    }

    case "between":
    case "notBetween": {
      const valueAsArray = toArray(value);
      if (valueAsArray.length >= 2 && !!valueAsArray[0] && !!valueAsArray[1]) {
        const [first, second] = valueAsArray;
        const firstNum = shouldRenderAsNumber(true)
          ? parseNumber(first, { parseNumbers: true })
          : NaN;
        const secondNum = shouldRenderAsNumber(true)
          ? parseNumber(second, { parseNumbers: true })
          : NaN;
        let firstValue = isNaN(firstNum)
          ? valueIsField
            ? `${first}`
            : `"${escapeDoubleQuotes(first, escapeQuotes)}"`
          : firstNum;
        let secondValue = isNaN(secondNum)
          ? valueIsField
            ? `${second}`
            : `"${escapeDoubleQuotes(second, escapeQuotes)}"`
          : secondNum;

        if (
          firstValue === firstNum &&
          secondValue === secondNum &&
          secondNum < firstNum
        ) {
          const tempNum = secondNum;
          secondValue = firstNum;
          firstValue = tempNum;
        }

        if (operator === "between") {
          return `(${field} >= ${firstValue} && ${field} <= ${secondValue})`;
        } else {
          return `(${field} < ${firstValue} || ${field} > ${secondValue})`;
        }
      } else {
        return "";
      }
    }
  }
  return "";
};

const shouldRenderAsNumber = (
  parseNumbers?: boolean,
  fieldData?: FullField,
) => {
  console.log("parseNumbers", parseNumbers, "fieldData", fieldData);
  return (
    parseNumbers &&
    (fieldData?.dataType === "integer" || fieldData?.dataType === "integerSet")
  );
};
