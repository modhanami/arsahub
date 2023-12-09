/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-12-09 11:34:29.

export interface AchievementCreateRequest {
  title: string | null;
  description: string | null;
}

export interface AchievementResponse {
  achievementId: number;
  title: string;
  description: string | null;
  imageUrl: string | null;
}

export interface AchievementUnlock extends ActivityUpdate {
  userId: number;
  achievement: AchievementResponse;
}

export interface AchievementUpdateRequest {
  title: string | null;
  description: string | null;
}

export interface ActionDefinition {
  key: string;
  params: { [index: string]: string };
}

export interface ActionResponse {
  createdAt: Date | null;
  updatedAt: Date | null;
  title: string | null;
  description: string | null;
  jsonSchema: { [index: string]: any } | null;
  key: string | null;
  id: number | null;
}

export interface ActivityCreateRequest {
  title: string | null;
  description: string | null;
  appId: number | null;
}

export interface ActivityResponse {
  id: number | null;
  title: string;
  description: string | null;
  members: MemberResponse[];
}

export interface ActivityTriggerRequest {
  key: string;
  params: { [index: string]: string };
  userId: number;
}

export interface ActivityUpdate {}

export interface ActivityUpdateRequest {
  title: string | null;
  description: string | null;
}

export interface ApiError {
  message: string;
}

export interface ApiValidationError {
  message: string;
  errors: { [index: string]: string };
}

export interface AppCreateRequest {
  name: string;
  createdBy: number;
  templateId: number | null;
}

export interface AppCreateResponse {
  id: number;
  name: string;
  apiKey: string | null;
}

export interface AppResponse {
  id: number;
  name: string;
  apiKey: string;
}

export interface AppTemplateResponse {
  name: string | null;
  description: string | null;
  triggerTemplates: TriggerTemplateResponse[];
  id: number | null;
}

export interface AppWithAPIToken {
  app: App;
  apiKey: string;
}

export interface CustomUnitCreateRequest {
  name: string;
  key: string;
}

export interface CustomUnitResponse {
  name: string | null;
  key: string | null;
  id: number | null;
}

export interface LeaderboardResponse {
  leaderboard: string;
  entries: Entry[];
}

export interface LeaderboardUpdate extends ActivityUpdate {
  leaderboard: LeaderboardResponse;
}

export interface MemberResponse {
  memberId: number | null;
  name: string | null;
  points: number | null;
  userId: number | null;
  username: string | null;
}

export interface PointsUpdate extends ActivityUpdate {
  userId: number;
  points: number;
}

export interface RuleCondition {
  type: string;
  params: { [index: string]: string };
}

export interface RuleCreateRequest {
  name: string | null;
  description: string | null;
  trigger: TriggerDefinition;
  action: ActionDefinition;
  condition: RuleCondition | null;
}

export interface RuleResponse {
  createdAt: Date | null;
  updatedAt: Date | null;
  title: string | null;
  description: string | null;
  trigger: TriggerResponse | null;
  action: ActionResponse | null;
  triggerTypeParams: { [index: string]: any } | null;
  actionParams: { [index: string]: any } | null;
  id: number | null;
}

export interface RuleTemplateResponse {
  name: string | null;
  description: string | null;
  trigger: TriggerResponse | null;
  action: ActionResponse | null;
  app: AppResponse | null;
  actionParams: { [index: string]: any } | null;
  triggerParams: { [index: string]: any } | null;
  id: number | null;
}

export interface TriggerCreateRequest {
  title: string | null;
  description: string | null;
  key: string | null;
  appId: number | null;
}

export interface TriggerDefinition {
  key: string;
  params: { [index: string]: string } | null;
}

export interface TriggerResponse {
  createdAt: Date | null;
  updatedAt: Date | null;
  title: string | null;
  description: string | null;
  key: string | null;
  id: number | null;
  jsonSchema: { [index: string]: any } | null;
}

export interface TriggerTemplateResponse {
  title: string | null;
  description: string | null;
  key: string | null;
  jsonSchema: { [index: string]: any } | null;
  id: number | null;
}

export interface UserActivityProfileResponse {
  user: UserResponse | null;
  points: number;
  achievements: AchievementResponse[];
}

export interface UserResponse {
  userId: number | null;
  name: string;
  username: string | null;
}

export interface App
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  title: string | null;
  description: string | null;
  apiKey: string | null;
  createdBy: User | null;
  id: number | null;
}

export interface Entry {
  rank: number;
  memberId: number;
  memberName: string;
  score: number;
}

export interface User
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  username: string;
  name: string;
  userActivities: UserActivity[];
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

export interface UserActivity
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  user: User | null;
  activity: Activity | null;
  points: number | null;
  userActivityAchievements: UserActivityAchievement[];
  userActivityProgresses: UserActivityProgress[];
  id: number | null;
}

export interface ExtendedSelfDirtinessTracker extends SelfDirtinessTracker {}

export interface ManagedMappedSuperclass extends Managed {}

export interface Managed extends PrimeAmongSecondarySupertypes {}

export interface PrimeAmongSecondarySupertypes {}

export interface Activity
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string;
  description: string | null;
  app: App | null;
  rules: Rule[];
  members: UserActivity[];
  userActivityPointHistories: UserActivityPointHistory[];
  activityId: number | null;
}

export interface UserActivityAchievement
  extends ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  achievement: Achievement | null;
  userActivity: UserActivity | null;
  completedAt: Date | null;
  id: number | null;
}

export interface UserActivityProgress
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  activity: Activity | null;
  userActivity: UserActivity | null;
  customUnit: CustomUnit | null;
  progressValue: number | null;
  id: number | null;
}

export interface Rule
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string | null;
  description: string | null;
  activity: Activity | null;
  trigger: Trigger | null;
  triggerType: TriggerType | null;
  action: Action | null;
  triggerTypeParams: { [index: string]: any } | null;
  actionParams: { [index: string]: any } | null;
  triggerParams: { [index: string]: any } | null;
  id: number | null;
}

export interface UserActivityPointHistory
  extends ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  userActivity: UserActivity | null;
  activity: Activity | null;
  points: number | null;
  description: string | null;
  id: number | null;
}

export interface Achievement
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string;
  description: string | null;
  imageUrl: string | null;
  userActivityAchievements: UserActivityAchievement[];
  activity: Activity | null;
  achievementId: number | null;
}

export interface CustomUnit
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  name: string | null;
  key: string | null;
  userActivityProgresses: UserActivityProgress[];
  id: number | null;
}

export interface Trigger
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string | null;
  description: string | null;
  rules: Rule[];
  key: string | null;
  jsonSchema: { [index: string]: any } | null;
  app: App | null;
  id: number | null;
}

export interface TriggerType
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string | null;
  description: string | null;
  jsonSchema: { [index: string]: any } | null;
  rules: Rule[];
  key: string | null;
  id: number | null;
}

export interface Action
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string | null;
  description: string | null;
  jsonSchema: { [index: string]: any } | null;
  rules: Rule[];
  key: string | null;
  id: number | null;
}
