import NextAuth, { AuthOptions } from "next-auth";

import AzureADProvider from "next-auth/providers/azure-ad";

export const authOptions: AuthOptions = {
  providers: [
    AzureADProvider({
      clientId: "7fa84d35-83cc-4559-b2d7-bd0dee0d9a56",
      clientSecret: "DFd8Q~jfdq1RULnGZo3cPSS1-K1AOgey2mj2qcAg",
      //   checks
    }),
  ],
  //   skipCSRFCheck: true,
  session: {
    strategy: "jwt",
  },
  callbacks: {
    async session(params) {
      const { token, session } = params;

      if (token?.sub && session?.user) {
        session.user.id = token.sub;
      }

      if (token.idToken) {
        session.idToken = token.idToken;
      }

      return session;
    },
    async jwt(params) {
      const { token, account } = params;
      //   console.log("inside jwt callback");
      //   console.log(`obj: ${JSON.stringify(params, null, 2)}`);

      //   auto register user
      if (account?.id_token) {
        token.idToken = account.id_token;

        // call backend api to register user
        try {
          const res = await fetch(`http://localhost:8080/api/auth/register`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              idToken: account.id_token,
            }),
          });

          const data = await res.json();
          console.log(`data: ${JSON.stringify(data, null, 2)}`);
        } catch (error) {
          console.log(`error: ${JSON.stringify(error, null, 2)}`);
        }
      }

      //   console.log(`token: ${JSON.stringify(token, null, 2)}`);
      //   user: {
      //     "id": "u0cRObrmHsxn_J2a23Weqj7OknYGFdygz7YEovWNY7o",
      //     "name": "TAWAN MUADMUENWAI",
      //     "email": "tawan.275@mail.kmutt.ac.th",
      //     "image": null
      //   }

      return token;
    },
  },
  cookies: {
    // sessionToken: {
    //   name: `__Secure-next-auth.session-token`,
    //   options: {
    //     path: "/",
    //     httpOnly: true,
    //     sameSite: "none",
    //     secure: true,
    //   },
    // },
    // callbackUrl: {
    //   name: `__Secure-next-auth.callback-url`,
    //   options: {
    //     path: "/",
    //     sameSite: "none",
    //     secure: true,
    //   },
    // },
    // csrfToken: {
    //   name: `__Host-next-auth.csrf-token`,
    //   options: {
    //     path: "/",
    //     httpOnly: true,
    //     sameSite: "none",
    //     secure: true,
    //   },
    // },
  },
};
const handler = NextAuth(authOptions);

export { handler as GET, handler as POST };
