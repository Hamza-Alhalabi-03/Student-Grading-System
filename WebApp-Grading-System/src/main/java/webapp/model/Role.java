package webapp.model;

public enum Role {
    ADMIN("admin"),
    STUDENT("student"),
    INSTRUCTOR("instructor");

    private String role;

    Role(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
