import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.*;

public class Explorer extends JFrame{
    private final int width = 440, height = 650;
    private int id;

    private SearchInstance search;

    private JTextField repository;
    private JTextArea searchLog;
    private JScrollPane searchLogScroll;
    private JMenuBar menuBar;
    private JButton startButton;
    private JButton stopButton;

    private JButton browseButton;
    private JFileChooser browseDirectoryChooser;

    private JCheckBox[] searchParams;
    private JTextField depth;


    private void initializeComponents(){
        initializeButtons();
        createMenu();

        Box container = Box.createVerticalBox();
        JPanel upperBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel middleBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel lowerBox = new JPanel(new FlowLayout(FlowLayout.LEFT));

        repository = new JTextField(30);
        repository.setFont(repository.getFont().deriveFont(Font.PLAIN, 13f));
        repository.setMaximumSize(repository.getPreferredSize());
        upperBox.add(repository);
        upperBox.add(browseButton);
        upperBox.add(startButton);
        upperBox.add(stopButton);
        container.add(upperBox);
        searchLog = new JTextArea(20,30);
        searchLog.setMaximumSize(searchLog.getPreferredSize());
        searchLog.setEditable(false);
        searchLogScroll = new JScrollPane(searchLog);
        searchLogScroll.setMaximumSize(searchLog.getPreferredSize());

        Box checkBoxesBox = Box.createVerticalBox();
        middleBox.add(new JLabel("Search parameters:"));
        container.add(middleBox);
        JLabel[] jlabels = {new JLabel("Files"), new JLabel("Subdirectories"),new JLabel("Search in subdirectories")};
        JPanel[] jpanels = {new JPanel(new FlowLayout(FlowLayout.LEFT)),new JPanel(new FlowLayout(FlowLayout.LEFT)),new JPanel(new FlowLayout(FlowLayout.LEFT))};

        for(int i =2; i >=0; i--){
            jpanels[i].add(searchParams[i]);
            jpanels[i].add(jlabels[i]);
            checkBoxesBox.add(jpanels[i]);
        }
        jpanels[2].add(depth);
        middleBox.add(checkBoxesBox);

        lowerBox.add(new JLabel("Explorer-"+ id +" log:"));
        lowerBox.add(searchLogScroll);

        container.add(lowerBox);
        container.add(Box.createVerticalGlue());


        this.add(container);
    }
    private void initializeButtons(){
        browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseAction());
        startButton = new JButton("Start search");
        startButton.addActionListener(new SearchAction());
        stopButton = new JButton("Stop search");
        stopButton.addActionListener(new StopAction());

        searchParams = new JCheckBox[]{new JCheckBox(), new JCheckBox(), new JCheckBox()};
        searchParams[2].addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(searchParams[2].isSelected()){
                    depth.setEditable(true);
                } else
                    depth.setEditable(false);
            }
        });
        depth = new JTextField(3);
        depth.setMaximumSize(depth.getPreferredSize());
        depth.setEditable(false);
        depth.setText("1");
    }
    private void createMenu(){
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu options = new JMenu("Options");
        JMenuItem stopThread = new JMenuItem("Stop Explorer-"+id);
        options.add(stopThread);
        stopThread.addActionListener(new StopAction());
        menuBar.add(options);
    }


    public Explorer(int i){
        super("Explorer number " + i);
        id = i;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        pack();
        setSize(width, height);

        setVisible(true);
        setResizable(false);
    }


    class SearchAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            search = new SearchInstance();
            try {
                search.start();
                logMessage("Search started successfully.");
            } catch (Exception exception){
                logMessage(exception.toString());
            }
        }
    }
    class StopAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(search!=null) search.interrupt();
            else logMessage("Trying to interrupt inactive Explorer");
        }
    }
    class BrowseAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            browseDirectoryChooser = new JFileChooser();
            browseDirectoryChooser.setDialogTitle("Choose directory");
            browseDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            browseDirectoryChooser.setAcceptAllFileFilterUsed(false);
            if (browseDirectoryChooser.showOpenDialog(Explorer.this) == JFileChooser.APPROVE_OPTION) {
                repository.setText(browseDirectoryChooser.getSelectedFile().toString());
            }
        }
    }

    class SearchInstance extends Thread{
        @Override
        public void run(){
            long start = System.currentTimeMillis(),finish = 0;
            try {
                Thread.sleep(2000);
                finish = System.currentTimeMillis() - start;
                logMessage(Explorer.this.getName()+" has finished search ("+ (float)(finish/1000.f)+" s)");
            }
            catch (InterruptedException e) {
                finish = System.currentTimeMillis() - start;
                logMessage(Explorer.this.getName()+" interrupted ("+ (float)(finish/1000.f)+" s)");
            }
        }

    }
    public void logMessage(String msg){
        searchLog.append("[" +LocalTime.now().toString().substring(0,11) +"]: "+msg+"\n");
    }
    public String getName(){
        return "Explorer-"+id;
    }

}
