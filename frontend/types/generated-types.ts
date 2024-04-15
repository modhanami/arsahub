/* tslint:disable */
/* eslint-disable */

// Generated using typescript-generator version 3.2.1263 on 2024-04-15 23:37:06.

export interface AchievementCreateRequest {
  title: string;
  description: string | null;
}

export interface AchievementSetImageRequest {
  achievementId: number;
  image: MultipartFile;
}

export interface Action {
  key: string;
}

export interface AddPointsAction extends Action {
  points: number;
}

export interface AddPointsExpressionAction extends Action {
  pointsExpression: string;
}

export interface AppUserCreateRequest {
  uniqueId: string;
  displayName: string;
}

export interface AppUserUpdateRequest {
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
  params: { [index: string]: any } | null;
  key: string | null;
}

export interface RewardCreateRequest {
  price: number | null;
  quantity: number | null;
  maxUserRedemptions: number | null;
  name: string | null;
  description: string | null;
}

export interface RewardRedeemRequest {
  rewardId: number;
  userId: string;
}

export interface RewardSetImageRequest {
  rewardId: number;
  image: MultipartFile;
}

export interface RuleCreateRequest {
  trigger: KeyAndParams;
  action: KeyAndParams;
  conditionExpression: string | null;
  title: string | null;
  description: string | null;
  repeatability: string | null;
  accumulatedFields: string[] | null;
}

export interface RuleCreateRequestKt {}

export interface RuleUpdateRequest {
  title: string | null;
  description: string | null;
}

export interface TriggerCreateRequest {
  fields: FieldDefinition[] | null;
  title: string | null;
  description: string | null;
}

export interface TriggerSendRequest {
  params: { [index: string]: any } | null;
  key: string | null;
  userId: string | null;
}

export interface TriggerUpdateRequest {
  title: string | null;
  description: string | null;
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

export interface WebhookCreateRequest {
  url: string | null;
}

export interface AchievementResponse {
  achievementId: number;
  title: string;
  description: string | null;
  imageKey: string | null;
  imageMetadata: { [index: string]: any } | null;
}

export interface AchievementWithUnlockCount {
  achievement: Achievement;
  count: number;
}

export interface AchievementWithUnlockCountResponse {
  achievement: AchievementResponse;
  count: number;
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
  createdAt: Date | null;
  updatedAt: Date | null;
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

export interface RewardResponse {
  id: number | null;
  name: string | null;
  description: string | null;
  price: number | null;
  quantity: number | null;
  maxUserRedemptions: number | null;
  imageKey: string | null;
  imageMetadata: { [index: string]: any } | null;
}

export interface RewardResponseWithCount {
  reward: RewardResponse;
  count: number;
}

export interface RewardWithCount {
  reward: Reward;
  count: number;
}

export interface RuleResponse {
  createdAt: Date | null;
  updatedAt: Date | null;
  title: string | null;
  description: string | null;
  trigger: TriggerResponse | null;
  action: string | null;
  actionPoints: number | null;
  actionPointsExpression: string | null;
  actionAchievement: AchievementResponse | null;
  id: number | null;
  repeatability: string | null;
  conditionExpression: string | null;
  accumulatedFields: string[] | null;
}

export interface SignupResponse {
  accessToken: string;
}

export interface TransactionResponse {
  id: number | null;
  pointsSpent: number | null;
  createdAt: number | null;
  referenceNumber: string | null;
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

export interface TriggerWithTriggerCount {
  trigger: Trigger;
  count: number;
}

export interface TriggerWithTriggerCountResponse {
  trigger: TriggerResponse;
  count: number;
}

export interface UserResponse {
  userId: number;
  internalUserId: number;
  externalUserId: string;
  googleUserId: string | null;
  email: string;
  name: string;
}

export interface WebhookPayload {
  id: string;
  appId: number;
  webhookUrl: string;
  event: string;
  appUserId: string;
  payload: { [index: string]: any };
}

export interface WebhookResponse {
  id: number;
  url: string;
  secretKey: string | null;
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

export interface SupabaseIdentity {
  supabaseUserId: string;
  googleUserId: string | null;
  email: string;
  name: string;
}

export interface UserIdentity {
  internalUserId: number;
  externalUserId: string;
  googleUserId: string | null;
  email: string;
  name: string;
}

export interface MultipartFile extends InputStreamSource {
  name: string;
  bytes: any;
  empty: boolean;
  resource: Resource;
  size: number;
  contentType: string;
  originalFilename: string;
}

export interface Achievement
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string;
  description: string | null;
  appUserAchievements: AppUserAchievement[];
  app: App | null;
  imageKey: string | null;
  imageMetadata: { [index: string]: any } | null;
  achievementId: number | null;
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

export interface Reward
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  app: App | null;
  name: string | null;
  description: string | null;
  price: number | null;
  type: RewardType | null;
  data: { [index: string]: any } | null;
  quantity: number | null;
  imageKey: string | null;
  imageMetadata: { [index: string]: any } | null;
  maxUserRedemptions: number | null;
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
  app: App | null;
  fields: TriggerField[];
  id: number | null;
}

export interface Resource extends InputStreamSource {
  open: boolean;
  file: any;
  readable: boolean;
  url: URL;
  description: string;
  filename: string;
  uri: URI;
  contentAsByteArray: any;
}

export interface InputStreamSource {
  inputStream: any;
}

export interface AppUserAchievement
  extends ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  achievement: Achievement | null;
  appUser: AppUser | null;
  completedAt: Date | null;
  app: App | null;
  id: number | null;
}

export interface AuditedEntity extends ManagedMappedSuperclass {
  createdAt: Date | null;
  updatedAt: Date | null;
}

export interface ManagedEntity extends Managed {}

export interface PersistentAttributeInterceptable
  extends PrimeAmongSecondarySupertypes {}

export interface ExtendedSelfDirtinessTracker extends SelfDirtinessTracker {}

export interface User
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  externalUserId: string | null;
  email: string | null;
  googleUserId: string | null;
  name: string | null;
  userId: number | null;
}

export interface SelfDirtinessTracker extends PrimeAmongSecondarySupertypes {}

export interface RewardType
  extends ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  id: number | null;
  key: string | null;
  name: string | null;
}

