/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-11-28 21:51:28.

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
    userId: string;
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
    integrationId: number | null;
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
    userId: string;
}

export interface ActivityUpdate {
}

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

export interface CustomUnitCreateRequest {
    name: string;
    key: string;
}

export interface CustomUnitResponse {
    name: string | null;
    key: string | null;
    id: number | null;
}

export interface IntegrationCreateRequest {
    name: string;
    createdBy: number;
    templateId: number | null;
}

export interface IntegrationResponse {
    id: number;
    name: string;
}

export interface IntegrationTemplateResponse {
    name: string | null;
    description: string | null;
    triggerTemplates: TriggerTemplateResponse[];
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
    userId: string | null;
    username: string | null;
}

export interface PointsUpdate extends ActivityUpdate {
    userId: string;
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
    externalSystem: IntegrationResponse | null;
    effectParams: { [index: string]: any } | null;
    triggerParams: { [index: string]: any } | null;
    id: number | null;
}

export interface TriggerCreateRequest {
    title: string | null;
    description: string | null;
    key: string | null;
    integrationId: number | null;
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

export interface Entry {
    rank: number;
    memberId: number;
    memberName: string;
    score: number;
}
