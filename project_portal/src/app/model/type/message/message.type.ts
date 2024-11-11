import { Dayjs } from "dayjs";

export interface Message {
  guid?: string,
  message?: string,
  senderId?: string,
  senderName?: string,
  conversationId?: string,
  createdAt?: Dayjs,
}
