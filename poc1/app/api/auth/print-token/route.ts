import { getServerSession } from "next-auth";
import { getToken } from "next-auth/jwt";
import { getSession } from "next-auth/react";
import { NextResponse } from "next/server";
import { authOptions } from "../[...nextauth]/route";

export async function GET(req: Request) {
  console.log(process.env.NEXTAUTH_SECRET);
  const token = await getToken({
    req,
    secret: process.env.NEXTAUTH_SECRET,
    // raw: true,
  });
  const session = await getServerSession(authOptions);

  return Response.json({ session, token });
}
