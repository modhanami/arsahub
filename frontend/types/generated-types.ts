/* tslint:disable */
/* eslint-disable */

// Generated using typescript-generator version 3.2.1263 on 2024-01-07 13:10:55.

export interface AchievementCreateRequest {
  title: string | null;
  description: string | null;
}

export interface Action {
  key: string;
}

export interface AddPointsAction extends Action {
  points: number;
}

export interface AppUserCreateRequest {
  uniqueId: string;
  displayName: string;
}

export interface CustomUnitCreateRequest {
  name: string;
  key: string;
}

export interface FieldDefinition {
  key: string | null;
  type: string | null;
  label: string | null;
}

export interface KeyAndParams {
  key: string;
  params: { [index: string]: any } | null;
}

export interface RuleCreateRequest {
  title: string | null;
  description: string | null;
  trigger: KeyAndParams;
  action: KeyAndParams;
  conditions: { [index: string]: any } | null;
  repeatability: string;
}

export interface RuleCreateRequestKt {}

export interface TriggerCreateRequest {
  fields: FieldDefinition[] | null;
  title: string | null;
  key: string | null;
  description: string | null;
}

export interface TriggerSendRequest {
  key: string;
  params: { [index: string]: any } | null;
  userId: string;
}

export interface UnlockAchievementAction extends Action {
  achievementId: number;
}

export interface UserLoginRequest {
  email: string;
  password: string;
}

export interface UserSignupRequest {
  email: string;
  password: string;
}

export interface AchievementResponse {
  achievementId: number;
  title: string;
  description: string | null;
  imageUrl: string | null;
}

export interface ApiError {
  message: string;
}

export interface ApiValidationError {
  message: string;
  errors: { [index: string]: string };
}

export interface AppResponse {
  id: number;
  name: string;
  apiKey: string;
}

export interface AppUserResponse {
  userId: string;
  displayName: string;
  points: number;
  achievements: AchievementResponse[];
}

export interface AppWithAPIToken {
  app: App;
  apiKey: string;
}

export interface LeaderboardResponse {
  leaderboard: string;
  entries: Entry[];
}

export interface LoginResponse {
  accessToken: string;
}

export interface RuleResponse {
  createdAt: Date | null;
  updatedAt: Date | null;
  title: string | null;
  description: string | null;
  trigger: TriggerResponse | null;
  action: string | null;
  actionPoints: number | null;
  actionAchievement: AchievementResponse | null;
  id: number | null;
  repeatability: string | null;
  conditions: { [index: string]: any } | null;
}

export interface SignupResponse {
  accessToken: string;
}

export interface TriggerResponse {
  createdAt: Date | null;
  updatedAt: Date | null;
  title: string | null;
  description: string | null;
  key: string | null;
  id: number | null;
  fields: FieldDefinition[] | null;
}

export interface UserResponse {
  userId: number | null;
  name: string;
  username: string | null;
}

export interface AchievementUnlock extends AppUpdate {
  userId: string;
  achievement: AchievementResponse;
}

export interface AppUpdate {}

export interface LeaderboardUpdate extends AppUpdate {
  leaderboard: LeaderboardResponse;
}

export interface PointsUpdate extends AppUpdate {
  userId: string;
  points: number;
}

export interface App
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  title: string | null;
  description: string | null;
  apiKey: string | null;
  owner: User | null;
  id: number | null;
}

export interface Entry {
  rank: number;
  userId: string;
  memberName: string;
  score: number;
}

export interface User
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  username: string;
  name: string;
  uuid: string | null;
  password: string | null;
  email: string | null;
  userId: number | null;
}

export interface AuditedEntity extends ManagedMappedSuperclass {
  createdAt: Date | null;
  updatedAt: Date | null;
}

export interface ManagedEntity extends Managed {}

export interface PersistentAttributeInterceptable
  extends PrimeAmongSecondarySupertypes {}

export interface SelfDirtinessTracker extends PrimeAmongSecondarySupertypes {}

export interface ManagedMappedSuperclass extends Managed {}

export interface Managed extends PrimeAmongSecondarySupertypes {}

export interface PrimeAmongSecondarySupertypes {}
