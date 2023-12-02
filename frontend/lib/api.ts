import { API_URL } from "../hooks/api";

export function fetchRules(activityId: number) {
  return fetch(`${API_URL}/activities/${activityId}/rules`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    next: {
      tags: [`rules`],
    },
  });
}

// //   function useTriggers() {
//   const [triggers, setTriggers] = React.useState<Trigger[]>([]);

//   React.useEffect(() => {
//     async function fetchTriggers() {
//       const response = await fetch(
//         `${API_URL}/activities/${id}/triggers`,
//         {
//           method: "GET",
//           headers: {
//             "Content-Type": "application/json",
//           },
//           next: {
//             tags: ["triggers"],
//           },
//         }
//       );

//       if (!response?.ok) {
//         return toast({
//           title: "Something went wrong.",
//           description: "Your activity was not created. Please try again.",
//           variant: "destructive",
//         });
//       }

//       const triggers: Trigger[] = await response.json();
//       setTriggers(triggers);
//     }

//     fetchTriggers();
//   }, []);

//   return triggers;
// }

// function useActions() {
//   const [actions, setActions] = React.useState<Action[]>([]);

//   React.useEffect(() => {
//     async function fetchActions() {
//       const response = await fetch(
//         `${API_URL}/activities/actions`,
//         {
//           method: "GET",
//           headers: {
//             "Content-Type": "application/json",
//           },
//           next: {
//             tags: ["actions"],
//           },
//         }
//       );

//       if (!response?.ok) {
//         return toast({
//           title: "Something went wrong.",
//           description: "Your activity was not created. Please try again.",
//           variant: "destructive",
//         });
//       }

//       const actions: Action[] = await response.json();
//       setActions(actions);
//     }

//     fetchActions();
//   }, []);

//   return actions;
// }

export function fetchTriggers(appId: number) {
  return fetch(
    `${API_URL}/apps/triggers?appId={${appId}}`, // TODO: remove param
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      next: {
        tags: [`triggers`],
      },
    }
  );
}

// export function fetchActions()

// fetch members
export function fetchMembers(activityId: number) {
  return fetch(`${API_URL}/activities/${activityId}/members`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    next: {
      tags: [`members`],
    },
  });
}
