"use client";

export default function Page({ params }: { params: { appId: string } }) {
  const app = 
  return <div>App ID: {params.appId}</div>;
}
