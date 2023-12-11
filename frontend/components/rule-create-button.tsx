import { Icons } from "./icons";
import { Button, ButtonProps } from "./ui/button";
import React, { forwardRef } from "react";

interface RuleCreateButtonProps extends ButtonProps {}

export const RuleCreateButton = forwardRef(
  (props: RuleCreateButtonProps, ref: React.Ref<HTMLButtonElement>) => {
    return (
      <Button ref={ref} {...props}>
        <Icons.add className="mr-2 h-4 w-4" />
        New Rule
      </Button>
    );
  },
);

RuleCreateButton.displayName = "RuleCreateButton";
