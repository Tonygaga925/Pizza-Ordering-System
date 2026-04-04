package model;

public class Member {
    private String id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String registerDate;
    
    public Member(String id, String username, String password, String name, 
                  String phone, String registerDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.registerDate = registerDate;
    }
    
    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRegisterDate() { return registerDate; }
}