import javax.swing.JFrame;
public class Main {
    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int bordWidth = columnCount * tileSize;
        int bordHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        //ne creem basically frame-ul sa vedem jocul
        //frame.setVisible(true);
        frame.setSize(bordWidth, bordHeight);
        //apare la centrul ecranului nostru jocul
        frame.setLocationRelativeTo(null);
        //nu vrem ca playerul sa dea resize la window
        frame.setResizable(false);
        //terminate if x pressed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        //adding the pannel to the window
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        //Dacă nu apelezi requestFocus(), chiar dacă ai implementat
        // KeyListener pe pacmanGame, tastatura poate să nu fie „ascultată”
        // de această componentă.
        frame.setVisible(true);
        pacmanGame.requestFocusInWindow();
    }
}
