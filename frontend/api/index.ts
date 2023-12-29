import {
  AchievementCreateRequest,
  AchievementResponse,
  ApiError,
  ApiValidationError,
  AppResponse,
  AppUserCreateRequest,
  AppUserResponse,
  LeaderboardResponse,
  RuleResponse,
  TriggerCreateRequest,
  TriggerResponse,
  TriggerSendRequest,
} from "../types/generated-types";

import axios from "axios";
import { UserResponseWithUUID } from "@/types";

export const API_URL = process.env.NEXT_PUBLIC_API_URL;

const instance = axios.create({
  baseURL: API_URL,
});

instance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (axios.isAxiosError(error)) {
      if (!error.response) {
        console.error("No response received:", error.request || error.message);
        return Promise.reject({
          message: "No response received",
        });
      }
      if (
        error.response.status === 400 &&
        isApiValidationError(error.response.data)
      ) {
        const apiValidationError: ApiValidationError = error.response.data;
        console.error("API Validation Error:", apiValidationError);
        return Promise.reject(apiValidationError);
      }

      if (isApiError(error.response.data)) {
        const apiError: ApiError = error.response.data;
        console.error("API Error:", apiError);
        return Promise.reject(apiError);
      }
    } else {
      console.error("Unknown Error:", error);
      return Promise.reject({
        message: "Unknown Error",
      });
    }
  },
);

export function isApiValidationError(
  error: unknown,
): error is ApiValidationError {
  return (error as ApiValidationError).errors !== undefined;
}

export function isApiError(error: unknown): error is ApiError {
  return (error as ApiError).message !== undefined;
}

export async function fetchAchievements(app: AppResponse) {
  const { data } = await instance.get<AchievementResponse[]>(
    `${API_URL}/apps/achievements`,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export async function createAchievement(
  app: AppResponse,
  newAchievement: AchievementCreateRequest,
) {
  const { data } = await instance.post<AchievementResponse>(
    `${API_URL}/apps/achievements`,
    newAchievement,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export async function fetchAppUsers(app: AppResponse) {
  const { data } = await instance.get<AppUserResponse[]>(
    `${API_URL}/apps/users`,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export function makeAppAuthHeader(app: AppResponse): { Authorization: string } {
  return {
    Authorization: `Bearer ${app.apiKey}`,
  };
}

export async function fetchTriggers(currentApp: AppResponse) {
  const { data } = await instance.get<TriggerResponse[]>(
    `${API_URL}/apps/triggers`,
    {
      headers: {
        ...makeAppAuthHeader(currentApp),
      },
    },
  );
  return data;
}

export async function createTrigger(
  currentApp: AppResponse,
  newTrigger: TriggerCreateRequest,
) {
  const { data } = await instance.post<TriggerResponse>(
    `${API_URL}/apps/triggers`,
    newTrigger,
    {
      headers: {
        ...makeAppAuthHeader(currentApp),
      },
    },
  );
  return data;
}

export async function sendTrigger(
  currentApp: AppResponse,
  trigger: TriggerSendRequest,
) {
  await instance.post<void>(`${API_URL}/apps/trigger`, trigger, {
    headers: {
      ...makeAppAuthHeader(currentApp),
    },
  });
}

export async function fetchRules(app: AppResponse) {
  const { data } = await instance.get<RuleResponse[]>(`${API_URL}/apps/rules`, {
    headers: {
      ...makeAppAuthHeader(app),
    },
  });
  return data;
}

export type UserUUID = string;

export async function fetchApp(userUUID: UserUUID) {
  const { data } = await instance.get<AppResponse>(
    `${API_URL}/apps?userUUID=${userUUID}`,
  );
  return data;
}

export async function fetchLeaderboard(appId: number, type: string) {
  const { data } = await instance.get<LeaderboardResponse>(
    `${API_URL}/apps/${appId}/leaderboard?type=${type}`,
  );
  return data;
}

export async function fetchUserByUUID(uuid: string) {
  const { data } = await instance.get<UserResponseWithUUID>(
    `${API_URL}/apps/users/current`,
    {
      headers: {
        Authorization: `Bearer ${uuid}`,
      },
    },
  );
  return data;
}

export async function createAppUser(
  app: AppResponse,
  newUser: AppUserCreateRequest,
) {
  const { data } = await instance.post<AppUserResponse>(
    `${API_URL}/apps/users`,
    newUser,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export async function fetchAppUser(app: AppResponse, userId: string) {
  const { data } = await instance.get<AppUserResponse>(
    `${API_URL}/apps/${app.id}/users/${userId}`,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export async function fetchAppByAPIKey(apiKey: string) {
  const { data } = await instance.get<AppResponse>(`${API_URL}/apps/current`, {
    headers: {
      Authorization: `Bearer ${apiKey}`,
    },
  });
  return data;
}
