import { Icons } from "./icons";
import { Button, ButtonProps } from "./ui/button";

interface RuleCreateButtonProps extends ButtonProps {}

export function RuleCreateButton(props: RuleCreateButtonProps) {
  return (
    <Button {...props}>
      <Icons.add className="mr-2 h-4 w-4" />
      New Rule
    </Button>
  );
}
