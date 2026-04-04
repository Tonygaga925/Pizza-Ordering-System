package service;

import com.google.gson.reflect.TypeToken;
import model.Member;
import util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MemberManager {
    private String memberFilePath;
    private List<Member> members;
    private Member currentMember;
    
    public MemberManager(String memberFilePath) throws IOException {
        this.memberFilePath = memberFilePath;
        this.currentMember = null;
        loadMembers();
    }
    
    private void loadMembers() throws IOException {
        Type memberListType = new TypeToken<ArrayList<Member>>() {}.getType();
        members = JsonUtil.readFromFile(memberFilePath, memberListType);
        if (members == null) members = new ArrayList<>();
    }
    
    private void saveMembers() throws IOException {
        JsonUtil.writeToFile(memberFilePath, members);
    }
    
    public boolean register(String username, String password, String name, String phone) throws IOException {
        for (Member m : members) {
            if (m.getUsername().equals(username)) {
                return false;
            }
        }
        
        String id = String.format("M%03d", members.size() + 1);
        String registerDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        Member newMember = new Member(id, username, password, name, phone, registerDate);
        members.add(newMember);
        saveMembers();
        return true;
    }
    
    public boolean login(String username, String password) {
        for (Member m : members) {
            if (m.getUsername().equals(username) && m.getPassword().equals(password)) {
                currentMember = m;
                return true;
            }
        }
        return false;
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
    
    public Member getMemberById(String memberId) {
        for (Member m : members) {
            if (m.getId().equals(memberId)) {
                return m;
            }
        }
        return null;
    }
}