export interface Rule
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  title: string | null;
  description: string | null;
  trigger: Trigger | null;
  triggerParams: { [index: string]: any } | null;
  app: App | null;
  action: string | null;
  actionPoints: number | null;
  actionAchievement: Achievement | null;
  repeatability: string | null;
  conditionExpression: string | null;
  deletedAt: Date | null;
  accumulatedFields: string[] | null;
  actionPointsExpression: string | null;
  id: number | null;
}

export interface TriggerField
  extends ManagedEntity,
    PersistentAttributeInterceptable,
    SelfDirtinessTracker {
  key: string | null;
  type: string | null;
  label: string | null;
  trigger: Trigger | null;
  id: number | null;
}

export interface URL extends Serializable {}

export interface URI extends Comparable<URI>, Serializable {}

export interface AppUser
  extends AuditedEntity,
    ManagedEntity,
    PersistentAttributeInterceptable,
    ExtendedSelfDirtinessTracker {
  userId: string | null;
  displayName: string | null;
  app: App | null;
  points: number | null;
  achievements: AppUserAchievement[];
  user: User | null;
  id: number | null;
}

export interface ManagedMappedSuperclass extends Managed {}

export interface Managed extends PrimeAmongSecondarySupertypes {}

export interface PrimeAmongSecondarySupertypes {}

export interface Serializable {}

export interface Comparable<T> {}

export const enum AnalyticsConstants {
  TOTAL_UNLOCKED_ACHIEVEMENTS = "total-unlocked-achievements",
  TOP_10_ACHIEVEMENTS = "top-10-achievements",
  TOP_10_TRIGGERS = "top-10-triggers",
}

export const enum ValidationMessages {
  TITLE_REQUIRED = "Title is required",
  TITLE_LENGTH = "Title must be between 4 and 200 characters",
  TITLE_PATTERN = "Title must contain only alphanumeric characters, spaces, underscores, and dashes",
  NAME_REQUIRED = "Name is required",
  NAME_LENGTH = "Name must be between 4 and 200 characters",
  NAME_PATTERN = "Name must contain only alphanumeric characters, spaces, underscores, and dashes",
  DESCRIPTION_LENGTH = "Description cannot be longer than 500 characters",
  KEY_REQUIRED = "Key is required",
  KEY_LENGTH = "Key must be between 4 and 200 characters",
  KEY_PATTERN = "Key must contain only alphanumeric characters, underscores, and dashes",
  PASSWORD_REQUIRED = "Password is required",
  PASSWORD_LENGTH = "Password must be between 8 and 50 characters",
  TYPE_REQUIRED = "Type is required",
  LABEL_LENGTH = "Label must be between 4 and 200 characters",
  REPEATABILITY_REQUIRED = "Repeatability is required",
  APP_USER_UID_REQUIRED = "App user UID is required",
  APP_USER_UID_LENGTH = "UID must be between 4 and 200 characters",
  APP_USER_UID_PATTERN = "UID must contain only alphanumeric characters, underscores, and dashes",
  APP_USER_DISPLAY_NAME_REQUIRED = "Display name is required",
  APP_USER_DISPLAY_NAME_LENGTH = "Display name must be between 4 and 200 characters",
  APP_USER_DISPLAY_NAME_PATTERN = "Display name must contain only alphanumeric characters, underscores, and dashes",
}
