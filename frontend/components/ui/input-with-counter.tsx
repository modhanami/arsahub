import * as React from "react";
import { cn } from "@/lib/utils";
import { Input, InputProps } from "./input";
import { useCharCountWithColor } from "@/components/ui/use-char-count-with-color";

export type InputWithCounterProps = Omit<InputProps, "type"> & {
  maxLength: number;
};

const InputWithCounter = React.forwardRef<
  HTMLInputElement,
  InputWithCounterProps
>(({ className, maxLength, ...props }, ref) => {
  const { charCount, handleCountChar, color } = useCharCountWithColor(
    (props.value || props.defaultValue)?.toString() || "",
    maxLength,
  );

  return (
    <div className="relative">
      <Input
        className={cn(
          "pr-16", // Added padding to the right
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
InputWithCounter.displayName = "InputWithCounter";

export { InputWithCounter };
