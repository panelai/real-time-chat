import { Dayjs } from "dayjs";
import { ConversationType } from "../../enums/conversation/conversation-type.enum"
import { Message } from "../message/message.type";
import { Account } from "../../../core/user/account.type";
// import { MessageInfo } from "../account/message-info.type";

export interface Conversation {
  id?: string,
  title?: string,
  type?: ConversationType,
  groupAdmin?: string,
  messages?: Array<Message>,
  memberIds?: Array<String>,
  members?: Array<Account>
  createdAt?: Dayjs,

  isActived?: boolean
}
