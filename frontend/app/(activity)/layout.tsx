import { AppProtectedPage } from "../../components/protected-page";

export default function Layout({ children }: { children: React.ReactNode }) {
  return <AppProtectedPage>{children}</AppProtectedPage>;
}
