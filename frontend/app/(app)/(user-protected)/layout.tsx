import { UserProtectedPage } from "@/components/protected-page";

export default function Layout({ children }: { children: React.ReactNode }) {
  return <UserProtectedPage>{children}</UserProtectedPage>;
}
