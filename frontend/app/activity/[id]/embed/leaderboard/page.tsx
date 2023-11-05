export default function Page({ params }: { params: { id: string } }) {
  return (
    <div>
      <h1>Leaderboard</h1>
      <div>ID: {params.id}</div>
    </div>
  );
}
