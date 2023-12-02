export default function Page({ params }: { params: { appId: string } }) {
  return <div>App ID: {params.appId}</div>;
}
