"use client";
import * as React from "react";
import { Button } from "@/components/ui/button";
import { CardTitle } from "@/components/ui/card";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "@/components/ui/dialog";
import { toast } from "./ui/use-toast";
import { useRouter } from "next/navigation";
import { Icons } from "@/components/icons";
import { useCreateReward, useSetRewardImage } from "@/hooks";
import {
  RewardResponse,
  ValidationLengths,
  ValidationMessages,
} from "@/types/generated-types";
import { isAlphaNumericExtended } from "@/lib/validations";
import { InputWithCounter } from "@/components/ui/input-with-counter";
import { TextareaWithCounter } from "@/components/ui/textarea-with-counter";
import { isApiError, isApiValidationError } from "@/api";
import { HttpStatusCode } from "axios";
import { Input } from "@/components/ui/input";
import { Image } from "@nextui-org/react";
import { DevTool } from "@hookform/devtools";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";

const MAX_FILE_SIZE_MB = 1000000;
const MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 2; // 2MB

const ACCEPTED_IMAGE_TYPES = [
  "image/jpeg",
  "image/jpg",
  "image/png",
  "image/webp",
];

export const rewardCreateSchema = z.object({
  name: z
    .string({
      required_error: ValidationMessages.NAME_REQUIRED,
    })
    .trim()
    .min(4, { message: ValidationMessages.NAME_LENGTH })
    .max(200, { message: ValidationMessages.NAME_LENGTH })
    .refine((value) => isAlphaNumericExtended(value, true), {
      message: ValidationMessages.NAME_PATTERN,
    }),
  description: z
    .string()
    .max(500, { message: ValidationMessages.DESCRIPTION_LENGTH })
    .optional(),
  price: z.coerce
    .number({
      required_error: "Price is required",
      invalid_type_error: "Price must be a positive integer",
    })
    .int({ message: "Price must be a positive integer" })
    .positive({ message: "Price must be a positive integer" })
    .refine((value) => value > 0, {
      message: "Price must be a positive number",
    }),
  quantity: z.coerce
    .number({
      required_error: "Quantity must be a positive number",
      invalid_type_error: "Quantity must be a positive number",
    })
    .int({ message: "Quantity must be a positive number" })
    .positive({ message: "Quantity must be a positive number" })
    .or(z.literal("")),
  image: z
    .custom<FileList>((files) => files instanceof FileList, {
      message: "Image is required",
    })
    .refine(
      (files) => {
        const file = files?.[0];
        if (!file) {
          return true;
        }
        console.log("file", file);
        console.log("file size", file.size);
        console.log("MAX_FILE_SIZE_BYTES", MAX_FILE_SIZE_BYTES);
        return file?.size <= MAX_FILE_SIZE_BYTES;
      },
      `Max image size is ${MAX_FILE_SIZE_BYTES / MAX_FILE_SIZE_MB}MB`,
    )
    .refine((files) => {
      const file = files?.[0];
      if (!file) {
        return true;
      }
      console.log("ACCEPTED_IMAGE_TYPES", ACCEPTED_IMAGE_TYPES);
      console.log("file.type", file.type);
      return ACCEPTED_IMAGE_TYPES.includes(file.type);
    }, "Only .jpg, .jpeg, .png and .webp formats are supported."),
});

type FormData = z.infer<typeof rewardCreateSchema>;

