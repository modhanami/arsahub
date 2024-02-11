import { Icons } from "../components/icons";
import {
  AchievementSetImageRequest,
  UserResponse,
} from "@/types/generated-types";
import { AxiosError } from "axios";

export type NavItem = {
  title: string;
  href: string;
  disabled?: boolean;
};

export type MainNavItem = NavItem;

export type SidebarNavItem =
  | ({
      title: string;
      disabled?: boolean;
      external?: boolean;
      icon?: keyof typeof Icons;
      appProtected?: boolean;
      type: "item";
      hoverHighlight?: boolean;
    } & (
      | {
          href: string;
          items?: never;
        }
      | {
          href?: string;
          items: NavLink[];
        }
    ))
  | {
      type: "divider";
    }
  | {
      type: "title";
      title: string;
    };

export type DashboardConfig = {
  mainNav: MainNavItem[];
  sidebarNav: SidebarNavItem[];
};

export type ContextProps = {
  params: {
    appId: string;
    userId: string;
  };
};

export type SiteConfig = {
  name: string;
  description: string;
  url: string;
  ogImage: string;
  links: {
    twitter: string;
    github: string;
  };
};

export type UserResponseWithAccessToken = UserResponse & {
  accessToken: string;
};

export type ApiErrorHolder<T = unknown, D = any> = AxiosError<T, D>;

declare module "axios" {
  export interface AxiosRequestConfig {
    isRefreshTokenRequest?: boolean;
    isRetryRequest?: boolean;
  }

  export interface AxiosError {
    config: AxiosRequestConfig;
  }
}

export type AchievementSetImageRequestClient = Omit<
  AchievementSetImageRequest,
  "image"
> & {
  image: File;
};

export type RewardSetImageRequestClient = Omit<
  RewardSetImageRequest,
  "image"
> & {
  image: File;
};

export type UserIdentity = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
};
