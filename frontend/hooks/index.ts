import { useCurrentApp } from "@/lib/current-app";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createAchievement,
  createAppUser,
  createTrigger,
  fetchAchievements,
  fetchApp,
  fetchAppByAPIKey,
  fetchAppUser,
  fetchAppUsers,
  fetchLeaderboard,
  fetchRules,
  fetchTriggers,
  fetchUserByUUID,
  sendTrigger,
  UserUUID,
} from "@/api";
import {
  AchievementCreateRequest,
  AppUserCreateRequest,
  TriggerCreateRequest,
  TriggerSendRequest,
} from "@/types/generated-types";
import { UserResponseWithUUID } from "@/types";

export function useAppUsers() {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["appUsers"],
    queryFn: () => currentApp && fetchAppUsers(currentApp),
    enabled: !!currentApp,
  });
}

export function useAchievements() {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["achievements"],
    queryFn: () => currentApp && fetchAchievements(currentApp),
    enabled: !!currentApp,
  });
}

export function useCreateAchievement() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationKey: ["achievements"],
    mutationFn: (newAchievement: AchievementCreateRequest) =>
      createAchievement(currentApp, newAchievement),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["achievements"] });
    },
  });
}

export function useTriggers() {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["triggers"],
    queryFn: () => currentApp && fetchTriggers(currentApp),
    enabled: !!currentApp,
  });
}

export function useCreateTrigger() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationKey: ["triggers"],
    mutationFn: (newTrigger: TriggerCreateRequest) =>
      createTrigger(currentApp, newTrigger),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["triggers"] });
    },
  });
}

export function useSendTrigger() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (request: TriggerSendRequest) =>
      sendTrigger(currentApp, request),
  });
}

export function useRules() {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["rules"],
    queryFn: () => currentApp && fetchRules(currentApp),
    enabled: !!currentApp,
  });
}

export function useLeaderboard(appId: number, type: string) {
  return useQuery({
    queryKey: ["leaderboard", appId, type],
    queryFn: () => fetchLeaderboard(appId, type),
  });
}

export function useAppByUserUUID(userUUID: UserUUID | null) {
  return useQuery({
    queryKey: ["app", "byUserUUID", userUUID],
    queryFn: () => fetchApp(userUUID!!),
    enabled: !!userUUID,
  });
}

export function useAppByAPIKey(apiKey: string | null) {
  return useQuery({
    queryKey: ["app", "byAPIKey", apiKey],
    queryFn: () => fetchAppByAPIKey(apiKey!!),
    enabled: !!apiKey,
  });
}

export function useUser(userUUID: UserUUID | null) {
  return useQuery<UserResponseWithUUID, Error>({
    queryKey: ["user", userUUID],
    queryFn: () => fetchUserByUUID(userUUID!),
    enabled: !!userUUID,
  });
}

export function useAppUser(userId: string) {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["appUser", userId],
    queryFn: () => currentApp && fetchAppUser(currentApp, userId),
    enabled: !!currentApp,
  });
}

export function useCreateAppUser() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (newUser: AppUserCreateRequest) =>
      createAppUser(currentApp, newUser),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["appUsers"] });
    },
  });
}
