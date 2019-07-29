export class Notification {
  events: NotificationEvent[] = [];
  alerts: NotificationItem[] = [];
}

// test failed/passed
export class NotificationItem {
  source_id: string;
  result: boolean;
  message: string;
  date_at: string;
}

export class NotificationEvent {
  issuer_id: string;
  type: NotificationType;
  message: string;
  source_id: string;
  date_at: string;
}

export enum NotificationType {
  RUN_CANCELLED,
  TEST_MODIFIED,
  STEP_MODIFIED,
}
