package largescaleit_demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Controller
public class GitLabController {

    private static final String GITLAB_TOKEN = "glpat-3KKz1_gxpnziVVAL_2VJ";
    private static final String GROUP_ID = "45915";

    private final RestTemplate restTemplate = new RestTemplate();
    
    
    @GetMapping("/members")
    public String listMembers(Model model) {
        String url = String.format("https://git-ce.rwth-aachen.de/api/v4/groups/%s/members", GROUP_ID);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Private-Token", GITLAB_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> members = response.getBody();
        // model.addAttribute("members", members);
    
        int memberCount = (members != null) ? members.size() : 0;

      
        model.addAttribute("members", members);
        model.addAttribute("memberCount", memberCount);

        return "members";
    }
    public int getMemberCount() {
        String url = String.format("https://git-ce.rwth-aachen.de/api/v4/groups/%s/members", GROUP_ID);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Private-Token", GITLAB_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> members = response.getBody();
        return (members != null) ? members.size() : 0;
    }
    
    
    @PostMapping("/addMember")
    public String addMember(@RequestParam String username, 
                            @RequestParam String name, 
                            @RequestParam int accessLevel, 
                            Model model) {

      
        String userSearchUrl = "https://git-ce.rwth-aachen.de/api/v4/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Private-Token", GITLAB_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> userResponse = restTemplate.exchange(
                userSearchUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> users = userResponse.getBody();
        if (users.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        int userId = (int) users.get(0).get("id");

      
        String addUserUrl = String.format("https://git-ce.rwth-aachen.de/api/v4/groups/%s/members", GROUP_ID);

        
        headers.set("Content-Type", "application/json");

        String requestBody = String.format("{\"user_id\": \"%d\", \"access_level\": \"%d\"}", userId, accessLevel);

        HttpEntity<String> addEntity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(addUserUrl, HttpMethod.POST, addEntity, String.class);

        return "redirect:/members";
    }

    @PostMapping("/deleteMember/{id}")
    public String deleteMember(@PathVariable("id") int id) {
        String url = String.format("https://git-ce.rwth-aachen.de/api/v4/groups/%s/members/%d", GROUP_ID, id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Private-Token", GITLAB_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        } catch (Exception e) {
            
        }

        return "redirect:/members";
    }


}