export function RewardCreateForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(rewardCreateSchema),
    defaultValues: {
      name: "",
      description: "",
    },
  });
  const [isOpen, setIsOpen] = React.useState(false);
  const createMutation = useCreateReward();
  const setImageMutation = useSetRewardImage();
  const [preview, setPreview] = React.useState("");
  const isSubmitting = createMutation.isPending || setImageMutation.isPending;
  const [isUnlimited, setIsUnlimited] = React.useState(false);

  async function onSubmit(values: FormData) {
    createMutation.mutate(
      {
        name: values.name,
        description: values.description || null,
        price: values.price,
        quantity: values.quantity === "" ? null : values.quantity,
      },
      {
        onSuccess: async (data) => {
          await handleImageUpload(data, values.image);

          toast({
            title: "Reward created",
            description: "Reward created successfully",
          });

          setIsOpen(false);
          form.reset();
          setPreview("");

          router.refresh();
        },
        onError: (error, b, c) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            // TODO: handle validation errors
          }
          if (isApiError(error)) {
            // root error
            if (error.response?.status === HttpStatusCode.Conflict) {
              form.setError("name", {
                message: error.response.data.message,
              });
            }
          }
        },
      },
    );
  }

  async function handleImageUpload(
    reward: RewardResponse,
    files: FileList | undefined,
  ) {
    // if image is present, upload it, otherwise skip
    const image = files?.[0];
    if (!files || !image) {
      return;
    }

    return setImageMutation.mutateAsync(
      {
        rewardId: reward.id,
        image: image,
      },
      {
        onSuccess: () => {},
        onError: (error) => {
          console.log("error", error);
          if (isApiValidationError(error)) {
            // TODO: handle validation errors
          }
        },
      },
    );
  }

  function getImageData(event: React.ChangeEvent<HTMLInputElement>) {
    // FileList is immutable, so we need to create a new one
    const dataTransfer = new DataTransfer();

    if (event.target.files === null) {
      return { files: dataTransfer.files, displayUrl: "" };
    }

    // Add newly uploaded images
    Array.from(event.target.files).forEach((image) =>
      dataTransfer.items.add(image),
    );

    const files = dataTransfer.files;
    const displayUrl = URL.createObjectURL(event.target.files![0]);

    return { files, displayUrl };
  }

  return (
    <>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <Button>
            <Icons.add className="mr-2 h-4 w-4" />
            New Reward
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <CardTitle>Create reward</CardTitle>
          </DialogHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Name</FormLabel>
                    <FormControl>
                      <InputWithCounter
                        placeholder="Name of your reward"
                        maxLength={ValidationLengths.TITLE_MAX_LENGTH}
                        {...field}
                      />
                    </FormControl>
                    <FormDescription />
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Description</FormLabel>
                    <FormControl>
                      <TextareaWithCounter
                        placeholder="Description of your reward"
                        maxLength={ValidationLengths.DESCRIPTION_MAX_LENGTH}
                        {...field}
                      />
                    </FormControl>
                    <FormDescription />
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="price"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Price</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        placeholder="Price of your reward"
                        {...field}
                        step="1"
                        min="1"
                      />
                    </FormControl>
                    <FormDescription />
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="quantity"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Quantity</FormLabel>
                    {!isUnlimited && (
                      <FormControl>
                        <Input
                          type="number"
                          placeholder="Quantity of your reward"
                          step="1"
                          min="1"
                          {...field}
                        />
                      </FormControl>
                    )}

                    <FormDescription />
                    <FormMessage />
                    <div className="flex items-center space-x-2 p-2 pb-4">
                      <Checkbox
                        checked={isUnlimited}
                        onCheckedChange={(checked) => {
                          setIsUnlimited(!!checked);
                          form.setValue("quantity", "");
                        }}
                        id="unlimited-quantity"
                      ></Checkbox>
                      <Label htmlFor="unlimited-quantity">Unlimited</Label>
                    </div>
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="image"
                render={({ field: { onChange, value, ...rest } }) => (
                  <FormItem>
                    <FormLabel>Image</FormLabel>
                    <FormControl>
                      <Input
                        type="file"
                        {...rest}
                        accept={ACCEPTED_IMAGE_TYPES.join(", ")}
                        onChange={(event) => {
                          const { files, displayUrl } = getImageData(event);
                          console.log("files", files);
                          console.log("displayUrl", displayUrl);
                          setPreview(displayUrl);
                          onChange(files);
                        }}
                      />
                    </FormControl>
                    <FormDescription />
                    <FormMessage />
                  </FormItem>
                )}
              />

              <div className="flex justify-center mt-4">
                <Image src={preview} alt="reward image preview" />
              </div>

              <div className="flex justify-between mt-8">
                <DialogClose asChild>
                  <Button variant="outline" type="button">
                    Cancel
                  </Button>
                </DialogClose>
                <Button type="submit" disabled={isSubmitting}>
                  Create
                </Button>
              </div>
            </form>
            <DevTool control={form.control} />
          </Form>
        </DialogContent>
      </Dialog>
    </>
  );
}
