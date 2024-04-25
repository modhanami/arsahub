import { ChangeEvent, ChangeEventHandler, useEffect, useState } from "react";

type TextInputElement = HTMLInputElement | HTMLTextAreaElement;

export const useCharCountWithColor = (
  initialValue: string,
  maxLength: number,
) => {
  const [charCount, setCharCount] = useState<number>(initialValue.length);

  const handleCountChar = (
    handler: ChangeEventHandler<TextInputElement> | undefined,
  ) => {
    return (event: ChangeEvent<TextInputElement>) => {
      if (handler) handler(event);
      if (event.target.value.length <= maxLength) {
        const newCharCount = event.target.value.length;
        if (newCharCount !== charCount) {
          setCharCount(newCharCount);
        }
      }
    };
  };

  useEffect(() => {
    setCharCount(initialValue.length);
  }, [initialValue]);

  const getColor = () => {
    const ratio = charCount / maxLength;
    if (ratio < 0.8) return "text-gray-500";
    if (ratio < 1) return "text-yellow-500";
    return "text-red-500";
  };

  return { charCount, handleCountChar, color: getColor() };
};
