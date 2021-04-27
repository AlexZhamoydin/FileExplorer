import java.util.ArrayList;

public class FileExplorer {
    public static void main(String[] args) {
        ArrayList<Explorer> explorers = new ArrayList<>();
        for(int i =0; i < 2; i++){
            explorers.add(new Explorer(i));
        }

    }
}
