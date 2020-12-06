package Redis;

import java.util.*;

public class Main {
    public static void main(String[] arg) throws Exception {
        //add file listener
        JedisTest jt = new JedisTest();
        //get hashmap
        HashMap<String, HashMap> actionMap = jt.getAction();
        HashMap<String, HashMap> counterMap = jt.getCounter();
        HashMap<String, String> action, counter;
        //set listening
        Monitor mon = new Monitor(500);
        //Add a listener to target folder
        mon.addMonitor("src/main/resources", new Listener(jt));
        mon.Start();
        //Display menu
        System.out.println("1.add count\n" +
                "2.reduce count\n" +
                "3.show change in the period\n" +
                "4.show the total number\n" +
                "5.show list\n6.show set\n" +
                "7.show z-set\n8.quit");
        System.out.println("Please input num:");
        Scanner input = new Scanner(System.in);
        String choice = input.nextLine();

        while (true) {
            switch (choice) {
                //Show the number of new people
                case "1": {
                    action = actionMap.get("ADD_CNT");
                    String desc = (String) action.get("describe");
                    String oprname = (String) action.get("operation");
                    counter = counterMap.get(oprname);
                    String temp = (String) counter.get("value");
                    System.out.println(desc);
                    jt.AddCnt(temp);
                    jt.ShowCnt();
                    System.out.println("\n");
                    break;
                }
                //Show the reduced number of people
                case "2": {
                    action = actionMap.get("DELETE_CNT");
                    String desc = (String) action.get("describe");
                    String oprname = (String) action.get("operation");
                    counter = counterMap.get(oprname);
                    String temp = (String) counter.get("value");
                    System.out.println(desc);
                    jt.DeleteCnt(temp);
                    jt.ShowCnt();
                    System.out.println("\n");
                    break;
                }
                //Shows how many people change over time
                case "3": {
                    action=actionMap.get("SHOW_FREQ");
                    String desc = (String) action.get("describe");
                    String oprname=(String)action.get("show");
                    counter=counterMap.get(oprname);
                    String temp= (String) counter.get("field");
                    System.out.println(desc);
                    jt.ShowFreq(temp);
                    System.out.println("\n");
                    break;
                }
                //Show total number of people
                case "4": {
                    jt.ShowCnt();
                    System.out.println("\n");
                    break;
                }
                //Show list
                case "5": {
                    jt.ShowList("enterList");
                    jt.ShowList("leaveList");
                    System.out.println("\n");
                    break;
                }
                //Show set
                case "6": {
                    jt.ShowSet("enterSet");
                    jt.ShowSet("leaveSet");
                    System.out.println("\n");
                    break;
                }
                //Show z-set
                case "7": {
                    jt.ShowZ_set("enterZset");
                    jt.ShowZ_set("leaveZset");
                    System.out.println("\n");
                    break;
                }
                //Quit
                case "8": {
                    System.exit(0);
                    break;
                }
                default:{
                    System.out.println("Input error !Please enter again!");
                    break;
                }
            }
            System.out.println("1.add count\n" +
                    "2.reduce count\n" +
                    "3.show change in the period\n" +
                    "4.show the total number\n" +
                    "5.show list\n6.show set\n" +
                    "7.show z-set\n8.quit");
            System.out.println("Please input num:");
            choice = input.nextLine();
        }
    }
}
