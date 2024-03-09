import { Separator } from "@/components/ui/separator";

interface DashboardHeaderProps {
  heading: string;
  text?: string;
  children?: React.ReactNode;
  separator?: boolean;
}

export function DashboardHeader({
  heading,
  text,
  children,
  separator = false,
}: DashboardHeaderProps) {
  return (
    <>
      <div className="flex items-center justify-between">
        <div className="grid gap-1">
          <h2 className="text-2xl font-bold tracking-tight">{heading}</h2>
          {text && <p className="text-muted-foreground">{text}</p>}
        </div>
        {children}
      </div>
      {separator && <Separator />}
    </>
  );
}
