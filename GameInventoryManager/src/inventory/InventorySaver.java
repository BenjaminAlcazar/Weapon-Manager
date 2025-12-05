package inventory;

import com.fasterxml.jackson.databind.ObjectMapper;         				// JSON mapper
import com.fasterxml.jackson.databind.type.CollectionType;    				

import java.io.File;                                               			
import java.io.IOException;                                        			
import java.util.ArrayList;                                        			
import java.util.List;

public class InventorySaver {

    private static final String FILE_NAME = "weapons.json";          			

    // Save a list of weapons to JSON file
    public static void saveWeapons(List<Weapon> weapons) {
        ObjectMapper mapper = new ObjectMapper();                      		
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), weapons); 
        } catch (IOException e) {
            e.printStackTrace();                                       		
        }
    }

    // Load a list of weapons from JSON file
    public static List<Weapon> loadWeapons() {
        ObjectMapper mapper = new ObjectMapper();                      		
        File file = new File(FILE_NAME);                                 	
        if (!file.exists()) return new ArrayList<>();                     	
        try {
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, Weapon.class); 
            return mapper.readValue(file, listType);                      	
        } catch (IOException e) {
            e.printStackTrace();                                         	
            return new ArrayList<>();                                      	
        }
    }
}