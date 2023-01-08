package logParser;
class User {
    private final String id;
    private final String name;
    private final String age;
    private final String email;

    public User(String id, String name, String age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }


    public User() {
		this.id = "";
		this.name = "";
		this.age = "";
		this.email = "";
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + ", email=" + email + "]";
	}


	public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }
}