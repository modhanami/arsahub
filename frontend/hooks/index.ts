import { useCurrentApp } from "@/lib/current-app";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createAchievement,
  createAppUser,
  createReward,
  createRule,
  createTrigger,
  createWebhook,
  deleteAchievement,
  deleteAppUser,
  deleteRule,
  deleteTrigger,
  deleteWebhook,
  dryTrigger,
  fetchAchievements,
  fetchAnalytics,
  fetchAppByAPIKey,
  fetchAppUser,
  fetchAppUserPointsHistory,
  fetchAppUsers,
  fetchCurrentUserWithAccessToken,
  fetchLeaderboard,
  fetchMyApp,
  fetchRewards,
  fetchRules,
  fetchTrigger,
  fetchTriggers,
  FetchTriggersOptions,
  fetchWebhooks,
  getRule,
  sendTrigger,
  setAchievementImage,
  setRewardImage,
  updateAppUser,
  updateRule,
  updateTrigger,
  updateWebhook,
} from "@/api";
import {
  AchievementCreateRequest,
  AchievementResponse,
  AppUserCreateRequest,
  AppUserUpdateRequest,
  RewardCreateRequest,
  RewardResponse,
  RuleCreateRequest,
  RuleUpdateRequest,
  TriggerCreateRequest,
  TriggerSendRequest,
  TriggerUpdateRequest,
  UserIdentity,
  WebhookCreateRequest,
} from "@/types/generated-types";
import {
  AchievementSetImageRequestClient,
  RewardSetImageRequestClient,
  UserResponseWithAccessToken,
} from "@/types";

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

export function useAppUserPointsHistory(userId: string) {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["appUserPointsHistory", userId],
    queryFn: () => currentApp && fetchAppUserPointsHistory(currentApp, userId),
    enabled: !!currentApp && !!userId,
  });
}

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

export function useTrigger(triggerId: number) {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["trigger", triggerId],
    queryFn: () => currentApp && fetchTrigger(currentApp, triggerId),
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

export function useUpdateTrigger() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: ({
      triggerId,
      updateRequest,
    }: {
      triggerId: number;
      updateRequest: TriggerUpdateRequest;
    }) => currentApp && updateTrigger(currentApp, triggerId, updateRequest),
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

export function useDryTrigger() {
  const { currentApp } = useCurrentApp();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (request: TriggerSendRequest) =>
      dryTrigger(currentApp, request),
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

export function useUpdateRule() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: ({
      ruleId,
      updateRequest,
    }: {
      ruleId: number;
      updateRequest: RuleUpdateRequest;
    }) => updateRule(currentApp, ruleId, updateRequest),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["rules"] });
    },
  });
}

export function useRule(ruleId: number) {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["rule", ruleId],
    queryFn: () => currentApp && getRule(currentApp, ruleId),
    enabled: !!currentApp,
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

export function useOwnedApp(user?: UserIdentity | null) {
  return useQuery({
    queryKey: ["app", user?.internalUserId],
    queryFn: fetchMyApp,
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

export function useAppUser(appId: number, userId: string) {
  return useQuery({
    queryKey: ["appUser", userId],
    queryFn: () => fetchAppUser(appId, userId),
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

export function useUpdateAppUser() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (request: {
      userId: string;
      updateRequest: AppUserUpdateRequest;
    }) =>
      currentApp &&
      updateAppUser(currentApp, request.userId, request.updateRequest),
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

export function useWebhooks() {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["webhooks"],
    queryFn: () => currentApp && fetchWebhooks(currentApp),
    enabled: !!currentApp,
  });
}

export function useCreateWebhook() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (newWebhook: WebhookCreateRequest) =>
      createWebhook(currentApp, newWebhook),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["webhooks"] });
    },
  });
}

export function useUpdateWebhook() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: ({
      webhookId,
      updateRequest,
    }: {
      webhookId: number;
      updateRequest: WebhookCreateRequest;
    }) => currentApp && updateWebhook(currentApp, webhookId, updateRequest),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["webhooks"] });
    },
  });
}

export function useDeleteWebhook() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();
  if (!currentApp) {
    throw new Error("No current app");
  }

  return useMutation({
    mutationFn: (webhookId: number) =>
      currentApp && deleteWebhook(currentApp, webhookId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["webhooks"] });
    },
  });
}

export type UseAnalyticsParams = {
  type: string;
  start?: Date;
  end?: Date;
};

export function useAnalytics<T>(options: UseAnalyticsParams) {
  const { currentApp } = useCurrentApp();

  return useQuery({
    queryKey: ["analytics", options.type, options.start, options.end],
    queryFn: () => currentApp && fetchAnalytics<T>(currentApp, options),
    enabled: !!currentApp && !!options.start && !!options.end,
  });
}
