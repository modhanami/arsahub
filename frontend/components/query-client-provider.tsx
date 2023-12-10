"use client";
import {ReactQueryDevtools} from "@tanstack/react-query-devtools";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";

const queryClient = new QueryClient();

export function WrappedQueryClientProvider({
                                             children,
                                           }: {
  children: React.ReactNode;
}) {
  return (
    <QueryClientProvider client={queryClient}>{children}
      <ReactQueryDevtools initialIsOpen={false}/>
    </QueryClientProvider>
  );
}
