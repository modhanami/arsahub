import {
  UserProfile,
  UserProfileRealTime,
} from "../../../../../components/ui/team-members";

async function getUserProfile(userId: string) {
  const res = await fetch(
    `http://localhost:8080/api/activities/1/profile?userId=${userId}`
  );

  if (!res.ok) {
    // This will activate the closest `error.js` Error Boundary
    throw new Error("Failed to fetch data");
  }

  return res.json();
}

export default async function Page() {
  const data = await getUserProfile("i_am_user1");
  console.log(data);

  return (
    <main>
      <UserProfileRealTime
        userId="i_am_user1"
        name={data.user.name}
        username="a@b.com"
        avatar="X"
        points={data.points}
        achievements={data.achievements}
      />
    </main>
  );
}
