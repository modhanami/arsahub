export function fetchRules(activityId: number) {
  return fetch(`http://localhost:8080/api/activities/${activityId}/rules`, {
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
//         `http://localhost:8080/api/activities/${id}/triggers`,
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
//         `http://localhost:8080/api/activities/actions`,
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

export function fetchTriggers(activityId: number) {
  return fetch(`http://localhost:8080/api/activities/${activityId}/triggers`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    next: {
      tags: [`triggers`],
    },
  });
}

// export function fetchActions()

// fetch members
export function fetchMembers(activityId: number) {
  return fetch(`http://localhost:8080/api/activities/${activityId}/members`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    next: {
      tags: [`members`],
    },
  });
}
