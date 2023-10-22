import { getServerSession } from "next-auth";
import { authOptions } from "../../../../api/auth/[...nextauth]/route";
import { signIn } from "next-auth/react";
import Link from "next/link";
import { getToken } from "next-auth/jwt";

export default async function Page({ params }: { params: { id: string } }) {
  const session = await getServerSession(authOptions);

  console.log("session", session);

  if (!session) {
    return (
      <div>
        <h1>Profile</h1>
        <div>Not signed in</div>
        <Link
          href={`/api/auth/signin?callbackUrl=${encodeURIComponent(
            `/activity/${params.id}/embed/profile`
          )}`}
          passHref
        >
          Sign in
        </Link>
      </div>
    );
  }

  return (
    <div>
      <h1>Profile</h1>
      <pre>Session: {JSON.stringify(session, null, 2)}</pre>
    </div>
  );
}
