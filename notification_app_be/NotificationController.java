package com.example.affordMedical.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private int getWeight(String type) {
        switch (type) {
            case "Placement": return 3;
            case "Result": return 2;
            case "Event": return 1;
            default: return 0;
        }
    }

    @GetMapping("/priority")
    public ResponseEntity<List<Map<String, Object>>> getPriorityInbox(
            @RequestParam(defaultValue = "10") int n) {

        // Notification data fetched from evaluation service
        List<Map<String, Object>> notifications = new ArrayList<>();

        notifications.add(Map.of("ID","7adb5e3b-80f0-49c3-8310-001ef2e540b7","Type","Placement","Message","Nvidia Corporation hiring","Timestamp","2026-06-02 23:59:21"));
        notifications.add(Map.of("ID","79d63546-275d-4557-8f72-4fe3cba6db7e","Type","Result","Message","external","Timestamp","2026-06-02 12:59:07"));
        notifications.add(Map.of("ID","5371ae39-aa52-4e38-af93-e52d64be94bd","Type","Result","Message","end-sem","Timestamp","2026-06-02 15:58:53"));
        notifications.add(Map.of("ID","35b0b768-8f24-465a-828a-7614b5bade28","Type","Placement","Message","Nvidia Corporation hiring","Timestamp","2026-06-02 08:28:39"));
        notifications.add(Map.of("ID","bd31dff9-e0f0-4339-9871-14c37e8a31cb","Type","Event","Message","induction","Timestamp","2026-06-02 13:28:25"));
        notifications.add(Map.of("ID","427011c1-a5e1-4125-afc7-c96e4796cf87","Type","Event","Message","cult-fest","Timestamp","2026-06-03 05:58:11"));
        notifications.add(Map.of("ID","a41f1ed2-3103-47b1-9b22-cd5adcc3ad74","Type","Event","Message","traditional-day","Timestamp","2026-06-02 10:27:57"));
        notifications.add(Map.of("ID","48afafe6-4ce8-44ca-bcae-bcaff5b0a11e","Type","Placement","Message","Amazon.com Inc. hiring","Timestamp","2026-06-02 19:57:43"));
        notifications.add(Map.of("ID","b983326b-425f-4f8f-9ffd-5afdb2510d36","Type","Placement","Message","Alphabet Inc. Class C hiring","Timestamp","2026-06-02 07:57:29"));
        notifications.add(Map.of("ID","d42b9585-0db7-4c7c-ac97-135c0246b018","Type","Event","Message","cult-fest","Timestamp","2026-06-02 08:57:15"));
        notifications.add(Map.of("ID","d98d194f-d4af-4a16-8dd9-a186faa4e6f2","Type","Placement","Message","Tesla Inc. hiring","Timestamp","2026-06-02 10:27:01"));
        notifications.add(Map.of("ID","66d826ac-ea26-4ba2-abca-ffb094827628","Type","Result","Message","external","Timestamp","2026-06-02 18:56:47"));
        notifications.add(Map.of("ID","63dbb35d-a8f5-4bba-86bb-241f68bf2fa1","Type","Placement","Message","Eli Lilly and Company hiring","Timestamp","2026-06-02 11:56:33"));
        notifications.add(Map.of("ID","ae779fdd-ab98-4cde-9380-11cf4d6ef7f3","Type","Result","Message","end-sem","Timestamp","2026-06-02 13:56:19"));
        notifications.add(Map.of("ID","019ab0c0-e9eb-43f0-8c08-5933db0882f6","Type","Event","Message","traditional-day","Timestamp","2026-06-02 20:56:05"));
        notifications.add(Map.of("ID","a500c09b-48d2-4edc-8e55-a76d252d7d03","Type","Event","Message","tech-fest","Timestamp","2026-06-02 10:25:51"));
        notifications.add(Map.of("ID","12575bb0-ea72-43e2-871c-8b7494d27998","Type","Result","Message","internal","Timestamp","2026-06-02 17:25:37"));
        notifications.add(Map.of("ID","d6e36d34-f0a5-4d3c-98b3-2ac451a9cf1b","Type","Placement","Message","Tesla Inc. hiring","Timestamp","2026-06-02 15:25:23"));
        notifications.add(Map.of("ID","05484e12-8144-45b7-ac31-75238ba9a81d","Type","Placement","Message","Booking Holdings Inc. hiring","Timestamp","2026-06-03 05:25:09"));
        notifications.add(Map.of("ID","22eb7ba2-9d52-481e-aa49-3152ea7020fa","Type","Result","Message","external","Timestamp","2026-06-02 14:24:55"));

        // Sort by weight desc then timestamp desc
        notifications.sort((a, b) -> {
            int wa = getWeight((String) a.get("Type"));
            int wb = getWeight((String) b.get("Type"));
            if (wb != wa) return wb - wa;
            return ((String) b.get("Timestamp")).compareTo((String) a.get("Timestamp"));
        });

        // Add weight field and take top N
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, notifications.size()); i++) {
            Map<String, Object> notif = new LinkedHashMap<>(notifications.get(i));
            notif.put("Weight", getWeight((String) notif.get("Type")));
            result.add(notif);
        }

        return ResponseEntity.ok(result);
    }
}