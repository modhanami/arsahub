import * as React from "react";

import { cn } from "@/lib/utils";
import { Textarea, TextareaProps } from "@/components/ui/textarea";
import { useCharCountWithColor } from "@/components/ui/use-char-count-with-color";

export type TextareaWithCounterProps = Omit<TextareaProps, "type"> & {
  maxLength: number;
};

const TextareaWithCounter = React.forwardRef<
  HTMLTextAreaElement,
  TextareaWithCounterProps
>(({ className, maxLength, ...props }, ref) => {
  const { charCount, handleCountChar, color } = useCharCountWithColor(
    (props.value || props.defaultValue)?.toString() || "",
    maxLength,
  );

  return (
    <div className="relative">
      <Textarea
        className={cn(
          "pr-12", // Added padding to the right
          className,
        )}
        ref={ref}
        {...props}
        maxLength={maxLength}
        onChange={handleCountChar(props.onChange)}
      />
      <div className={cn("absolute right-2 bottom-2 text-xs", color)}>
        {`${charCount}/${maxLength}`}
      </div>
    </div>
  );
});
TextareaWithCounter.displayName = "Textarea";

export { TextareaWithCounter };
