export default function Page({ params }: { params: { id: string } }) {
  return <div>Activity ID: {params.id}</div>;
}
