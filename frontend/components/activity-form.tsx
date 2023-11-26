import {
  CardTitle,
  CardDescription,
  CardHeader,
  CardContent,
  CardFooter,
  Card,
} from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

export default function ActivityForm() {
  return (
    <div className="flex-grow">
      <Card className="mx-auto">
        <CardHeader>
          <CardTitle>Activity Management</CardTitle>
          <CardDescription>Edit your activity</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Title</Label>
              <Input placeholder="Name of your activity" required />
            </div>

            <div className="space-y-2">
              <Label>Description</Label>
              <Input placeholder="Description of your activity" required />
            </div>
          </div>
        </CardContent>
        <CardFooter className="flex mt-4 space-x-2">
          <Button
            className="w-full bg-gray-800 hover:bg-gray-700 text-white "
            type="submit"
          >
            Update Activity
          </Button>
          <Button className="w-full bg-red-500 hover:bg-red-600 text-white">
            Delete Activity
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}
