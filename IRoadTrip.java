import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IRoadTrip {


    public IRoadTrip (String [] args) {
        // Replace with your code
        if (args.length != 3) {
            System.err.println("ERROR: not all the files were passed");
        }
        String borderFile = args[0];
        String capDistFile = args[1];
        String stateNameFile = args[2];

        try {

            BufferedReader borders = new BufferedReader(new FileReader(borderFile));
            BufferedReader capDist = new BufferedReader(new FileReader(capDistFile));
            BufferedReader stateName = new BufferedReader(new FileReader(stateNameFile));

        } catch(IOException e) {
            System.err.println("ERROR: cant read these files");
        }
    }


    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

}

