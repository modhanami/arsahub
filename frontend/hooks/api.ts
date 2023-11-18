// export function fetchRules(activityId: number) {
//     return fetch(`http://localhost:8080/api/activities/${activityId}/rules`, {
//       method: "GET",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       next: {
//         tags: [`rules`],
//       },
//     });
//   }

import React from "react";
import { toast } from "../components/ui/use-toast";

// export function fetchTriggers(activityId: number) {
//     return fetch(`http://localhost:8080/api/activities/${activityId}/triggers`, {
//       method: "GET",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       next: {
//         tags: [`triggers`],
//       },
//     });
//   }

//   // export function fetchActions()

//   // fetch members
//   export function fetchMembers(activityId: number) {
//     return fetch(`http://localhost:8080/api/activities/${activityId}/members`, {
//       method: "GET",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       next: {
//         tags: [`members`],
//       },
//     });
//   }

interface Member {
  memberId: number;
  name: string;
  points: number;
  userId: string;
}
// rewrite the above to react hooks
export function useMembers(activityId: number) {
  const [members, setMembers] = React.useState<Member[]>([]);

  React.useEffect(() => {
    async function fetchMembers() {
      const response = await fetch(
        `http://localhost:8080/api/activities/${activityId}/members`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          next: {
            tags: [`members`],
          },
        }
      );

      if (!response?.ok) {
        return toast({
          title: "Something went wrong.",
          description: "Activity members could not be fetched.",
          variant: "destructive",
        });
      }

      return response.json();
    }

    fetchMembers().then((members) => setMembers(members));
  }, [activityId]);

  return members;
}

export interface Trigger {
  title: string;
  description: string;
  key: string;
  id: number;
}

export function useTriggers(activityId: number) {
  const [triggers, setTriggers] = React.useState<Trigger[]>([]);

  React.useEffect(() => {
    async function fetchTriggers() {
      const response = await fetch(
        `http://localhost:8080/api/activities/${activityId}/triggers`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          next: {
            tags: [`triggers`],
          },
        }
      );

      if (!response?.ok) {
        return toast({
          title: "Something went wrong.",
          description: "Activity triggers could not be fetched.",
          variant: "destructive",
        });
      }

      return response.json();
    }

    fetchTriggers().then((triggers) => setTriggers(triggers));
  }, [activityId]);

  return triggers;
}

// rule
// {
//     "createdAt": "2023-11-04T17:15:29.781097Z",
//     "updatedAt": "2023-11-04T17:15:29.781097Z",
//     "title": "Give 1 point when points reached 1000",
//     "description": null,
//     "trigger": {
//       "createdAt": "2023-11-04T16:14:46.747363Z",
//       "updatedAt": "2023-11-04T16:14:46.747363Z",
//       "title": "Points reached",
//       "description": null,
//       "key": "points_reached",
//       "id": 8
//     },
//     "action": {
//       "createdAt": "2023-10-31T13:54:49.958514Z",
//       "updatedAt": "2023-10-31T13:54:49.958514Z",
//       "title": "Add points",
//       "description": null,
//       "jsonSchema": {
//         "type": "object",
//         "$schema": "http://json-schema.org/draft-04/schema#",
//         "required": [
//           "value"
//         ],
//         "properties": {
//           "value": {
//             "type": "number"
//           }
//         }
//       },
//       "key": "add_points",
//       "id": 1
//     },
//     "triggerTypeParams": null,
//     "actionParams": {
//       "value": "1"
//     },
//     "id": 25
//   }
export interface Rule {
  id: number;
  title: string;
  trigger: Trigger;
  action: Action;
}

export function useRules(activityId: number) {
  const [rules, setRules] = React.useState<Rule[]>([]);

  React.useEffect(() => {
    async function fetchRules() {
      const response = await fetch(
        `http://localhost:8080/api/activities/${activityId}/rules`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          next: {
            tags: [`rules`],
          },
        }
      );

      if (!response?.ok) {
        return toast({
          title: "Something went wrong.",
          description: "Activity rules could not be fetched.",
          variant: "destructive",
        });
      }

      return response.json();
    }

    fetchRules().then((rules) => setRules(rules));
  }, [activityId]);

  return rules;
}

export interface Action {
  title: string;
  description: string;
  key: string;
  id: number;
  jsonSchema: Record<string, unknown>;
}

export interface AchievementResponse {
  achievementId: number;
  title: string;
  description: string;
  imageUrl: string;
}

interface UserProfile {
  user: {
    userId: number;
    name: string;
  };
  points: number;
  achievements: AchievementResponse[];
}
export function useUserProfile(activityId: number, userId: string | null) {
  const [profile, setProfile] = React.useState<UserProfile | null>(null);

  React.useEffect(() => {
    async function fetchProfile() {
      if (!userId) {
        return null;
      }
      const response = await fetch(
        `http://localhost:8080/api/activities/${activityId}/profile?userId=${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          next: {
            tags: [`profile`],
          },
        }
      );

      if (!response?.ok) {
        // return toast({
        //   title: "Something went wrong.",
        //   description: "Activity profile could not be fetched.",
        //   variant: "destructive",
        // });
        return null;
      }

      return response.json();
    }

    fetchProfile().then((profile) => {
      if (profile) {
        setProfile(profile);
      }
    });
  }, [activityId, userId]);

  return profile;
}