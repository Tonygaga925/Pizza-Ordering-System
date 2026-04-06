package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Member;
import model.order.*;
import model.NormalState;
import model.VIPState;
import model.order.Order;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemberManager {
    private Map<String, Member> members;
    private Member currentMember;
    private final String memberFilePath;
    private final Gson gson;
    private OrderManager orderManager; 
    
    public MemberManager(String memberFilePath) throws IOException {
        this.memberFilePath = memberFilePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.members = new ConcurrentHashMap<>();
        this.currentMember = null;
        loadMembers();
    }
    
    private void loadMembers() throws IOException {
        File file = new File(memberFilePath);
        if (!file.exists()) {
            members = new ConcurrentHashMap<>();
            saveMembers();
            return;
        }
        
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Member>>(){}.getType();
            Map<String, Member> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                members = new ConcurrentHashMap<>(loaded);
                // Initialize state for each member after loading
                for (Member member : members.values()) {
                    member.initState();
                }
            } else {
                members = new ConcurrentHashMap<>();
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load members, starting with empty members.");
            members = new ConcurrentHashMap<>();
        }
    }
    
    private void saveMembers() throws IOException {
        try (Writer writer = new FileWriter(memberFilePath)) {
            gson.toJson(members, writer);
        }
    }
    
    public boolean login(String username, String password) throws IOException {
        for (Member member : members.values()) {
            if (member.getUsername().equals(username) && member.getPassword().equals(password)) {
                currentMember = member;
                if (currentMember.getState() == null) {
                    currentMember.initState();
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean register(String username, String password, String name, String phone) throws IOException {
        // Check if username exists
        for (Member member : members.values()) {
            if (member.getUsername().equals(username)) {
                return false;
            }
        }
        
        String id = generateMemberId();
        Member newMember = new Member(id, username, password, name, phone);
        members.put(id, newMember);
        saveMembers();
        return true;
    }
    
    private String generateMemberId() {
        int maxId = 0;
        for (String id : members.keySet()) {
            if (id.startsWith("M")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return String.format("M%03d", maxId + 1);
    }
    
    public void logout() {
        currentMember = null;
    }
    
    public boolean isLoggedIn() {
        return currentMember != null;
    }
    
    public Member getCurrentMember() {
        return currentMember;
    }
    
    public Member getMemberById(String memberId) throws IOException {
        if (currentMember != null && currentMember.getId().equals(memberId)) {
            return currentMember;
        }
        
        Member member = members.get(memberId);
        
        if (member == null) {
            loadMembers();
            member = members.get(memberId);
        }
        
        if (member != null && member.getState() == null) {
            member.initState();
        }
        
        return member;
    }
    
    public void updateMemberPoints(String memberId, int pointsToAdd) throws IOException {
        Member member = getMemberById(memberId);
        if (member != null) {
            String oldLevel = member.getLevel();
            member.addPoints(pointsToAdd);
            
            if (!oldLevel.equals(member.getLevel())) {
                System.out.println( member.getName() + " has been upgraded to VIP!");
                System.out.println("   10% discount will be applied to future orders!");
            }
            
            saveMembers();
            
            if (currentMember != null && currentMember.getId().equals(memberId)) {
                currentMember = member;
            }
        }
    }
    
public void displayMemberInfo() {
    if (currentMember != null) {
        System.out.println("\n=== Member Information ===");
        System.out.println("ID: " + currentMember.getId());
        System.out.println("Name: " + currentMember.getName());
        System.out.println("Username: " + currentMember.getUsername());
        System.out.println("Phone: " + currentMember.getPhone());
        System.out.println("Level: " + currentMember.getLevelDisplay());
        System.out.println("Points: " + currentMember.getPoints());
        System.out.println("Register Date: " + currentMember.getRegisterDate());
        
        int pointsToNext = currentMember.getPointsToNextLevel();
        if (pointsToNext > 0) {
            System.out.println("Points to VIP: " + pointsToNext + " more points");
        } else if ("VIP".equals(currentMember.getLevel())) {
            System.out.println("You are a VIP member! Enjoy 10% discount on all orders!");
        }
        System.out.println("=========================");
    } else {
        System.out.println("No member is currently logged in.");
    }
}

public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
    }


}