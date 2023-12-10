import {ActivityAddMembersRequest, ActivityResponse, AppUserResponse, MemberResponse} from "@/types/generated-types";
import {API_URL, makeAppAuthHeader} from "@/hooks/api";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {useRouter} from "next/navigation";
import {useCurrentApp} from "@/lib/current-app";
import {ApiError} from "@/types";
import {toast} from "@/components/ui/use-toast";
import {Button} from "@/components/ui/button";
import {Dialog, DialogClose, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog";
import {Icons} from "@/components/icons";
import {CardDescription, CardTitle} from "@/components/ui/card";
import * as React from "react";
import {useMemo, useState} from "react";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {Label} from "@/components/ui/label";

type Props = {
  activityId: number;
};

// select only one member frop a select dropdown
export default function MemberAddForm({activityId}: Props) {
  const router = useRouter();
  const [isOpen, setIsOpen] = useState(false);
  const {currentApp} = useCurrentApp()
  const queryClient = useQueryClient();
  const [selectedUserId, setSelectedUserId] = useState<string>("");

  type MutationData = {
    activityId: number,
    newMember: ActivityAddMembersRequest
  }

  async function addMember(activityId: number, newMember: ActivityAddMembersRequest): Promise<ActivityResponse> {
    const response = await fetch(`${API_URL}/activities/${activityId}/members`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(makeAppAuthHeader(currentApp)),
      },
      body: JSON.stringify(newMember),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    return response.json();
  }

  async function fetchMembers(activityId: number): Promise<MemberResponse[]> {
    const response = await fetch(`${API_URL}/activities/${activityId}/members`, {
      headers: {
        'Content-Type': 'application/json',
        ...(makeAppAuthHeader(currentApp)),
      }
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    return response.json();
  }

  async function fetchAppUsers(): Promise<AppUserResponse[]> {
    const response = await fetch(`${API_URL}/apps/users`, {
      headers: {
        'Content-Type': 'application/json',
        ...(makeAppAuthHeader(currentApp)),
      }
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    return response.json();
  }

  const membersQuery = useQuery({
    queryKey: ['members', activityId], queryFn: () => fetchMembers(Number(activityId))
  });

  const appUsersQuery = useQuery({
    queryKey: ['appUsers'], queryFn: () => fetchAppUsers()
  });

  const availableAppUsers = useMemo(() => {
    const memberUserIds = new Set(membersQuery.data?.map((member) => member.userId));
    return appUsersQuery.data?.filter((user) => !memberUserIds.has(user.userId)) || [];
  }, [appUsersQuery.data, membersQuery.data]);

  const membersMutation = useMutation<ActivityResponse, ApiError, MutationData>(
    {
      mutationFn: ({activityId, newMember}: MutationData) => addMember(activityId, newMember),
      onSuccess: () => {
        queryClient.invalidateQueries({queryKey: ['members', activityId]});

        toast({
          title: "Member added",
          description: "Your member was added successfully.",
        });
      },
      onError: () => {
        toast({
          title: "Something went wrong.",
          description: "Your member was not added. Please try again.",
          variant: "destructive",
        });
      }
    }
  );

  function handleSubmit() {
    membersMutation.mutate({
      activityId,
      newMember: {
        userIds: [selectedUserId]
      }
    });
  }

  return (
    <>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <Button>
            <Icons.add className="mr-2 h-4 w-4"/>
            Add Member
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Add Member</CardTitle>
            <CardDescription>Choose a user from your app to add as a member of this activity.</CardDescription>
          </DialogHeader>
          <form onSubmit={handleSubmit}>
            <Label>
              Select a User
            </Label>
            <Select
              value={selectedUserId}
              onValueChange={setSelectedUserId}
              required
              disabled={availableAppUsers.length === 0}
            >
              <SelectTrigger>
                {availableAppUsers.length === 0 ? (
                  <SelectValue placeholder="All users are already members"
                  />
                ) : (
                  <SelectValue placeholder="Select a user"/>
                )}
              </SelectTrigger>
              <SelectContent>
                {availableAppUsers.map((user) => (
                  <SelectItem value={user.userId} key={user.userId}>{user.displayName}</SelectItem>
                ))}
              </SelectContent>
            </Select>

            <div className="flex justify-between mt-8">
              <DialogClose asChild>
                <Button variant="outline" type="button">
                  Cancel
                </Button>
              </DialogClose>
              <Button type="submit"
                      disabled={membersMutation.isPending}
              >Add</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </>
  );
}