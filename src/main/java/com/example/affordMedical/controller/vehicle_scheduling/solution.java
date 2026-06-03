package com.example.affordMedical.controller.vehicle_scheduling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RestController
@RequestMapping("/vehicle-scheduling")
public class solution {

    private static final String BASE = "http://4.224.186.213/evaluation-service";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiJtb2hkbXVydGF6YXphaWRpOTg3QGdtYWlsLmNvbSIsImV4cCI6MTc4MDQ2NjU3MiwiaWF0IjoxNzgwNDY1NjcyLCJpc3MiOiJBZmZvcmQgTWVkaWNhbCBUZWNobm9sb2dpZXMgUHJpdmF0ZSBMaW1pdGVkIiwianRpIjoiMmUzMTM4ZTMtNjE2NS00NGNmLTg1MjctYjA3Mjg1ZGMzNTYxIiwibG9jYWxlIjoiZW4tSU4iLCJuYW1lIjoibW9oZCBtdXJ0YXphIHphaWRpIiwic3ViIjoiOWJkZTVlZGEtZjRjZi00YTQ2LWJhNTYtZmM5ZGNiMDk0ZWM4In0sImVtYWlsIjoibW9oZG11cnRhemF6YWlkaTk4N0BnbWFpbC5jb20iLCJuYW1lIjoibW9oZCBtdXJ0YXphIHphaWRpIiwicm9sbE5vIjoiMjMwMjkwMTU0MDA3NCIsImFjY2Vzc0NvZGUiOiJzZFdXZ2MiLCJjbGllbnRJRCI6IjliZGU1ZWRhLWY0Y2YtNGE0Ni1iYTU2LWZjOWRjYjA5NGVjOCIsImNsaWVudFNlY3JldCI6IkRzek1Tc1VKUmhDanFEZ0QifQ.Ks6gaFAxsoy-bTtF3luMoK0nkbU25I_pbvxyjl5mZpI";

    private String fetch(String endpoint) throws Exception {
        URL url = new URL(BASE + endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + TOKEN);
        Scanner sc = new Scanner(con.getInputStream());
        StringBuilder sb = new StringBuilder();
        while (sc.hasNext()) sb.append(sc.nextLine());
        sc.close();
        return sb.toString();
    }

    private List<String> parseStrings(String json, String key) {
        List<String> values = new ArrayList<>();
        String search = "\"" + key + "\":\"";
        int idx = 0;
        while ((idx = json.indexOf(search, idx)) != -1) {
            idx += search.length();
            int end = json.indexOf("\"", idx);
            values.add(json.substring(idx, end));
        }
        return values;
    }

    private List<Integer> parseInts(String json, String key) {
        List<Integer> values = new ArrayList<>();
        String search = "\"" + key + "\":";
        int idx = 0;
        while ((idx = json.indexOf(search, idx)) != -1) {
            idx += search.length();
            int end = json.indexOf(",", idx);
            int end2 = json.indexOf("}", idx);
            if (end == -1 || end2 < end) end = end2;
            values.add(Integer.parseInt(json.substring(idx, end).trim()));
        }
        return values;
    }

    @GetMapping("/solve")
    public ResponseEntity<String> solve() throws Exception {

        String depotJson   = fetch("/depots");
        String vehicleJson = fetch("/vehicles");
        List<String>  taskIDs   = parseStrings(vehicleJson, "TaskID");
        List<Integer> durations = parseInts(vehicleJson, "Duration");
        List<Integer> impacts   = parseInts(vehicleJson, "Impact");
        int n = taskIDs.size();
        List<Integer> depotIDs = parseInts(depotJson, "ID");
        List<Integer> budgets  = parseInts(depotJson, "MechanicHours");

        StringBuilder result = new StringBuilder();
        result.append("{\n  \"depots\": [\n");

        for (int d = 0; d < depotIDs.size(); d++) {
            int depotId = depotIDs.get(d);
            int budget  = budgets.get(d);
            int[][] dp = new int[n + 1][budget + 1];
            for (int i = 1; i <= n; i++) {
                int dur = durations.get(i - 1);
                int imp = impacts.get(i - 1);
                for (int w = 0; w <= budget; w++) {
                    dp[i][w] = dp[i - 1][w];
                    if (dur <= w)
                        dp[i][w] = Math.max(dp[i][w], dp[i-1][w - dur] + imp);
                }
            }
            List<String> selected = new ArrayList<>();
            int w = budget;
            for (int i = n; i > 0; i--) {
                if (dp[i][w] != dp[i-1][w]) {
                    selected.add("\"" + taskIDs.get(i-1) + "\"");
                    w -= durations.get(i - 1);
                }
            }
            result.append("    {\n");
            result.append("      \"depotId\": ").append(depotId).append(",\n");
            result.append("      \"budget\": ").append(budget).append(",\n");
            result.append("      \"maxImpact\": ").append(dp[n][budget]).append(",\n");
            result.append("      \"selectedTasks\": ").append(selected).append("\n");
            result.append("    }");
            if (d < depotIDs.size() - 1) result.append(",");
            result.append("\n");
        }
        result.append("  ]\n}");
        return ResponseEntity.ok(result.toString());
    }
}