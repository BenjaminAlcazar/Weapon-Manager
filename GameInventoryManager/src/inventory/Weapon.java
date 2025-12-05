package inventory;

import java.util.ArrayList;
import java.util.List;

public class Weapon {

    private String name;        
    private String type;        
    private int damage;         
    private String rarity;
    private List<String> tags = new ArrayList<>();
    
    public Weapon() {
    	
    }

    public Weapon(String name, String type, int damage, String rarity) {
        this.name = name;       
        this.type = type;       
        this.damage = damage;   
        this.rarity = rarity;   
    }

    //setting stuff
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public List<String> getTags() { return tags; }

    public void addTag(String tag) {
        if (!tags.contains(tag)) tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }
    public String toString() {
        String tagStr = tags.isEmpty() ? "" : " [" + String.join(", ", tags) + "]";
        return name + " (" + type + ", " + damage + ", " + rarity + ")" + tagStr;
    }
}