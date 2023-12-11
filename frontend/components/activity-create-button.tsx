import { Icons } from "./icons";
import { Button, ButtonProps } from "./ui/button";
import React, { forwardRef } from "react";

interface ActivityCreateButtonProps extends ButtonProps {}

export const ActivityCreateButton = forwardRef(
  (props: ActivityCreateButtonProps, ref: React.Ref<HTMLButtonElement>) => {
    return (
      <Button ref={ref} {...props}>
        <Icons.add className="mr-2 h-4 w-4" />
        New Activity
      </Button>
    );
  },
);

ActivityCreateButton.displayName = "ActivityCreateButton";
