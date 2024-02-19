import { useCurrentApp } from "@/lib/current-app";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createAchievement,
  createAppUser,
  createReward,
  createRule,
  createTrigger,
  deleteAchievement,
  deleteAppUser,
  deleteRule,
  deleteTrigger,
  fetchAchievements,
  fetchAppByAPIKey,
  fetchAppUser,
  fetchAppUsers,
  fetchCurrentUserWithAccessToken,
  fetchLeaderboard,
  fetchMyApp,
  fetchRewards,
  fetchRules,
  fetchTriggers,
  FetchTriggersOptions,
  sendTrigger,
  setAchievementImage,
  setRewardImage,
} from "@/api";
import {
  AchievementCreateRequest,
  AchievementResponse,
  AppUserCreateRequest,
  RewardCreateRequest,
  RewardResponse,
  RuleCreateRequest,
  TriggerCreateRequest,
  TriggerSendRequest,
} from "@/types/generated-types";
import {
  AchievementSetImageRequestClient,
  RewardSetImageRequestClient,
  UserResponseWithAccessToken,
} from "@/types";
import { useCurrentUser } from "@/lib/current-user";

export function useAppUsers() {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["appUsers"],
    queryFn: () => currentApp && fetchAppUsers(currentApp),
    enabled: !!currentApp,
  });
}

const defaultOptions = {
  enabled: true,
};

export function useAchievements(options = defaultOptions) {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["achievements"],
    queryFn: () => currentApp && fetchAchievements(currentApp),
    enabled: !!currentApp && options.enabled,
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

export function useDeleteAchievement() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (achievementId: number) =>
      currentApp && deleteAchievement(currentApp, achievementId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["achievements"] });
    },
  });
}

export function useSetAchievementImage() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (request: AchievementSetImageRequestClient) =>
      setAchievementImage(currentApp, request),
    onSuccess: async (data, variables) => {
      // invalidate only the achievement that was updated
      queryClient.setQueryData(
        ["achievements"],
        (oldData: AchievementResponse[] | undefined) => {
          if (!oldData) {
            return [];
          }
          return oldData.map((achievement) => {
            if (achievement.achievementId === variables.achievementId) {
              return {
                ...achievement,
                imageKey: data.imageKey,
              };
            } else {
              return achievement;
            }
          });
        },
      );
    },
  });
}

export function useTriggers(options: FetchTriggersOptions = {}) {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["triggers"],
    queryFn: () => currentApp && fetchTriggers(currentApp, options),
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

export function useDeleteTrigger() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (triggerId: number) =>
      currentApp && deleteTrigger(currentApp, triggerId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["triggers"] });
    },
  });
}

export function useSendTrigger() {
  const { currentApp } = useCurrentApp();
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

export function useCreateRule() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (newRule: RuleCreateRequest) => createRule(currentApp, newRule),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["rules"] });
    },
  });
}

export function useDeleteRule() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (ruleId: number) =>
      currentApp && deleteRule(currentApp, ruleId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["rules"] });
    },
  });
}

export function useLeaderboard(appId: number, type: string) {
  return useQuery({
    queryKey: ["leaderboard", appId, type],
    queryFn: () => fetchLeaderboard(appId, type),
  });
}

export function useOwnedApp() {
  const { currentUser } = useCurrentUser();
  return useQuery({
    queryKey: ["app"],
    queryFn: fetchMyApp,
    enabled: !!currentUser,
  });
}

export function useAppByAPIKey(apiKey: string | null) {
  return useQuery({
    queryKey: ["app", "byAPIKey", apiKey],
    queryFn: () => fetchAppByAPIKey(apiKey!!),
    enabled: !!apiKey,
  });
}

export function useUser(accessToken: string | null) {
  return useQuery<UserResponseWithAccessToken, Error>({
    queryKey: ["user", accessToken],
    queryFn: () => fetchCurrentUserWithAccessToken(accessToken!!),
    enabled: !!accessToken,
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

export function useDeleteAppUser() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (userId: string) =>
      currentApp && deleteAppUser(currentApp, userId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["appUsers"] });
    },
  });
}

// Points shop

export function useRewards() {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["rewards"],
    queryFn: () => currentApp && fetchRewards(currentApp),
    enabled: !!currentApp,
  });
}

export function useCreateReward() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationKey: ["rewards"],
    mutationFn: (newReward: RewardCreateRequest) =>
      createReward(currentApp, newReward),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["rewards"] });
    },
  });
}

export function useSetRewardImage() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (request: RewardSetImageRequestClient) =>
      setRewardImage(currentApp, request),
    onSuccess: async (data, variables) => {
      // invalidate only the reward that was updated
      queryClient.setQueryData(
        ["rewards"],
        (oldData: RewardResponse[] | undefined) => {
          if (!oldData) {
            return [];
          }
          return oldData.map((reward) => {
            if (reward.id === variables.rewardId) {
              return {
                ...reward,
                imageKey: data.imageKey,
              };
            } else {
              return reward;
            }
          });
        },
      );
    },
  });
}
