"use client";
import { Button } from "@/components/ui/button";
import { Image } from "@nextui-org/react";
import { useRewards } from "@/hooks";
import { RewardResponse } from "@/types/generated-types";
import { getImageUrlFromKey } from "@/lib/image";
import { useEffect } from "react";

// this page is intended to be embedded in an iframe
export default function ShopPage() {
  const { data: rewards, isLoading } = useRewards();

  useEffect(() => {
    // get auth token from url
    const urlParams = new URLSearchParams(window.location.search);
    const authToken = urlParams.get("token");
    console.log("authToken", authToken);
  }, []);

  if (isLoading) return "Loading...";

  function handleRedeemClick(reward: RewardResponse) {
    console.log("Redeem reward", reward);
    // notify parent frame
    window.postMessage({ type: "redeem-reward", reward }, "*");
  }

  return (
    <div>
      <h1>Shop</h1>
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {rewards?.map((reward) => (
          <RewardItem
            key={reward.id}
            reward={reward}
            onRedeemClick={handleRedeemClick}
          />
        ))}
      </div>
    </div>
  );
}

interface RewardItemProps {
  reward: RewardResponse;
  disabled?: boolean;
  onRedeemClick?: (reward: RewardResponse) => void;
}

function RewardItem({ reward, onRedeemClick, disabled }: RewardItemProps) {
  const isUnlimited = reward.quantity === null;
  const isOutOfStock = reward.quantity === 0;
  const isDisabled = disabled || isOutOfStock;

  const quantityText = isUnlimited
    ? "Unlimited"
    : isOutOfStock
      ? "Out of stock"
      : `${reward.quantity} Available`;

  return (
    <div className="relative group">
      <Image
        alt={reward.name || "Reward"}
        src={reward.imageKey ? getImageUrlFromKey(reward.imageKey) : ""}
        fallbackSrc="https://via.placeholder.com/200x200"
        width={200}
        height={200}
      />
      <div className="flex-1 py-4">
        <h3 className="font-semibold text-lg md:text-xl">{reward.name}</h3>
        <h4 className="text-sm text-gray-500 dark:text-gray-400">
          {reward.price} {reward.price === 1 ? "Point" : "Points"}
        </h4>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          {quantityText}
        </p>
        <Button
          className="mt-2"
          size="sm"
          onClick={onRedeemClick?.bind(null, reward)}
          disabled={isDisabled}
        >
          Redeem
        </Button>
      </div>
    </div>
  );
}
