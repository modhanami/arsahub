// export function fetchRules(activityId: number) {
//     return fetch(`${API_URL}/activities/${activityId}/rules`, {
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
import {
  AppResponse,
  AppUserResponse,
  RuleResponse,
  TriggerResponse,
  UserProfileResponse,
} from "../types/generated-types";
import { useCurrentApp } from "../lib/current-app";
import { useQuery } from "@tanstack/react-query";

// export function fetchTriggers(activityId: number) {
//     return fetch(`${API_URL}/activities/${activityId}/triggers`, {
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
//     return fetch(`${API_URL}/activities/${activityId}/members`, {
//       method: "GET",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       next: {
//         tags: [`members`],
//       },
//     });
//   }

export function makeAppAuthHeader(
  app: AppResponse | undefined,
): Record<string, string> {
  if (!app) {
    return {};
  }

  return {
    Authorization: `Bearer ${app.apiKey}`,
  };
}

export interface Trigger {
  title: string;
  description: string;
  key: string;
  id: number;
  jsonSchema: Record<string, unknown>;
}

export function useTriggers() {
  const [triggers, setTriggers] = React.useState<TriggerResponse[]>([]);
  const { currentApp, isLoading } = useCurrentApp();

  React.useEffect(() => {
    if (isLoading) {
      return;
    }

    async function fetchTriggers() {
      const response = await fetch(`${API_URL}/apps/triggers`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...makeAppAuthHeader(currentApp),
        },
        next: {
          tags: [`triggers`],
        },
      });

      if (!response?.ok) {
        toast({
          title: "Something went wrong.",
          description: "Activity triggers could not be fetched.",
          variant: "destructive",
        });
        return null;
      }

      return response.json();
    }

    fetchTriggers().then((triggers) => {
      if (!triggers) {
        return;
      }
      setTriggers(triggers);
    });
  }, [currentApp, isLoading]);

  return triggers;
}

export function useActions() {
  const [actions, setActions] = React.useState<Action[]>([]);
  const { currentApp } = useCurrentApp();
  React.useEffect(() => {
    async function fetchActions() {
      const response = await fetch(`${API_URL}/activities/actions`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...makeAppAuthHeader(currentApp),
        },
        next: {
          tags: ["actions"],
        },
      });

      if (!response?.ok) {
        toast({
          title: "Something went wrong.",
          description: "Activity actions could not be fetched.",
          variant: "destructive",
        });
        return null;
      }

      return response.json();
    }

    fetchActions().then((actions) => {
      if (!actions) {
        return;
      }
      setActions(actions);
    });
  }, [currentApp]);

  return actions;
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

export function useRules(appId: number) {
  const [rules, setRules] = React.useState<RuleResponse[]>([]);
  const { currentApp } = useCurrentApp();
  React.useEffect(() => {
    async function fetchRules() {
      if (!appId) {
        return [];
      }

      const response = await fetch(`${API_URL}/apps/rules`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...makeAppAuthHeader(currentApp),
        },
        next: {
          tags: [`rules`],
        },
      });

      if (!response?.ok) {
        toast({
          title: "Something went wrong.",
          description: "Activity rules could not be fetched.",
          variant: "destructive",
        });
        return null;
      }

      return response.json();
    }

    fetchRules().then((rules) => {
      if (!rules) {
        return;
      }
      setRules(rules);
    });
  }, [appId, currentApp]);

  return rules;
}

export interface Action {
  title: string;
  description: string;
  key: string;
  id: number;
  jsonSchema: Record<string, unknown>;
}

export function useUserProfile(activityId: number, userId: string) {
  const [profile, setProfile] = React.useState<UserProfileResponse | null>(
    null,
  );
  const { currentApp } = useCurrentApp();
  React.useEffect(() => {
    async function fetchProfile() {
      if (!userId) {
        return null;
      }
      const response = await fetch(`${API_URL}/apps/users/${userId}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...makeAppAuthHeader(currentApp),
        },
        next: {
          tags: [`profile`],
        },
      });

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

export type App = {
  id: number;
  name: string;
};
export type UserUUID = string;

async function fetchApps(userId: number, currentApp: AppResponse | undefined) {
  const response = await fetch(`${API_URL}/apps?userId=${userId}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      ...makeAppAuthHeader(currentApp),
    },
    next: {
      tags: [`apps`],
    },
  });

  if (!response?.ok) {
    toast({
      title: "Something went wrong.",
      description: "Apps could not be fetched.",
      variant: "destructive",
    });
    return null;
  }

  return response.json();
}

async function fetchApp(userUUID: UserUUID) {
  const response = await fetch(`${API_URL}/apps?userUUID=${userUUID}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response?.ok) {
    toast({
      title: "Something went wrong.",
      description: "App could not be fetched.",
      variant: "destructive",
    });
    return null;
  }

  return response.json();
}

export function useApp(userUUID: UserUUID | null) {
  return useQuery({
    queryKey: ["appData", userUUID],
    queryFn: () => fetchApp(userUUID!!),
    enabled: !!userUUID,
  });
}

export function useApps(userId: number) {
  const [loading, setLoading] = React.useState<boolean>(true); // Always start with loading as true
  const [apps, setApps] = React.useState<App[]>([]);
  const { currentApp } = useCurrentApp();
  React.useEffect(() => {
    fetchApps(userId, currentApp).then((apps) => {
      if (!apps) {
        return;
      }
      setApps(apps);
      setLoading(false); // Set loading to false once data is fetched.
    });
  }, [currentApp, userId]);

  function refetch() {
    setLoading(true);
    fetchApps(userId, currentApp).then((apps) => {
      if (!apps) {
        return;
      }
      setApps(apps);
      setLoading(false);
    });
  }

  return { loading, data: apps, refetch };
}

type TriggerTemplate = Trigger;

export interface AppTemplate {
  id: number;
  name: string;
  description: string;
  triggerTemplates: TriggerTemplate[];
}

export function useAppTemplates() {
  const [templates, setTemplates] = React.useState<AppTemplate[]>([]);
  const { currentApp } = useCurrentApp();
  React.useEffect(() => {
    async function fetchTemplates() {
      const response = await fetch(`${API_URL}/apps/templates`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...makeAppAuthHeader(currentApp),
        },
        next: {
          tags: [`templates`],
        },
      });

      if (!response?.ok) {
        toast({
          title: "Something went wrong.",
          description: "App templates could not be fetched.",
          variant: "destructive",
        });
        return null;
      }

      return response.json();
    }

    fetchTemplates().then((templates) => {
      if (!templates) {
        return;
      }
      setTemplates(templates);
    });
  }, [currentApp]);

  return templates;
}

export async function fetchUsers(
  currentApp: AppResponse,
): Promise<AppUserResponse[]> {
  try {
    const res = await fetch(`${API_URL}/apps/users`, {
      headers: makeAppAuthHeader(currentApp),
    });
    return await res.json();
  } catch (error) {
    console.error("Error fetching users:", error);
    throw error;
  }
}

export const API_URL = process.env.NEXT_PUBLIC_API_URL;
