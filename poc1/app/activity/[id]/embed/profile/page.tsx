import { getServerSession } from "next-auth";
import { getSession } from "next-auth/react";
import Link from "next/link";
import { authOptions } from "../../../../api/auth/[...nextauth]/route";
import { UserPoints } from "./user-points";

export default async function Page({ params }: { params: { id: string } }) {
  const session = await getServerSession(authOptions);
  const clientSession = await getSession();
  console.log("clientSession", clientSession);

  console.log("session", session);

  return (
    <div>
      <h1>Child: localhost:9000</h1>
      <pre>Session: {JSON.stringify(session, null, 2)}</pre>
      <br />
      {!session && (
        <ul>
          <li>
            <Link
              href={`/api/auth/signin?callbackUrl=${encodeURIComponent(
                `/activity/${params.id}/embed/profile`
              )}`}
              passHref
            >
              Sign in
            </Link>
          </li>
          <li>
            <Link href={`/api/auth/print-token`} passHref>
              Print token
            </Link>
          </li>
        </ul>
      )}
      <br />
      <UserPoints />
    </div>
  );
}
