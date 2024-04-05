import { FieldDefinition } from "@/types/generated-types";
import React from "react";

export type Condition<T> = {
  uuid: string;
  field: string;
  operator: string;
  value: T;
  fieldDefinition?: FieldDefinition;
  inputType?: string;
  inputProps?: any;
};

interface SectionTitleProps {
  title: string;
  number?: number;
  isOptional?: boolean;
}

export function SectionTitle({ number, title, isOptional }: SectionTitleProps) {
  return (
    <div className="flex items-center space-x-2">
      {number && (
        <span className="h-8 w-8 bg-secondary rounded-full inline-flex items-center justify-center text-sm font-semibold">
          {number}
        </span>
      )}
      <h3 className="text-lg font-semibold">{title}</h3>
      {isOptional && <span className="text-muted-foreground">(Optional)</span>}
    </div>
  );
}
