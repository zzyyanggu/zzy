package Redis;

import java.io.*;

public class JsontoString {
    //Read the JSON and convert it to a string
    public static String JsontoString(String path){
        String jsonStr = "";
        try{
            // Open and read the JSON file and convert it to a string
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            Reader reader = new InputStreamReader(new FileInputStream(file), "Utf-8");
            int temp = 0;
            StringBuffer buffer = new StringBuffer();
            while ((temp = reader.read()) != -1) {
                buffer.append((char) temp);
            }
            fileReader.close();
            reader.close();
            jsonStr = buffer.toString();
            return jsonStr;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
