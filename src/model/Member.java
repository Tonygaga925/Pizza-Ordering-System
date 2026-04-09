package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Member {
    private String id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private int points;
    private String level;
    private String registerDate;
    private static final int VIP_THRESHOLD = 2000;
    private static final String normalString = "Normal";
    private static final String vipString = "VIP";

    private transient MemberState state;
    
    // For Gson deserialization
    public Member() {}
    
    // Constructor for NEW members
    public Member(String id, String username, String password, String name, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.points = 0;
        this.level = normalString;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.registerDate = LocalDateTime.now().format(formatter);
        initState();
    }
    
    public void initState() {
        if (points >= VIP_THRESHOLD) {
            this.state = new VIPState();
            this.level = vipString;
        } else {
            this.state = new NormalState();
            this.level = normalString;
        }
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { 
        this.points = points;
    }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public String getRegisterDate() { return registerDate; } 
    public void setRegisterDate(String registerDate) { this.registerDate = registerDate; } 
    
    public MemberState getState() { return state; }
    public void setState(MemberState state) { this.state = state; }
    
    // Business methods
    public void addPoints(int pointsEarned) {
        state.addPoints(this, pointsEarned);
        if (state instanceof VIPState) {
            this.level = vipString;
        } else {
            this.level = normalString;
        }
    }
    
    public double getDiscount() {
        return state.getDiscount();
    }
    
    public String getLevelDisplay() {
        return state.getLevelName() + (getDiscount() > 0 ? " (" + (int)(getDiscount() * 100) + "% off)" : "");
    }
    
    public int getPointsToNextLevel() {
        if (state instanceof NormalState) {
            return Math.max(0, VIP_THRESHOLD - points);
        }
        return 0;
    }
}