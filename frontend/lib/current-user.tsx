"use client";
import {
  fetchCurrentUserWithAccessToken,
  loginUser,
  logoutUser,
  refreshAccessToken,
} from "@/api";
import { create } from "zustand";
import { UserResponseWithAccessToken } from "@/types";
import { devtools, subscribeWithSelector } from "zustand/middleware";

type State = {
  accessToken: string | null;
  isLoading: boolean;
  isInitialized: boolean;
  isRefreshing: boolean;
  currentUser: UserResponseWithAccessToken | null;
  setAccessToken: (accessToken: string | null) => void;
  setIsRefreshing: (isRefreshing: boolean) => void;
  refreshingCall: Promise<void> | null;
  setRefreshingCall: (refreshingCall: Promise<void> | null) => void;
  refresh: () => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  init: () => Promise<void>;
};

const INTERNAL: {
  loggingOutCall: Promise<void> | null;
} = {
  loggingOutCall: null,
};

export const useCurrentUser = create<State>()(
  devtools(
    subscribeWithSelector((set, get) => ({
      accessToken: null,
      isLoading: true,
      isInitialized: false,
      isRefreshing: false,
      currentUser: null,
      setAccessToken: async (accessToken: string | null) => {
        try {
          if (accessToken === null) {
            set({ accessToken: null, currentUser: null });
          } else {
            const currentUser =
              await fetchCurrentUserWithAccessToken(accessToken);
            set({ accessToken, currentUser });
          }
        } catch (error) {
          console.log("[useCurrentUser] error setting access token");
          console.error(error);
          set({ accessToken: null, currentUser: null });
        } finally {
          set({ isLoading: false });
        }
      },
      setIsRefreshing: (isRefreshing: boolean) => set({ isRefreshing }),
      refreshingCall: null,
      setRefreshingCall: (refreshingCall: Promise<void> | null) =>
        set({ refreshingCall }),
      refresh: async () => {
        const { setAccessToken, refreshingCall } = get();
        if (refreshingCall) {
          console.log(
            "[useCurrentUser][refresh] already refreshing, waiting...",
          );
          return refreshingCall;
        }

        console.log("[useCurrentUser][refresh] start");
        set({ isRefreshing: true });
        const newRefreshingCall = refreshAccessToken()
          .then((response) => {
            console.log("[useCurrentUser][refresh] got new access token");
            setAccessToken(response.accessToken);
          })
          .catch((error) => {
            console.log("[useCurrentUser][refresh] error refreshing");
            setAccessToken(null);
            return Promise.reject(error);
          });
        set({ refreshingCall: newRefreshingCall });
        try {
          await newRefreshingCall;
          console.log("[useCurrentUser][refresh] done");
        } catch (error) {
          console.log("[useCurrentUser][refresh] error refreshing");
          return Promise.reject(error);
        } finally {
          set({ isRefreshing: false, refreshingCall: null });
        }
      },
      login: async (email: string, password: string) => {
        const { setAccessToken } = get();
        try {
          set({ isLoading: true });
          const { accessToken } = await loginUser(email, password);
          setAccessToken(accessToken);
        } catch (error) {
          console.error(error);
        }
      },
      logout: async () => {
        const { setAccessToken } = get();
        try {
          if (INTERNAL.loggingOutCall) {
            console.log("[useCurrentUser] already logging out, waiting...");
            await INTERNAL.loggingOutCall;
          } else {
            console.log("[useCurrentUser] logging out");
            INTERNAL.loggingOutCall = logoutUser();
          }
          await INTERNAL.loggingOutCall;
        } catch (error) {
          console.error(error);
          console.log("[useCurrentUser] error logging out, clearing token");
        } finally {
          console.log("[useCurrentUser] clearing token");
          setAccessToken(null);
          INTERNAL.loggingOutCall = null;
        }
      },
      init: async () => {
        const { isInitialized } = get();
        if (isInitialized) {
          console.log("[useCurrentUser][init] is initialized, skipping");
          return;
        }

        const { refresh } = get();
        try {
          console.log("[useCurrentUser][init] start");
          await refresh();
          console.log("[useCurrentUser][init] done");
        } catch (error) {
          console.log("[useCurrentUser][init] error refreshing");
          console.error(error);
          return Promise.reject(error);
        } finally {
          set({
            isInitialized: true,
          });
        }
      },
    })),
  ),
);
