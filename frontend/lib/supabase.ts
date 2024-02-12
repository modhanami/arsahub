import { createClient } from "@supabase/supabase-js";

const supabaseUrl = "https://hbioyxpzykniktpsdmoy.supabase.co";
const supabaseKey = process.env.NEXT_PUBLIC_SUPABASE_KEY || "public";
export const supabase = createClient(supabaseUrl, supabaseKey);
