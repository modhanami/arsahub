export default function Page({
  params,
}: {
  params: { integrationId: string };
}) {
  return <div>Integration ID: {params.integrationId}</div>;
}
