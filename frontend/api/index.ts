import {
  AchievementCreateRequest,
  AchievementResponse,
  ApiError,
  ApiValidationError,
  AppResponse,
  AppUserCreateRequest,
  AppUserResponse,
  LeaderboardResponse,
  LoginResponse,
  RewardCreateRequest,
  RewardResponse,
  RuleCreateRequest,
  RuleResponse,
  TriggerCreateRequest,
  TriggerResponse,
  TriggerSendRequest,
  UserResponse,
} from "../types/generated-types";

import axios from "axios";
import {
  AchievementSetImageRequestClient,
  ApiErrorHolder,
  RewardSetImageRequestClient,
  UserResponseWithAccessToken,
} from "@/types";

export const API_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:3000/api";

console.log("API_URL", API_URL);
const instance = axios.create({
  baseURL: API_URL,
});

instance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (!axios.isAxiosError(error)) {
      console.error("Unknown Error:", error);
      return Promise.reject(error);
    }

    if (error.response) {
      return Promise.reject(error);
    }

    if (error.request) {
      console.error("Request Error:", error);
      return Promise.reject({
        message: "Request Error",
      });
    }

    console.error("Unknown Error:", error);
    return Promise.reject({
      message: "Unknown Error",
    });
  },
);

export function isApiValidationError(
  error: unknown,
): error is ApiErrorHolder<ApiValidationError> {
  return (
    axios.isAxiosError(error) &&
    (error.response?.data as ApiValidationError).errors !== undefined
  );
}

export function isApiError(error: unknown): error is ApiErrorHolder<ApiError> {
  return (
    axios.isAxiosError(error) &&
    (error.response?.data as ApiError).message !== undefined
  );
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

export async function setAchievementImage(
  app: AppResponse,
  { achievementId, image }: AchievementSetImageRequestClient,
) {
  const formData = new FormData();
  formData.append("image", image);

  const { data } = await instance.post<AchievementResponse>(
    `${API_URL}/apps/achievements/${achievementId}/image`,
    formData,
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

export function makeAppAuthHeader(app: AppResponse): { "X-API-Key": string } {
  return {
    "X-API-Key": `${app.apiKey}`,
  };
}

export function makeAppAuthHeaderWithToken(apiToken: string): {
  "X-API-Key": string;
} {
  return {
    "X-API-Key": `${apiToken}`,
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

export async function createRule(app: AppResponse, newRule: RuleCreateRequest) {
  const { data } = await instance.post<RuleResponse>(
    `${API_URL}/apps/rules`,
    newRule,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export type UserUUID = string;

export async function fetchMyApp() {
  const { data } = await instance.get<AppResponse>(`${API_URL}/apps/me`);
  return data;
}

export async function fetchLeaderboard(appId: number, type: string) {
  const { data } = await instance.get<LeaderboardResponse>(
    `${API_URL}/apps/${appId}/leaderboard?type=${type}`,
  );
  return data;
}

export async function fetchCurrentUserWithAccessToken(
  accessToken: string,
): Promise<UserResponseWithAccessToken> {
  const { data } = await instance.get<UserResponse>(
    `${API_URL}/apps/users/current`,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    },
  );
  return {
    ...data,
    accessToken,
  };
}

export async function loginUser(email: string, password: string) {
  const { data } = await instance.post<LoginResponse>(
    `${API_URL}/auth/login`,
    {
      email,
      password,
    },
    {
      withCredentials: true,
    },
  );
  return data;
}

export async function logoutUser() {
  await instance.post<void>(`${API_URL}/auth/logout`, null, {
    withCredentials: true,
  });
}

export async function refreshAccessToken(): Promise<LoginResponse> {
  const { data } = await instance.post<LoginResponse>(
    `${API_URL}/auth/refresh`,
    null,
    {
      withCredentials: true,
      isRefreshTokenRequest: true,
    },
  );
  return {
    accessToken: data.accessToken,
  };
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
      ...makeAppAuthHeaderWithToken(apiKey),
    },
  });
  return data;
}

export async function fetchRewards(app: AppResponse) {
  const { data } = await instance.get<RewardResponse[]>(
    `${API_URL}/apps/shop/rewards`,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export async function createReward(
  app: AppResponse,
  newReward: RewardCreateRequest,
) {
  const { data } = await instance.post<RewardResponse>(
    `${API_URL}/apps/shop/rewards`,
    newReward,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export async function setRewardImage(
  app: AppResponse,
  { rewardId, image }: RewardSetImageRequestClient,
) {
  const formData = new FormData();
  formData.append("image", image);

  const { data } = await instance.post<RewardResponse>(
    `${API_URL}/apps/shop/rewards/${rewardId}/image`,
    formData,
    {
      headers: {
        ...makeAppAuthHeader(app),
      },
    },
  );
  return data;
}

export async function syncExternalUser(): Promise<void> {
  console.log("syncExternalUser");
  await instance.post<void>(`${API_URL}/auth/sync`, null);
}
