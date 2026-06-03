Stage 1

REST API Design - Campus Notification System
GET /notifications
Fetch all notifications for logged-in student.
Headers: Authorization: Bearer <token>
Response 200:**
{
  "notifications": [
    {
      "id": "uuid",
      "type": "Placement | Event | Result",
      "message": "string",
      "isRead": false,
      "createdAt": "2026-04-22T17:51:38"
    }
  ]
pATCH /notifications/:id/read
Mark a notification as read.
Response 200: { "success": true }

GET /notifications/unread
Fetch only unread notifications.

POST /notifications
Admin sends new notification.
Body: { "type": "Placement", "message": "string", "studentId": "uuid" }
Response 201:** { "id": "uuid", "message": "created" }

Stage 2

Database Schema

Chosen DB: PostgreSQL (Relational)

notifications table

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    type VARCHAR(20) CHECK (type IN ('Event', 'Result', 'Placement')),
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW()
);


Problems at scale (50,000 students, 5,000,000 notifications)
 Full table scans on unread queries
 Slow ORDER BY created_at without index

Solutions
 Index on student_id + is_read
 Partition table by created_at (monthly)


Stage 3

Query Fix

The query is slow because there is no index and SELECT * is used.

Fixed query:

SELECT id, type, message, createdAt 
FROM notifications
WHERE studentID = 1042 AND isRead = false
ORDER BY createdAt DESC;


Adding index:

CREATE INDEX idx_student ON notifications(studentID, isRead);


For placement notifications in last 7 days:

SELECT * FROM notifications
WHERE type = 'Placement'
AND createdAt >= NOW() - INTERVAL 7 DAY;

Stage 4

Performance Fix

The main problem is DB is being hit on every page load for every student.

I would use Redis caching. Store the notifications in cache for each student with a key . Set expiry of 60 seconds.
Stage 5

Bulk Notify Fix

Problems I see:
if send_email fails midway, remaining students dont get notified
saving to db one by one inside loop is slow
no error handling

What I would change:
first save all notifications to db in one  insert
 then use a queue to send emails so if one fails it retries
Stage 6

Priority Inbox

See implementation in notification_app_be folder.
Priority is based on type weight (Placement=3, Result=2, Event=1) and timestamp.
